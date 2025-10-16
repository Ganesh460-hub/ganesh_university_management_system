package com.ums.ums_project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_transaction")
@Data
public class FeeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private String paymentMethod = "card";

    @Column(nullable = false)
    private String status = "SUCCESS";

    private String stripePaymentId;
    private String receiptUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_id")
    private StudentFee studentFee;


    @PrePersist
    public void setPaymentDate() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
    }


    public void setStudentFee(StudentFee studentFee) {
        this.studentFee = studentFee;
    }


    public Double getAmount() {
        return this.amount;
    }


    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setStripePaymentId(String stripePaymentId) {
        this.stripePaymentId = stripePaymentId;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}