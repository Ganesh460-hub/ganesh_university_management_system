package com.ums.ums_project.service;

import com.ums.ums_project.model.SubjectResult;
import java.util.List;
import java.util.Map;

public interface SubjectResultService {

    void saveSubjectResult(String collegeId, String teacherEmployeeId, String subject,
                           Integer semester, Integer marks);

    List<SubjectResult> getResultsByStudent(String collegeId);

    List<SubjectResult> getResultsByTeacher(String teacherEmployeeId);

    boolean hasFailedSubjects(String collegeId);

    String getStudentOverallStatus(String collegeId);

    Map<String, List<SubjectResult>> getResultsGroupedBySubject(String collegeId);

    List<SubjectResult> getResultsByStudentAndSemester(String collegeId, Integer semester);

    Map<Integer, List<SubjectResult>> getResultsGroupedBySemester(String collegeId);

    boolean isSemesterComplete(String collegeId, Integer semester);

    List<Integer> getAvailableSemesters(String collegeId);

    Double calculateSemesterCGPA(String collegeId, Integer semester);

    Map<Integer, Boolean> getSemesterStatus(String collegeId);

    Map<Integer, Double> getSemesterCGPAs(String collegeId);

}