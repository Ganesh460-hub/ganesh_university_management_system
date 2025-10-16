package com.ums.ums_project.controller;

import com.ums.ums_project.model.Student;
import com.ums.ums_project.model.SubjectResult;
import com.ums.ums_project.repository.StudentRepository;
import com.ums.ums_project.repository.SubjectResultRepository;
import com.ums.ums_project.repository.TeacherRepository;
import com.ums.ums_project.service.SubjectResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentResultsController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectResultService subjectResultService;

    @Autowired
    private SubjectResultRepository subjectResultRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping("/results")
    public String showStudentResults(
            @RequestParam(value = "semester", required = false) Integer semester,
            Authentication authentication, Model model) {

        String collegeId = authentication.getName();
        Student student = studentRepository.findByCollegeId(collegeId);

        if (student == null) {
            return "redirect:/login?error";
        }

        // Get available semesters
        List<Integer> availableSemesters = getCompletedSemesters(collegeId);

        // CHANGE: Don't auto-select, only use explicitly selected semester
        Integer selectedSemester = (semester != null && availableSemesters.contains(semester))
                ? semester
                : null; // ← Changed from auto-selection to null

        // Get results ONLY if semester is explicitly selected
        List<SubjectResult> semesterResults = new ArrayList<>();
        if (selectedSemester != null) {
            semesterResults = subjectResultService.getResultsByStudentAndSemester(collegeId, selectedSemester);
        }

        // Calculate statistics ONLY if semester is selected
        long totalSubjects = semesterResults.size();
        long passedSubjects = semesterResults.stream().filter(SubjectResult::isPassed).count();
        long failedSubjects = totalSubjects - passedSubjects;
        Double semesterCGPA = calculateSemesterCGPA(semesterResults);
        boolean isSemesterPassed = failedSubjects == 0 && totalSubjects > 0;

        // Add to model
        model.addAttribute("student", student);
        model.addAttribute("subjectResults", semesterResults);
        model.addAttribute("totalSubjects", totalSubjects);
        model.addAttribute("passedSubjects", passedSubjects);
        model.addAttribute("failedSubjects", failedSubjects);
        model.addAttribute("isSemesterPassed", isSemesterPassed);
        model.addAttribute("currentSemester", selectedSemester); // Will be null initially
        model.addAttribute("semesterCGPA", semesterCGPA);
        model.addAttribute("availableSemesters", availableSemesters);
        model.addAttribute("allSemesters", Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));

        return "student/results";
    }

    private List<Integer> getCompletedSemesters(String collegeId) {
        List<Integer> completedSemesters = new ArrayList<>();

        // For each semester 1-8, check if all teachers posted results
        for (int sem = 1; sem <= 8; sem++) {
            if (isSemesterComplete(collegeId, sem)) {
                completedSemesters.add(sem);
            }
        }

        return completedSemesters;
    }

    private boolean isSemesterComplete(String collegeId, Integer semester) {
        try {
            Student student = studentRepository.findByCollegeId(collegeId);
            if (student == null) return false;

            // Get count of unique teachers who posted results for this semester
            Long teachersWithResults = subjectResultRepository.countTeachersForStudentAndSemester(student, semester);

            // Get total teachers in the department
            Long totalTeachers = teacherRepository.countByDepartment(student.getDepartment());

            return teachersWithResults != null && totalTeachers != null && teachersWithResults.equals(totalTeachers);
        } catch (Exception e) {
            return false;
        }
    }

    private Double calculateSemesterCGPA(List<SubjectResult> results) {
        if (results.isEmpty()) return 0.0;

        double totalGradePoints = results.stream()
                .mapToDouble(SubjectResult::getGradePoint)
                .sum();

        return totalGradePoints / results.size();
    }
}