package com.ums.ums_project.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "subject_results")
public class SubjectResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_college_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_employee_id")
    private Teacher teacher;

    private String subject;
    private Integer semester;
    private Integer marks;
    private Integer totalMarks = 100;
    private boolean passed;
    private String grade;
    private Double gradePoint;
    private LocalDate resultDate;

    // Constructors
    public SubjectResult() {}

    public SubjectResult(Student student, Teacher teacher, String subject, Integer semester,
                         Integer marks,  LocalDate resultDate) {
        this.student = student;
        this.teacher = teacher;
        this.subject = subject;
        this.semester = semester;
        this.marks = marks;
        this.totalMarks = 100;
        this.resultDate = resultDate;
        calculateGradeAndStatus();
    }

    // Auto-calculate grade and status based on marks
    public void calculateGradeAndStatus() {
        if (marks == null) {
            this.grade = "N/A";
            this.gradePoint = 0.0;
            this.passed = false;
            return;
        }

        if (marks >= 90) {
            this.grade = "A";
            this.gradePoint = 10.0;
            this.passed = true;
        } else if (marks >= 80) {
            this.grade = "B";
            this.gradePoint = 9.0;
            this.passed = true;
        } else if (marks >= 70) {
            this.grade = "C";
            this.gradePoint = 8.0;
            this.passed = true;
        } else if (marks >= 60) {
            this.grade = "D";
            this.gradePoint = 7.0;
            this.passed = true;
        } else if (marks >= 40) {
            this.grade = "E";
            this.gradePoint = 6.0;
            this.passed = true;
        } else {
            this.grade = "F";
            this.gradePoint = 0.0;
            this.passed = false;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) {
        this.marks = marks;
        calculateGradeAndStatus();
    }

    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Double getGradePoint() { return gradePoint; }
    public void setGradePoint(Double gradePoint) { this.gradePoint = gradePoint; }


    public LocalDate getResultDate() { return resultDate; }
    public void setResultDate(LocalDate resultDate) { this.resultDate = resultDate; }
}