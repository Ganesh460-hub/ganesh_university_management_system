package com.ums.ums_project.repository;

import com.ums.ums_project.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
    Teacher findByEmployeeId(String employeeId);
    Teacher findByEmail(String email);

    List<Teacher> findByDepartment(String department);

    Long countByDepartment(String department);
}
