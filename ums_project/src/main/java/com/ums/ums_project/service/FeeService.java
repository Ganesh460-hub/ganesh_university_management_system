package com.ums.ums_project.service;

import com.ums.ums_project.model.FeeStructure;
import com.ums.ums_project.model.StudentFee;
import com.ums.ums_project.model.FeeTransaction;
import com.ums.ums_project.repository.FeeStructureRepository;
import com.ums.ums_project.repository.StudentFeeRepository;
import com.ums.ums_project.repository.FeeTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeeService {

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Autowired
    private StudentFeeRepository studentFeeRepository;

    @Autowired
    private FeeTransactionRepository feeTransactionRepository;


    public void initializeFeeStructure() {
        for (int sem = 1; sem <= 8; sem++) {
            FeeStructure feeStructure = new FeeStructure(
                    "CSE",
                    sem,
                    1000.0, // $1000 instead of ₹79,000
                    750.0,  // $750 tuition
                    150.0,  // $150 exam fee
                    100.0   // $100 other charges
            );

            if (feeStructureRepository.findByDepartmentAndSemester("CSE", sem).isEmpty()) {
                feeStructureRepository.save(feeStructure);
            }
        }
    }

    // Get or create student fee record
    @Transactional
    public StudentFee getOrCreateStudentFee(String studentId, Integer semester, String department) {
        Optional<StudentFee> existingFee = studentFeeRepository.findByStudentIdAndSemester(studentId, semester);

        if (existingFee.isPresent()) {
            return existingFee.get();
        }

        // Get fee structure for this department and semester
        FeeStructure feeStructure = feeStructureRepository.findByDepartmentAndSemester(department, semester)
                .orElseThrow(() -> new RuntimeException("Fee structure not found for department: " + department + " semester: " + semester));

        // Create new student fee record
        StudentFee studentFee = new StudentFee();
        studentFee.setStudentId(studentId);
        studentFee.setSemester(semester);
        studentFee.setTotalFee(feeStructure.getTotalFee());
        studentFee.setPaidAmount(0.0);
        studentFee.setDueAmount(feeStructure.getTotalFee());
        studentFee.setStatus("PENDING");

        return studentFeeRepository.save(studentFee);
    }

    // Get all fee records for a student
    public List<StudentFee> getStudentFees(String studentId) {
        return studentFeeRepository.findByStudentId(studentId);
    }

    // Create a payment transaction
    @Transactional
    public FeeTransaction createPaymentTransaction(String studentId, Integer semester, Double amount,
                                                   String paymentMethod, String stripePaymentId, String receiptUrl) {
        StudentFee studentFee = studentFeeRepository.findByStudentIdAndSemester(studentId, semester)
                .orElseThrow(() -> new RuntimeException("Student fee record not found"));

        // FIXED: Update the paid amount and due amount
        double newPaidAmount = studentFee.getPaidAmount() + amount;
        double newDueAmount = studentFee.getDueAmount() - amount;

        // Validate that payment doesn't exceed due amount
        if (amount > studentFee.getDueAmount()) {
            throw new RuntimeException("Payment amount exceeds due amount");
        }

        studentFee.setPaidAmount(newPaidAmount);
        studentFee.setDueAmount(newDueAmount);

        // Update status based on payment
        if (newDueAmount <= 0) {
            studentFee.setStatus("PAID");
        } else if (newPaidAmount > 0) {
            studentFee.setStatus("PARTIAL");
        }

        // Create transaction
        FeeTransaction transaction = new FeeTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAmount(amount);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setStripePaymentId(stripePaymentId);
        transaction.setReceiptUrl(receiptUrl);
        transaction.setStatus("SUCCESS");
        transaction.setPaymentDate(LocalDateTime.now());

        // Add transaction to student fee
        studentFee.addTransaction(transaction);

        // Save both - transaction first, then student fee
        feeTransactionRepository.save(transaction);
        StudentFee updatedStudentFee = studentFeeRepository.save(studentFee);

        System.out.println("Payment processed - Student: " + studentId +
                ", Semester: " + semester +
                ", Amount: " + amount +
                ", New Paid: " + updatedStudentFee.getPaidAmount() +
                ", New Due: " + updatedStudentFee.getDueAmount());

        return transaction;
    }
    // Get payment history for student
    public List<FeeTransaction> getPaymentHistory(String studentId) {
        return feeTransactionRepository.findByStudentFee_StudentId(studentId);
    }

    public List<FeeTransaction> getPaymentHistoryBySemester(String studentId, Integer semester) {
        return feeTransactionRepository.findByStudentIdAndSemester(studentId, semester);
    }

    // Get total paid amount for student
    public Double getTotalPaidAmount(String studentId) {
        List<FeeTransaction> transactions = getPaymentHistory(studentId);
        return transactions.stream()
                .mapToDouble(FeeTransaction::getAmount)
                .sum();
    }

    // Get due amount for specific semester
    public Double getDueAmountForSemester(String studentId, Integer semester) {
        Optional<StudentFee> studentFee = studentFeeRepository.findByStudentIdAndSemester(studentId, semester);
        return studentFee.map(StudentFee::getDueAmount).orElse(0.0);
    }
}