package com.ums.ums_project.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "fee_structure")
@Data
public class FeeStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false)
    private Double totalFee;

    @Column(nullable = false)
    private Double tuitionFee;

    @Column(nullable = false)
    private Double examFee;

    @Column(nullable = false)
    private Double otherCharges;

    // Default constructor
    public FeeStructure() {}

    // Constructor for easy creation
    public FeeStructure(String department, Integer semester, Double totalFee,
                        Double tuitionFee, Double examFee, Double otherCharges) {
        this.department = department;
        this.semester = semester;
        this.totalFee = totalFee;
        this.tuitionFee = tuitionFee;
        this.examFee = examFee;
        this.otherCharges = otherCharges;
    }
}