package com.ums.ums_project.controller;

import com.ums.ums_project.model.Student;
import com.ums.ums_project.model.Teacher;
import com.ums.ums_project.repository.StudentRepository;
import com.ums.ums_project.repository.TeacherRepository;
import com.ums.ums_project.service.SubjectResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherResultsController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectResultService subjectResultService;

    @GetMapping("/subject-results")
    public String showSubjectResultsForm(
            @RequestParam(value = "semester", defaultValue = "1") Integer semester,
            Authentication authentication, Model model) {

        String employeeId = authentication.getName();
        Teacher teacher = teacherRepository.findByEmployeeId(employeeId);

        if (teacher == null) {
            return "redirect:/login?error";
        }

        // DEBUG: Get ALL students from teacher's department
        List<Student> students = studentRepository.findByDepartmentOrderByCollegeId(teacher.getDepartment());

        System.out.println("=== TEACHER SUBJECT RESULTS DEBUG ===");
        System.out.println("Teacher: " + teacher.getEmployeeId() + " - " + teacher.getName());
        System.out.println("Department: " + teacher.getDepartment());
        System.out.println("Students found: " + students.size());
        students.forEach(s -> System.out.println(" - " + s.getCollegeId() + " - " + s.getName()));
        System.out.println("=====================================");

        model.addAttribute("teacher", teacher);
        model.addAttribute("classStudents", students);
        model.addAttribute("teacherSubject", teacher.getSubject());
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("allSemesters", Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));

        return "teacher/subject-results";
    }

    @PostMapping("/submit-results")
    public String submitSubjectResults(@RequestParam String studentId,
                                       @RequestParam String subject,
                                       @RequestParam Integer semester,
                                       @RequestParam Integer marks,
                                       Authentication authentication) {
        String employeeId = authentication.getName();

        try {
            subjectResultService.saveSubjectResult(
                    studentId,
                    employeeId,
                    subject,
                    semester,
                    marks
            );

            return "redirect:/teacher/subject-results?success&semester=" + semester;
        } catch (Exception e) {
            return "redirect:/teacher/subject-results?error=" + e.getMessage() + "&semester=" + semester;
        }
    }

    @PostMapping("/submit-batch-results")
    public String submitBatchResults(@RequestParam List<String> studentIds,
                                     @RequestParam List<String> subjects,
                                     @RequestParam List<Integer> semesters,
                                     @RequestParam List<Integer> marksList,
                                     Authentication authentication) {
        String employeeId = authentication.getName();

        try {
            for (int i = 0; i < studentIds.size(); i++) {
                subjectResultService.saveSubjectResult(
                        studentIds.get(i),
                        employeeId,
                        subjects.get(i),
                        semesters.get(i),
                        marksList.get(i)
                );
            }

            // Redirect back to the same semester
            Integer semester = semesters.isEmpty() ? 1 : semesters.get(0);
            return "redirect:/teacher/subject-results?success&semester=" + semester;
        } catch (Exception e) {
            Integer semester = semesters.isEmpty() ? 1 : semesters.get(0);
            return "redirect:/teacher/subject-results?error=" + e.getMessage() + "&semester=" + semester;
        }
    }

    // Add this to debug the student-teacher relationship
    @GetMapping("/debug-students")
    @ResponseBody
    public String debugStudents(Authentication authentication) {
        String employeeId = authentication.getName();
        Teacher teacher = teacherRepository.findByEmployeeId(employeeId);

        StringBuilder debug = new StringBuilder();
        debug.append("=== TEACHER DEBUG ===\n");
        debug.append("Teacher: ").append(teacher.getEmployeeId()).append(" - ").append(teacher.getName()).append("\n");
        debug.append("Department: ").append(teacher.getDepartment()).append("\n");
        debug.append("Subject: ").append(teacher.getSubject()).append("\n\n");

        // Get all students in department
        List<Student> students = studentRepository.findByDepartmentOrderByCollegeId(teacher.getDepartment());
        debug.append("Students in department: ").append(students.size()).append("\n");

        for (Student student : students) {
            debug.append(" - ").append(student.getCollegeId())
                    .append(" - ").append(student.getName())
                    .append(" - Sem: ").append(student.getCurrentSemester())
                    .append("\n");
        }

        return debug.toString();
    }
}