package com.ums.ums_project.repository;

import com.ums.ums_project.model.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    Optional<FeeStructure> findByDepartmentAndSemester(String department, Integer semester);
}