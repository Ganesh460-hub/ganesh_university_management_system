package com.ums.ums_project.service;

import com.ums.ums_project.model.Student;
import com.ums.ums_project.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    // Constructor injection
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Create or Update student
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get student by collegeId
    public Student getStudentByCollegeId(String collegeId) {
        return studentRepository.findById(collegeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid collegeId: " + collegeId));
    }

    // Delete student by collegeId
    public void deleteStudent(String collegeId) {
        studentRepository.deleteById(collegeId);
    }


    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }
}
