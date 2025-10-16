package com.ums.ums_project.repository;

import com.ums.ums_project.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    Student findByEmail(String email);

    Student findByCollegeId(String collegeId);

    List<Student> findByDepartment(String department);

    List<Student> findByDepartmentOrderByCollegeId(String department);

}
