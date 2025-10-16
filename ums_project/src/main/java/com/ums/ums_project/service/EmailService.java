package com.ums.ums_project.service;

import com.ums.ums_project.model.FeeTransaction;
import com.ums.ums_project.model.Student;
import com.ums.ums_project.model.StudentFee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPaymentReceipt(Student student, FeeTransaction transaction, StudentFee studentFee) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(student.getEmail());
            helper.setSubject("Payment Receipt - Semester " + studentFee.getSemester());

            // Create email content
            String emailContent = buildPaymentEmailContent(student, transaction, studentFee);
            helper.setText(emailContent, true); // true = HTML content

            mailSender.send(message);

            System.out.println("Payment receipt email sent to: " + student.getEmail());

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // Don't throw exception - payment should still succeed even if email fails
        }
    }

    private String buildPaymentEmailContent(Student student, FeeTransaction transaction, StudentFee studentFee) {
        Context context = new Context();

        // Format date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        String paymentDate = transaction.getPaymentDate().format(formatter);

        // Add data to template context
        context.setVariable("student", student);
        context.setVariable("transaction", transaction);
        context.setVariable("studentFee", studentFee);
        context.setVariable("paymentDate", paymentDate);


        return templateEngine.process("email/payment-receipt", context);
    }

    // Simple text-based email (fallback)
    private String buildSimpleEmailContent(Student student, FeeTransaction transaction, StudentFee studentFee) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .header { background: #4f6df5; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .receipt { border: 2px solid #4f6df5; padding: 15px; margin: 15px 0; }
                    .footer { background: #f5f5f5; padding: 15px; text-align: center; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2>University Management System</h2>
                    <h3>Payment Receipt</h3>
                </div>
                
                <div class="content">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your payment has been processed successfully. Here are the details:</p>
                    
                    <div class="receipt">
                        <h4>Payment Details</h4>
                        <p><strong>Transaction ID:</strong> %s</p>
                        <p><strong>Amount Paid:</strong> $%.2f</p>
                        <p><strong>Payment Date:</strong> %s</p>
                        <p><strong>Semester:</strong> %d</p>
                        <p><strong>Payment Method:</strong> %s</p>
                    </div>
                    
                    <div class="receipt">
                        <h4>Fee Summary</h4>
                        <p><strong>Total Fee:</strong> $%.2f</p>
                        <p><strong>Previously Paid:</strong> $%.2f</p>
                        <p><strong>Remaining Balance:</strong> $%.2f</p>
                    </div>
                    
                    <p>This is an automated receipt. Please keep it for your records.</p>
                </div>
                
                <div class="footer">
                    <p>University Management System<br>
                    Contact: admin@university.edu</p>
                </div>
            </body>
            </html>
            """.formatted(
                student.getName(),
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getPaymentDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
                studentFee.getSemester(),
                transaction.getPaymentMethod(),
                studentFee.getTotalFee(),
                studentFee.getPaidAmount() - transaction.getAmount(), // previous paid amount
                studentFee.getDueAmount()
        );
    }
}