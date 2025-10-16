package com.ums.ums_project.service;

import com.ums.ums_project.model.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {



    // Return a list of summary maps (one map per student) for teacher dashboard/report
    List<Map<String, Object>> getAttendanceSummary();

    // Monthly attendance for a specific student (collegeId)
    Map<String, Double> getMonthlyAttendanceForStudent(String collegeId);

    // Attendance history for a specific student (collegeId)
    List<Attendance> getAttendanceHistory(String collegeId);

    // Get a single attendance record by ID
    Attendance getAttendanceById(Long id);

    // Update attendance status
    void updateAttendance(Long id, boolean present);

    // Returns a map of collegeId -> present/absent for a given date
    Map<String, Boolean> getAttendanceByDate(LocalDate date);

    // Attendance records for a month
    List<Map<String, Object>> getAttendanceByMonth(int month);

    // updated: include teacherEmployeeId
    void saveAttendance(Map<String, Boolean> attendanceData, LocalDate date, String teacherEmployeeId);

    //  Get attendance records for a specific date and teacher
    List<Attendance> getAttendanceByDateAndTeacher(LocalDate date, String teacherEmployeeId);

    //  Update specific attendance record
    void updateAttendanceStatus(Long attendanceId, boolean present);

    //  Get all dates when teacher marked attendance
    List<LocalDate> getAttendanceDatesByTeacher(String teacherEmployeeId);


    Map<String, Boolean> getAttendanceMapByDateAndTeacher(LocalDate date, String teacherEmployeeId);


    Map<String, List<Map<String, Object>>> getAttendanceSummaryByPercentageRanges(String teacherEmployeeId);

    //  Calculate attendance percentage for a student
    Double calculateAttendancePercentage(String collegeId);

    // NGet all students with their attendance percentages
    List<Map<String, Object>> getAllStudentsWithAttendancePercentages();

    // Get attendance summary for THIS TEACHER only
    Map<String, List<Map<String, Object>>> getAttendanceSummaryByPercentageRangesForTeacher(String teacherEmployeeId);

    //  Calculate attendance percentage for THIS TEACHER only
    Double calculateAttendancePercentageForTeacher(String collegeId, String teacherEmployeeId);

}
