package com.ums.ums_project.repository;

import com.ums.ums_project.model.FeeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeTransactionRepository extends JpaRepository<FeeTransaction, Long> {


    List<FeeTransaction> findByStudentFee_StudentId(String studentId);


    @Query("SELECT ft FROM FeeTransaction ft WHERE ft.studentFee.studentId = :studentId AND ft.studentFee.semester = :semester")
    List<FeeTransaction> findByStudentIdAndSemester(@Param("studentId") String studentId, @Param("semester") Integer semester);
}