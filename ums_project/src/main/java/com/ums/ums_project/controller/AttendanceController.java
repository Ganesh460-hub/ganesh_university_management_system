package com.ums.ums_project.controller;

import com.ums.ums_project.model.Attendance;
import com.ums.ums_project.service.AttendanceService;
import com.ums.ums_project.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final StudentService studentService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService, StudentService studentService){
        this.attendanceService = attendanceService;
        this.studentService = studentService;
    }

    // Show attendance summary
    @GetMapping("/summary")
    public String viewSummary(Model model){
        List<Map<String, Object>> summaryList = attendanceService.getAttendanceSummary();
        model.addAttribute("summaryList", summaryList);
        return "attendance/summary";
    }

    // Show mark attendance page
    @GetMapping("/mark")
    public String markAttendance(Model model){
        model.addAttribute("students", studentService.getAllStudents());
        return "attendance/mark";
    }

    // Show attendance history for a student
    @GetMapping("/student/{collegeId}/history")
    public String viewStudentHistory(@PathVariable String collegeId, Model model){
        List<Attendance> history = attendanceService.getAttendanceHistory(collegeId);
        model.addAttribute("history", history);
        return "attendance/history"; // points to history.html
    }

    // Show edit form for a specific attendance record
    @GetMapping("/edit/{attendanceId}")
    public String editAttendance(@PathVariable Long attendanceId, Model model){
        Attendance record = attendanceService.getAttendanceById(attendanceId);
        model.addAttribute("record", record);
        return "attendance/edit"; // points to edit.html
    }

    //  Handle form submission for updating attendance
    @PostMapping("/edit/{attendanceId}")
    public String updateAttendance(@PathVariable Long attendanceId,
                                   @RequestParam("present") boolean present,
                                   @RequestParam("collegeId") String collegeId){  // added collegeId
        attendanceService.updateAttendance(attendanceId, present);
        return "redirect:/attendance/student/" + collegeId + "/history"; // redirect back to history
    }


}
