package com.ums.ums_project.controller;


import com.ums.ums_project.model.Attendance;
import com.ums.ums_project.model.Student;
import com.ums.ums_project.model.SubjectResult;
import com.ums.ums_project.model.Teacher;
import com.ums.ums_project.repository.AttendanceRepository;
import com.ums.ums_project.repository.TeacherRepository;
import com.ums.ums_project.service.AttendanceService;
import com.ums.ums_project.service.StudentService;
import com.ums.ums_project.service.SubjectResultService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final TeacherRepository teacherRepository;
    private final AttendanceRepository attendanceRepository;
    private final SubjectResultService subjectResultService;

    @Autowired
    public TeacherController(StudentService studentService,
                             AttendanceService attendanceService,
                             TeacherRepository teacherRepository, AttendanceRepository attendanceRepository, SubjectResultService subjectResultService) {
        this.studentService = studentService;
        this.attendanceService = attendanceService;
        this.teacherRepository = teacherRepository;
        this.attendanceRepository = attendanceRepository;
        this.subjectResultService = subjectResultService;
    }

    //  Teacher dashboard with teacher info
    @GetMapping("/dashboard")
    public String showDashboard(Authentication auth, Model model) {
        String employeeId = auth.getName();  // Spring Security username
        Teacher teacher = teacherRepository.findByEmployeeId(employeeId);

        if (teacher == null) {
            throw new RuntimeException("Teacher not found for employeeId: " + employeeId);
        }

        model.addAttribute("teacher", teacher);
        return "teacher/dashboard";
    }


    //  Submit attendance
    @PostMapping("/attendance")
    public String submitAttendance(
            @RequestParam Map<String, Boolean> attendanceData,
            @RequestParam("attendanceDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication auth,
            Model model) {

        String employeeId = auth.getName(); // get employee ID from auth
        attendanceService.saveAttendance(attendanceData, date, employeeId);

        model.addAttribute("successMessage", "✅ Attendance posted successfully!");
        List<Student> students = studentService.getAllStudents();
        Map<String, Boolean> attendanceMap = attendanceService.getAttendanceByDate(date);
        model.addAttribute("students", students);
        model.addAttribute("attendanceMap", attendanceMap);
        model.addAttribute("selectedDate", date);

        return "teacher/mark";
    }



    //  Show attendance form
    @GetMapping("/attendance")
    public String showAttendance(Model model,
                                 @RequestParam(value="date", required=false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 Authentication auth) {

        if (date == null) date = LocalDate.now();

        // Get current teacher
        String employeeId = auth.getName();
        Teacher teacher = teacherRepository.findByEmployeeId(employeeId);

        if (teacher == null) {
            throw new RuntimeException("Teacher not found: " + employeeId);
        }

        List<Student> students = studentService.getAllStudents();

        // Get ONLY current teacher's attendance for this date
        Map<String, Boolean> currentTeacherAttendance = new HashMap<>();

        for (Student student : students) {
            // Check if current teacher already marked this student for this date
            List<Attendance> teacherRecords = attendanceRepository
                    .findByStudentAndDateAndTeacher(student, date, teacher);

            boolean present = !teacherRecords.isEmpty() &&
                    teacherRecords.get(0).isPresent();
            currentTeacherAttendance.put(student.getCollegeId(), present);
        }

        model.addAttribute("teacher", teacher);
        model.addAttribute("students", students);
        model.addAttribute("attendanceMap", currentTeacherAttendance);
        model.addAttribute("selectedDate", date);

        return "teacher/mark";
    }
    //  Attendance summary
    @GetMapping("/attendance/summary")
    public String viewAttendanceSummary(Model model, Authentication auth) {
        String teacherId = auth.getName();
        Teacher teacher = teacherRepository.findByEmployeeId(teacherId);

        // Use teacher-specific method
        Map<String, List<Map<String, Object>>> attendanceSummary =
                attendanceService.getAttendanceSummaryByPercentageRangesForTeacher(teacherId);

        // FIX: Make sure categoryLabels is NOT null
        Map<String, String> categoryLabels = Map.of(
                "excellent", "Excellent (85-100%)",
                "good", "Good (75-84%)",
                "average", "Average (60-74%)",
                "poor", "Needs Improvement (Below 60%)",
                "noData", "No Attendance Data"
        );

        model.addAttribute("teacher", teacher);
        model.addAttribute("attendanceSummary", attendanceSummary);
        model.addAttribute("categoryLabels", categoryLabels); // This was missing/null

        return "teacher/attendance-summary";
    }

    //  Attendance history
    @GetMapping("/attendance/history")
    public String viewAttendanceHistory(Model model, Authentication auth) {
        String employeeId = auth.getName();
        Teacher teacher = teacherRepository.findByEmployeeId(employeeId);

        // Get all dates when this teacher marked attendance
        List<LocalDate> attendanceDates = attendanceService.getAttendanceDatesByTeacher(employeeId);

        model.addAttribute("teacher", teacher);
        model.addAttribute("attendanceDates", attendanceDates);

        return "teacher/attendance-history";
    }

    @GetMapping("/attendance/edit/{date}")
    public String editAttendance(@PathVariable String date,
                                 Model model,
                                 Authentication authentication) {

        String teacherId = authentication.getName(); // This gets the logged-in teacher's ID

        System.out.println("=== LOADING ATTENDANCE PAGE ===");
        System.out.println("Date: " + date);
        System.out.println("Teacher: " + teacherId);

        // Convert string date to LocalDate
        LocalDate localDate = LocalDate.parse(date);

        // Get attendance for THIS TEACHER only (not all teachers)
        Map<String, Boolean> attendanceMap = attendanceService.getAttendanceMapByDateAndTeacher(localDate, teacherId);

        System.out.println("Loaded attendance: " + attendanceMap);

        // Get all students
        List<Student> students = studentService.getAllStudents();

        model.addAttribute("students", students);
        model.addAttribute("attendanceMap", attendanceMap);
        model.addAttribute("attendanceDate", date);

        return "teacher/edit-attendance";
    }

    @PostMapping("/attendance/update")
    public String updateAttendance(@RequestParam Long attendanceId,
                                   @RequestParam boolean present,
                                   @RequestParam LocalDate date,
                                   Authentication auth) {
        attendanceService.updateAttendanceStatus(attendanceId, present);

        return "redirect:/teacher/attendance/edit/" + date + "?success=true";
    }



    @PostMapping("/upload-photo")
    public String uploadPhoto(Authentication auth, @RequestParam("photo") MultipartFile photoFile) throws IOException {
        Teacher teacher = teacherRepository.findByEmployeeId(auth.getName());

        if (!photoFile.isEmpty()) {
            String filename = teacher.getEmployeeId() + "_" + photoFile.getOriginalFilename();
            String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
            File dest = new File(uploadDir + "/" + filename);
            photoFile.transferTo(dest);  // save file

            teacher.setPhoto(filename);  // update teacher
            teacherRepository.save(teacher);
        }

        return "redirect:/teacher/dashboard";
    }

    @PostMapping("/save-attendance-ajax")
    @ResponseBody
    public Map<String, String> saveAttendanceAjax(
            @RequestBody Map<String, Object> payload,
            Authentication auth) {   // <-- add this parameter

        String dateStr = (String) payload.get("attendanceDate");
        Object attendanceMapObj = payload.get("attendanceMap");

        Map<String, Boolean> attendanceMap = new HashMap<>();
        if (attendanceMapObj instanceof Map<?, ?> rawMap) {
            rawMap.forEach((key, value) -> attendanceMap.put(key.toString(), (Boolean) value));
        }

        LocalDate date = LocalDate.parse(dateStr);


        String employeeId = auth.getName(); // now this works

        attendanceService.saveAttendance(attendanceMap, date, employeeId);

        return Map.of("message", "✅ Attendance posted successfully!");
    }

    @GetMapping("/attendance/data")
    @ResponseBody
    public Map<String, Object> getAttendanceData(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Student> students = studentService.getAllStudents();
        Map<String, Boolean> attendanceMap = attendanceService.getAttendanceByDate(date);

        List<String> studentNames = students.stream().map(Student::getName).toList();
        List<Integer> presentData = students.stream()
                .map(s -> attendanceMap.getOrDefault(s.getCollegeId(), false) ? 1 : 0)
                .toList();

        return Map.of(
                "students", studentNames,
                "present", presentData
        );
    }

    @GetMapping("/attendance/history/{date}")
    public String viewAttendanceByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                       Model model, Authentication auth) {
        String employeeId = auth.getName();
        Teacher teacher = teacherRepository.findByEmployeeId(employeeId);

        // Get attendance records for this specific date
        List<Attendance> attendanceRecords = attendanceService.getAttendanceByDateAndTeacher(date, employeeId);

        model.addAttribute("teacher", teacher);
        model.addAttribute("attendanceRecords", attendanceRecords);
        model.addAttribute("selectedDate", date);

        return "teacher/attendance-detail"; // You'll need to create this HTML template
    }

    @PostMapping("/attendance/update-batch")
    public String updateBatchAttendance(@RequestParam String attendanceDate,
                                        HttpServletRequest request,
                                        Authentication authentication) {

        String teacherId = authentication.getName();

        System.out.println("=== SAVING ATTENDANCE ===");
        System.out.println("Date: " + attendanceDate);
        System.out.println("Teacher: " + teacherId);

        try {
            Map<String, Boolean> attendanceData = new HashMap<>();

            // Get ALL parameters from request
            Map<String, String[]> allParams = request.getParameterMap();
            System.out.println("All parameters: " + allParams);

            // Get all students to know what to look for
            List<Student> allStudents = studentService.getAllStudents();

            for (Student student : allStudents) {
                String studentId = student.getCollegeId();
                String[] values = allParams.get(studentId);
                boolean isPresent = values != null && values.length > 0 && "on".equals(values[0]);
                attendanceData.put(studentId, isPresent);
                System.out.println("Student " + studentId + " = " + isPresent);
            }

            System.out.println("Final attendance data: " + attendanceData);

            // Convert date and save
            LocalDate localDate = LocalDate.parse(attendanceDate);
            attendanceService.saveAttendance(attendanceData, localDate, teacherId);

            System.out.println("✅ Saved successfully!");

            return "redirect:/teacher/attendance/edit/" + attendanceDate + "?success=true";

        } catch (Exception e) {
            System.err.println("❌ Error saving attendance: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/teacher/attendance/edit/" + attendanceDate + "?error=true";
        }
    }



}
