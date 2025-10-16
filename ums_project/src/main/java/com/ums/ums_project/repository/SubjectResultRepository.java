package com.ums.ums_project.repository;

import com.ums.ums_project.model.Student;
import com.ums.ums_project.model.SubjectResult;
import com.ums.ums_project.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectResultRepository extends JpaRepository<SubjectResult, Long> {

    // Find results by student
    List<SubjectResult> findByStudent(Student student);

    // Find results by teacher
    List<SubjectResult> findByTeacher(Teacher teacher);

    // Find results by student and teacher
    List<SubjectResult> findByStudentAndTeacher(Student student, Teacher teacher);

    // Find results by student and subject
    List<SubjectResult> findByStudentAndSubject(Student student, String subject);

    // Find by student, teacher AND subject
    List<SubjectResult> findByStudentAndTeacherAndSubject(Student student, Teacher teacher, String subject);

    // Find by student and semester
    List<SubjectResult> findByStudentAndSemester(Student student, Integer semester);

    // Check if student has any failed subjects
    @Query("SELECT COUNT(sr) > 0 FROM SubjectResult sr WHERE sr.student = :student AND sr.passed = false")
    boolean hasFailedSubjects(@Param("student") Student student);


    @Query("SELECT COUNT(DISTINCT sr.teacher) FROM SubjectResult sr WHERE sr.student = :student AND sr.semester = :semester")
    Long countTeachersForStudentAndSemester(@Param("student") Student student, @Param("semester") Integer semester);

    @Query("SELECT DISTINCT sr.semester FROM SubjectResult sr WHERE sr.student = :student ORDER BY sr.semester")
    List<Integer> findAvailableSemestersByStudent(@Param("student") Student student);
}