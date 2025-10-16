package com.ums.ums_project.repository;

import com.ums.ums_project.model.Attendance;
import com.ums.ums_project.model.Student;
import com.ums.ums_project.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(Student student);

    List<Attendance> findByStudentOrderByDateAsc(Student student);

    List<Attendance> findByStudentAndDate(Student student, LocalDate date);

    List<Attendance> findByDateBetween(LocalDate start, LocalDate end);

    List<Attendance> findByStudentAndDateAndTeacher(Student student, LocalDate date, Teacher teacher);

    List<Attendance> findByDateAndTeacher(LocalDate date, Teacher teacher);


    List<Attendance> findByStudentCollegeIdAndTeacherEmployeeId(String collegeId, String teacherEmployeeId);

    @Query("SELECT DISTINCT a.date FROM Attendance a WHERE a.teacher = :teacher ORDER BY a.date DESC")
    List<LocalDate> findDistinctDateByTeacherOrderByDateDesc(@Param("teacher") Teacher teacher);
}


