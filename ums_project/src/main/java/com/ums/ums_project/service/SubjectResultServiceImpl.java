package com.ums.ums_project.service;

import com.ums.ums_project.model.Student;
import com.ums.ums_project.model.SubjectResult;
import com.ums.ums_project.model.Teacher;
import com.ums.ums_project.repository.StudentRepository;
import com.ums.ums_project.repository.SubjectResultRepository;
import com.ums.ums_project.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubjectResultServiceImpl implements SubjectResultService {

    @Autowired
    private SubjectResultRepository subjectResultRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public void saveSubjectResult(String collegeId, String teacherEmployeeId, String subject,
                                  Integer semester, Integer marks) {
        Student student = studentRepository.findByCollegeId(collegeId);
        Teacher teacher = teacherRepository.findByEmployeeId(teacherEmployeeId);

        if (student == null || teacher == null) {
            throw new RuntimeException("Student or Teacher not found");
        }

        // Check if result already exists
        List<SubjectResult> existingResults = subjectResultRepository.findByStudentAndTeacherAndSubject(
                student, teacher, subject);

        SubjectResult result;
        if (!existingResults.isEmpty()) {
            // Update existing result
            result = existingResults.get(0);
            result.setMarks(marks);
            result.setSemester(semester);
            result.setResultDate(LocalDate.now());
        } else {
            // Create new result - NO REMARKS
            result = new SubjectResult(student, teacher, subject, semester, marks, LocalDate.now());
        }

        subjectResultRepository.save(result);
    }

    @Override
    public List<SubjectResult> getResultsByStudent(String collegeId) {
        Student student = studentRepository.findByCollegeId(collegeId);
        return subjectResultRepository.findByStudent(student);
    }

    @Override
    public List<SubjectResult> getResultsByTeacher(String teacherEmployeeId) {
        Teacher teacher = teacherRepository.findByEmployeeId(teacherEmployeeId);
        return subjectResultRepository.findByTeacher(teacher);
    }

    @Override
    public boolean hasFailedSubjects(String collegeId) {
        Student student = studentRepository.findByCollegeId(collegeId);
        return subjectResultRepository.hasFailedSubjects(student);
    }

    @Override
    public String getStudentOverallStatus(String collegeId) {
        return hasFailedSubjects(collegeId) ? "FAILED" : "PASSED";
    }

    @Override
    public Map<String, List<SubjectResult>> getResultsGroupedBySubject(String collegeId) {
        List<SubjectResult> allResults = getResultsByStudent(collegeId);
        return allResults.stream()
                .collect(Collectors.groupingBy(SubjectResult::getSubject));
    }

    @Override
    public List<SubjectResult> getResultsByStudentAndSemester(String collegeId, Integer semester) {
        Student student = studentRepository.findByCollegeId(collegeId);
        return subjectResultRepository.findByStudentAndSemester(student, semester);
    }

    @Override
    public Map<Integer, List<SubjectResult>> getResultsGroupedBySemester(String collegeId) {
        List<SubjectResult> allResults = getResultsByStudent(collegeId);
        return allResults.stream()
                .collect(Collectors.groupingBy(SubjectResult::getSemester));
    }

    @Override
    public boolean isSemesterComplete(String collegeId, Integer semester) {
        Student student = studentRepository.findByCollegeId(collegeId);
        if (student == null) return false;

        // Get count of unique teachers who posted results for this semester
        Long teachersWithResults = subjectResultRepository.countTeachersForStudentAndSemester(student, semester);

        // Get total teachers in the department
        try {
            Long totalTeachers = teacherRepository.countByDepartment(student.getDepartment());
            return teachersWithResults != null && totalTeachers != null && teachersWithResults.equals(totalTeachers);
        } catch (Exception e) {
            // If method doesn't exist yet, assume semester is complete if any results exist
            return teachersWithResults != null && teachersWithResults > 0;
        }
    }

    @Override
    public List<Integer> getAvailableSemesters(String collegeId) {
        try {
            Student student = studentRepository.findByCollegeId(collegeId);
            if (student == null) return new ArrayList<>();

            List<Integer> availableSemesters = subjectResultRepository.findAvailableSemestersByStudent(student);
            return availableSemesters != null ? availableSemesters : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    @Override
    public Double calculateSemesterCGPA(String collegeId, Integer semester) {
        List<SubjectResult> semesterResults = getResultsByStudentAndSemester(collegeId, semester);

        if (semesterResults.isEmpty()) return 0.0;

        double totalGradePoints = semesterResults.stream()
                .mapToDouble(SubjectResult::getGradePoint)
                .sum();

        return totalGradePoints / semesterResults.size();
    }

    @Override
    public Map<Integer, Boolean> getSemesterStatus(String collegeId) {
        Map<Integer, Boolean> statusMap = new HashMap<>();
        List<Integer> availableSemesters = getAvailableSemesters(collegeId);

        for (Integer semester : availableSemesters) {
            List<SubjectResult> results = getResultsByStudentAndSemester(collegeId, semester);
            boolean hasFailed = results.stream().anyMatch(result -> !result.isPassed());
            statusMap.put(semester, !hasFailed); // true = PASSED, false = FAILED
        }

        return statusMap;
    }

    @Override
    public Map<Integer, Double> getSemesterCGPAs(String collegeId) {
        Map<Integer, Double> cgpaMap = new HashMap<>();
        List<Integer> availableSemesters = getAvailableSemesters(collegeId);

        for (Integer semester : availableSemesters) {
            cgpaMap.put(semester, calculateSemesterCGPA(collegeId, semester));
        }

        return cgpaMap;
    }

    // Helper method to get total teachers in department
    private Long getTotalTeachersInDepartment(String department) {
        return teacherRepository.countByDepartment(department);
    }

}