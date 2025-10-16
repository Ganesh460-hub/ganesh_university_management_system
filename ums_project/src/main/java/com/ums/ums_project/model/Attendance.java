package com.ums.ums_project.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each attendance record belongs to a student
    @ManyToOne
    @JoinColumn(name = "student_college_id", referencedColumnName = "collegeId", nullable = false)
    private Student student;

    // Each attendance record is marked by a teacher
    @ManyToOne
    @JoinColumn(name = "teacher_employee_id", referencedColumnName = "employeeId", nullable = false)
    private Teacher teacher;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean present;

    // Constructors
    public Attendance() {}

    public Attendance(Student student, Teacher teacher, LocalDate date, boolean present) {
        this.student = student;
        this.teacher = teacher;
        this.date = date;
        this.present = present;
    }

    public Attendance(Student student, LocalDate date, boolean present) {
        this.student = student;
        this.date = date;
        this.present = present;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isPresent() { return present; }
    public void setPresent(boolean present) { this.present = present; }
}
