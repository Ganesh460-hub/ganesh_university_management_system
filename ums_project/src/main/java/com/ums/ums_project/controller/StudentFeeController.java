package com.ums.ums_project.controller;

import com.ums.ums_project.model.Student;
import com.ums.ums_project.model.StudentFee;
import com.ums.ums_project.model.FeeTransaction;
import com.ums.ums_project.repository.StudentRepository;
import com.ums.ums_project.service.EmailService;
import com.ums.ums_project.service.FeeService;
import com.ums.ums_project.service.StripeService;
import com.stripe.model.PaymentIntent;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class StudentFeeController {

    @Autowired
    private FeeService feeService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private StudentRepository studentRepository;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @Autowired
    private EmailService emailService;

    // Fee dashboard
    @GetMapping("/fees")
    public String showFeeDashboard(Authentication authentication, Model model) {
        String collegeId = authentication.getName();
        Student student = studentRepository.findByCollegeId(collegeId);

        if (student == null) {
            return "redirect:/login?error";
        }

        // Initialize fee structure if not exists
        feeService.initializeFeeStructure();

        // Get or create fee records for all semesters
        for (int sem = 1; sem <= 8; sem++) {
            feeService.getOrCreateStudentFee(collegeId, sem, student.getDepartment());
        }

        // Get all fee records
        List<StudentFee> studentFees = feeService.getStudentFees(collegeId);
        Double totalPaid = feeService.getTotalPaidAmount(collegeId);

        model.addAttribute("student", student);
        model.addAttribute("studentFees", studentFees);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("stripePublicKey", stripePublicKey);

        return "student/fee-dashboard";
    }

    // Payment page for specific semester
    @GetMapping("/fees/pay/{semester}")
    public String showPaymentPage(@PathVariable Integer semester, Authentication authentication, Model model) {
        String collegeId = authentication.getName();
        Student student = studentRepository.findByCollegeId(collegeId);

        StudentFee studentFee = feeService.getOrCreateStudentFee(collegeId, semester, student.getDepartment());

        model.addAttribute("student", student);
        model.addAttribute("studentFee", studentFee);
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("semester", semester);

        return "student/payment-page";
    }



    @PostMapping("/fees/create-payment-intent")
    @ResponseBody
    public Map<String, String> createPaymentIntent(@RequestBody Map<String, Object> data, Authentication authentication) {
        String collegeId = authentication.getName();
        Integer semester = (Integer) data.get("semester");

        // FIXED LINE: Handle both Integer and Long
        Number amountNumber = (Number) data.get("amount");
        Long amount = amountNumber.longValue(); // Amount in cents

        Map<String, String> response = new HashMap<>();

        try {
            Student student = studentRepository.findByCollegeId(collegeId);
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                    amount,
                    "usd",
                    "Fee payment for " + student.getName() + " - Semester " + semester
            );

            response.put("clientSecret", paymentIntent.getClientSecret());
            response.put("status", "success");

        } catch (StripeException e) {
            response.put("error", e.getMessage());
            response.put("status", "error");
        }

        return response;
    }
    // Handle successful payment
    @PostMapping("/fees/payment-success")
    @ResponseBody
    @Transactional
    public Map<String, String> handlePaymentSuccess(@RequestBody Map<String, Object> data, Authentication authentication) {
        String collegeId = authentication.getName();
        String paymentIntentId = (String) data.get("paymentIntentId");
        Integer semester = (Integer) data.get("semester");

        // FIXED LINE: Handle both Integer and Double
        Number amountNumber = (Number) data.get("amount");
        Double amount = amountNumber.doubleValue();

        Map<String, String> response = new HashMap<>();

        try {
            // Verify payment with Stripe
            PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                // Get student details
                Student student = studentRepository.findByCollegeId(collegeId);

                // Generate receipt URL
                String receiptUrl = generateReceiptUrl(paymentIntentId, collegeId);


                FeeTransaction transaction = null;
                try {
                    transaction = feeService.createPaymentTransaction(
                            collegeId,
                            semester,
                            amount,
                            "card",
                            paymentIntentId,
                            receiptUrl
                    );
                } catch (Exception transactionError) {
                    System.err.println("Transaction creation failed: " + transactionError.getMessage());
                    transactionError.printStackTrace();
                    throw new RuntimeException("Failed to create transaction record: " + transactionError.getMessage());
                }

                // Get updated student fee record
                StudentFee studentFee = feeService.getOrCreateStudentFee(collegeId, semester, student.getDepartment());

                // FIXED: Don't fail payment if email fails
                try {
                    emailService.sendPaymentReceipt(student, transaction, studentFee);
                } catch (Exception emailError) {
                    System.out.println("Email sending failed (but payment succeeded): " + emailError.getMessage());
                    // Continue - don't fail the payment because email failed
                }

                response.put("status", "success");
                response.put("transactionId", transaction != null ? transaction.getTransactionId() : "N/A");
                response.put("message", "Payment completed successfully!");
            } else {
                response.put("status", "error");
                response.put("message", "Payment not completed. Status: " + paymentIntent.getStatus());
            }

        } catch (Exception e) {
            // FIXED: Better error logging
            System.err.println("Payment success processing error: " + e.getMessage());
            e.printStackTrace();

            response.put("status", "error");
            response.put("message", "Payment verification failed: " + e.getMessage());
        }

        return response;
    }
    // Helper method to generate receipt URL
    private String generateReceiptUrl(String paymentIntentId, String collegeId) {

        return "/student/fees/receipt/" + paymentIntentId;
    }

    // Payment history
    @GetMapping("/fees/history")
    public String showPaymentHistory(Authentication authentication, Model model) {
        String collegeId = authentication.getName();
        Student student = studentRepository.findByCollegeId(collegeId);
        List<FeeTransaction> paymentHistory = feeService.getPaymentHistory(collegeId);

        model.addAttribute("student", student);
        model.addAttribute("paymentHistory", paymentHistory);

        return "student/payment-history";
    }
}