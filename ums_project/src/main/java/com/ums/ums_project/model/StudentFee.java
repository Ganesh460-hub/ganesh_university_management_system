package com.ums.ums_project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_fee")
@Data
public class StudentFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false)
    private Double totalFee = 0.0;

    @Column(nullable = false)
    private Double paidAmount = 0.0;

    @Column(nullable = false)
    private Double dueAmount = 0.0;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, PARTIAL_PAID, FULL_PAID

    @OneToMany(mappedBy = "studentFee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FeeTransaction> transactions = new ArrayList<>();

    // Helper method to calculate due amount
    public void calculateDueAmount() {
        this.dueAmount = this.totalFee - this.paidAmount;
        updateStatus();
    }

    // Helper method to update status
    public void updateStatus() {
        if (paidAmount <= 0) {
            this.status = "PENDING";
        } else if (paidAmount < totalFee) {
            this.status = "PARTIAL_PAID";
        } else {
            this.status = "FULL_PAID";
        }
    }

    // Add transaction and update amounts
    public void addTransaction(FeeTransaction transaction) {
        transaction.setStudentFee(this);
        this.transactions.add(transaction);
        this.paidAmount += transaction.getAmount();
        calculateDueAmount();
    }
}