package com.ums.ums_project.controller;


import com.ums.ums_project.model.Attendance;
import com.ums.ums_project.model.Student;
import com.ums.ums_project.model.SubjectResult;
import com.ums.ums_project.service.AttendanceService;
import com.ums.ums_project.service.StudentService;

import com.ums.ums_project.service.SubjectResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;

    @Autowired
    public StudentController(StudentService studentService, AttendanceService attendanceService){
        this.studentService = studentService;
        this.attendanceService = attendanceService;
    }

    //  Student dashboard
    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Authentication auth) {
        String collegeId = auth.getName(); // logged-in collegeId
        Student student = studentService.getStudentByCollegeId(collegeId);
        model.addAttribute("student", student);
        return "student/dashboard";
    }

    //  List all students
    @GetMapping
    public String listStudents(Model model){
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "student/list";
    }

    //  Show form to add new student
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        return "student/create";
    }

    //  Save new student
    @PostMapping
    public String saveStudent(@ModelAttribute Student student){
        studentService.saveStudent(student);
        return "redirect:/student";
    }

    //  Show form to update student
    @GetMapping("/edit/{collegeId}")
    public String showUpdateForm(@PathVariable String collegeId, Model model){
        Student student = studentService.getStudentByCollegeId(collegeId);
        model.addAttribute("student", student);
        return "student/update";
    }

    //  Update student
    @PostMapping("/update/{collegeId}")
    public String updateStudent(@PathVariable String collegeId, @ModelAttribute Student student) {
        student.setCollegeId(collegeId);
        studentService.saveStudent(student);
        return "redirect:/student";
    }

    //  Delete student
    @GetMapping("/delete/{collegeId}")
    public String deleteStudent(@PathVariable String collegeId) {
        studentService.deleteStudent(collegeId);
        return "redirect:/student";
    }

    @PostMapping("/uploadPhoto")
    public String uploadPhoto(@RequestParam("photo") MultipartFile file, Authentication auth) throws IOException {
        String collegeId = auth.getName();
        Student student = studentService.getStudentByCollegeId(collegeId);

        if (!file.isEmpty()) {
            String fileName = collegeId + "_" + file.getOriginalFilename();
            Path path = Paths.get("src/main/resources/static/uploads/" + fileName);
            Files.write(path, file.getBytes());

            // FIX: Store only filename, not full path
            student.setPhoto(fileName); // ← CHANGE THIS LINE
            studentService.saveStudent(student);
        }
        return "redirect:/student/dashboard";
    }

    @GetMapping("/attendance")
    public String studentAttendance(Model model, Authentication auth) {
        String collegeId = auth.getName(); // logged-in student
        Map<String, Double> monthlyAttendance = attendanceService.getMonthlyAttendanceForStudent(collegeId);
        List<Attendance> history = attendanceService.getAttendanceHistory(collegeId);

        // Calculate overall attendance
        double overall = history.isEmpty() ? 0 :
                history.stream().filter(Attendance::isPresent).count() * 100.0 / history.size();

        model.addAttribute("monthlyAttendance", monthlyAttendance);
        model.addAttribute("overallAttendance", overall);

        return "student/attendance-dashboard";
    }


    @Autowired
    private SubjectResultService subjectResultService;

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication auth) {
        String collegeId = auth.getName();
        Student student = studentService.getStudentByCollegeId(collegeId);

        List<SubjectResult> subjectResults = subjectResultService.getResultsByStudent(collegeId);
        boolean hasFailedSubjects = subjectResultService.hasFailedSubjects(collegeId);

        model.addAttribute("student", student);
        model.addAttribute("subjectResults", subjectResults);
        model.addAttribute("hasFailedSubjects", hasFailedSubjects);

        return "student/profile";
    }



}
