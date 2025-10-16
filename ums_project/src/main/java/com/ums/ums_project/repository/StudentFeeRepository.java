package com.ums.ums_project.repository;

import com.ums.ums_project.model.StudentFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeeRepository extends JpaRepository<StudentFee, Long> {
    Optional<StudentFee> findByStudentIdAndSemester(String studentId, Integer semester);
    List<StudentFee> findByStudentId(String studentId);
}