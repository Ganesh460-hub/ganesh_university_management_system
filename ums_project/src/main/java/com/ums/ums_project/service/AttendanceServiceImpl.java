package com.ums.ums_project.service;

import com.ums.ums_project.model.Attendance;
import com.ums.ums_project.model.Student;
import com.ums.ums_project.model.Teacher;
import com.ums.ums_project.repository.AttendanceRepository;
import com.ums.ums_project.repository.StudentRepository;
import com.ums.ums_project.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentService studentService;
    private final TeacherRepository teacherRepository ;
    private final StudentRepository studentRepository;

    @Autowired
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 StudentService studentService, TeacherRepository teacherRepository, StudentRepository studentRepository ) {
        this.attendanceRepository = attendanceRepository;
        this.studentService = studentService;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    // Attendance summary for all students
    @Override
    public List<Map<String, Object>> getAttendanceSummary() {
        List<Student> students = studentService.getAllStudents();
        List<Map<String, Object>> summaryList = new ArrayList<>();

        for (Student student : students) {
            List<Attendance> records = attendanceRepository.findByStudent(student);
            long totalClasses = records.size();
            long presentCount = records.stream().filter(Attendance::isPresent).count();
            double percentage = (totalClasses == 0) ? 0.0 : (presentCount * 100.0 / totalClasses);

            Map<String, Object> summary = new HashMap<>();
            summary.put("student", student);
            summary.put("totalClasses", totalClasses);
            summary.put("presentCount", presentCount);
            summary.put("percentage", percentage);

            summaryList.add(summary);
        }
        return summaryList;
    }

    // Get monthly attendance for a specific student
    @Override
    public Map<String, Double> getMonthlyAttendanceForStudent(String collegeId) {
        Student student = studentService.getStudentByCollegeId(collegeId);
        List<Attendance> records = attendanceRepository.findByStudent(student);

        Map<String, List<Attendance>> recordByMonth = records.stream()
                .collect(Collectors.groupingBy(a -> a.getDate().getMonth().toString()));

        Map<String, Double> monthlyPercentage = new HashMap<>();
        for (Map.Entry<String, List<Attendance>> entry : recordByMonth.entrySet()) {
            long total = entry.getValue().size();
            long present = entry.getValue().stream().filter(Attendance::isPresent).count();
            double percent = total == 0 ? 0.0 : (present * 100.0 / total);
            monthlyPercentage.put(entry.getKey(), percent);
        }
        return monthlyPercentage;
    }

    // Attendance history for a specific student
    @Override
    public List<Attendance> getAttendanceHistory(String collegeId) {
        Student student = studentService.getStudentByCollegeId(collegeId);
        return attendanceRepository.findByStudentOrderByDateAsc(student);
    }

    // Get a single attendance record by ID
    @Override
    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));
    }

    // Update attendance status
    @Override
    public void updateAttendance(Long id, boolean present) {
        Attendance record = getAttendanceById(id);
        record.setPresent(present);
        attendanceRepository.save(record);
    }

    // Save attendance for multiple students for a date

    @Override
    public void saveAttendance(Map<String, Boolean> attendanceData, LocalDate date, String teacherEmployeeId) {
        Teacher teacher = teacherRepository.findByEmployeeId(teacherEmployeeId);
        if (teacher == null) {
            throw new RuntimeException("Teacher not found with employeeId: " + teacherEmployeeId);
        }

        for (Map.Entry<String, Boolean> entry : attendanceData.entrySet()) {
            String collegeId = entry.getKey();  // directly use key as collegeId
            boolean present = entry.getValue() != null && entry.getValue();

            Student student = studentService.getStudentByCollegeId(collegeId);
            if (student == null) continue;

            Attendance attendance = new Attendance(student, teacher, date, present);
            attendanceRepository.save(attendance);
        }
    }


    // Get attendance for all students on a specific date
    @Override
    public Map<String, Boolean> getAttendanceByDate(LocalDate date) {
        List<Student> students = studentService.getAllStudents();
        Map<String, Boolean> attendanceMap = new HashMap<>();

        for (Student student : students) {
            List<Attendance> records = attendanceRepository.findByStudentAndDate(student, date);
            boolean present = records.stream().anyMatch(Attendance::isPresent);
            attendanceMap.put(student.getCollegeId(), present);
        }

        return attendanceMap;
    }

    // Attendance records for a month
    @Override
    public List<Map<String, Object>> getAttendanceByMonth(int month) {
        LocalDate start = LocalDate.of(LocalDate.now().getYear(), month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> records = attendanceRepository.findByDateBetween(start, end);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Attendance a : records) {
            Map<String, Object> map = new HashMap<>();
            map.put("collegeId", a.getStudent().getCollegeId());
            map.put("name", a.getStudent().getName());
            map.put("date", a.getDate());
            map.put("present", a.isPresent());
            result.add(map);
        }
        return result;
    }



        @Override
        public List<Attendance> getAttendanceByDateAndTeacher(LocalDate date, String teacherEmployeeId) {
            Teacher teacher = teacherRepository.findByEmployeeId(teacherEmployeeId);
            return attendanceRepository.findByDateAndTeacher(date, teacher);
        }

        @Override
        public void updateAttendanceStatus(Long attendanceId, boolean present) {
            Attendance attendance = attendanceRepository.findById(attendanceId)
                    .orElseThrow(() -> new RuntimeException("Attendance record not found"));
            attendance.setPresent(present);
            attendanceRepository.save(attendance);
        }

        @Override
        public List<LocalDate> getAttendanceDatesByTeacher(String teacherEmployeeId) {
            Teacher teacher = teacherRepository.findByEmployeeId(teacherEmployeeId);
            return attendanceRepository.findDistinctDateByTeacherOrderByDateDesc(teacher);
        }

    @Override
    public Map<String, Boolean> getAttendanceMapByDateAndTeacher(LocalDate date, String teacherEmployeeId) {
        Teacher teacher = teacherRepository.findByEmployeeId(teacherEmployeeId);
        if (teacher == null) {
            throw new RuntimeException("Teacher not found: " + teacherEmployeeId);
        }

        // Use your existing method to get the list
        List<Attendance> records = getAttendanceByDateAndTeacher(date, teacherEmployeeId);
        Map<String, Boolean> attendanceMap = new HashMap<>();

        for (Attendance record : records) {
            attendanceMap.put(record.getStudent().getCollegeId(), record.isPresent());
        }

        System.out.println("=== LOADED ATTENDANCE MAP FOR TEACHER ===");
        System.out.println("Date: " + date);
        System.out.println("Teacher: " + teacherEmployeeId);
        System.out.println("Records found: " + records.size());
        System.out.println("Attendance map: " + attendanceMap);

        return attendanceMap;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getAttendanceSummaryByPercentageRanges(String teacherEmployeeId) {
        List<Map<String, Object>> allStudentsWithPercentages = getAllStudentsWithAttendancePercentages();

        Map<String, List<Map<String, Object>>> categorizedStudents = new LinkedHashMap<>();

        // Initialize categories
        categorizedStudents.put("excellent", new ArrayList<>());  // 85-100%
        categorizedStudents.put("good", new ArrayList<>());       // 75-84%
        categorizedStudents.put("average", new ArrayList<>());    // 60-74%
        categorizedStudents.put("poor", new ArrayList<>());       // Below 60%
        categorizedStudents.put("noData", new ArrayList<>());     // No attendance records

        for (Map<String, Object> studentData : allStudentsWithPercentages) {
            Double percentage = (Double) studentData.get("attendancePercentage");
            String category = categorizeAttendance(percentage);
            categorizedStudents.get(category).add(studentData);
        }

        return categorizedStudents;
    }

    @Override
    public Double calculateAttendancePercentage(String collegeId) {
        List<Attendance> attendanceHistory = getAttendanceHistory(collegeId);

        if (attendanceHistory.isEmpty()) {
            return null; // No attendance data
        }

        long totalClasses = attendanceHistory.size();
        long presentClasses = attendanceHistory.stream()
                .filter(Attendance::isPresent)
                .count();

        return (presentClasses * 100.0) / totalClasses;
    }

    @Override
    public List<Map<String, Object>> getAllStudentsWithAttendancePercentages() {
        List<Student> allStudents = studentRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Student student : allStudents) {
            Double percentage = calculateAttendancePercentage(student.getCollegeId());

            Map<String, Object> studentData = new HashMap<>();
            studentData.put("collegeId", student.getCollegeId());
            studentData.put("name", student.getName());
            studentData.put("attendancePercentage", percentage);
            studentData.put("totalClasses", getAttendanceHistory(student.getCollegeId()).size());
            studentData.put("presentClasses", getAttendanceHistory(student.getCollegeId()).stream()
                    .filter(Attendance::isPresent)
                    .count());

            result.add(studentData);
        }

        return result;
    }

    private String categorizeAttendance(Double percentage) {
        if (percentage == null) {
            return "noData";
        } else if (percentage >= 85.0) {
            return "excellent";
        } else if (percentage >= 75.0) {
            return "good";
        } else if (percentage >= 60.0) {
            return "average";
        } else {
            return "poor";
        }
    }


    @Override
    public Map<String, List<Map<String, Object>>> getAttendanceSummaryByPercentageRangesForTeacher(String teacherEmployeeId) {
        List<Map<String, Object>> allStudentsWithPercentages = getAllStudentsWithAttendancePercentagesForTeacher(teacherEmployeeId);

        Map<String, List<Map<String, Object>>> categorizedStudents = new LinkedHashMap<>();

        // Initialize categories
        categorizedStudents.put("excellent", new ArrayList<>());  // 85-100%
        categorizedStudents.put("good", new ArrayList<>());       // 75-84%
        categorizedStudents.put("average", new ArrayList<>());    // 60-74%
        categorizedStudents.put("poor", new ArrayList<>());       // Below 60%
        categorizedStudents.put("noData", new ArrayList<>());     // No attendance records

        for (Map<String, Object> studentData : allStudentsWithPercentages) {
            Double percentage = (Double) studentData.get("attendancePercentage");
            String category = categorizeAttendance(percentage);
            categorizedStudents.get(category).add(studentData);
        }

        return categorizedStudents; // ← THIS IS THE MISSING RETURN STATEMENT
    }
    @Override
    public Double calculateAttendancePercentageForTeacher(String collegeId, String teacherEmployeeId) {
        // Get attendance only for this teacher
        List<Attendance> teacherAttendance = attendanceRepository.findByStudentCollegeIdAndTeacherEmployeeId(collegeId, teacherEmployeeId);

        if (teacherAttendance.isEmpty()) {
            return null;
        }

        long totalClasses = teacherAttendance.size();
        long presentClasses = teacherAttendance.stream()
                .filter(Attendance::isPresent)
                .count();

        return (presentClasses * 100.0) / totalClasses;
    }

    private List<Map<String, Object>> getAllStudentsWithAttendancePercentagesForTeacher(String teacherEmployeeId) {
        List<Student> allStudents = studentRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Student student : allStudents) {
            Double percentage = calculateAttendancePercentageForTeacher(student.getCollegeId(), teacherEmployeeId);

            Map<String, Object> studentData = new HashMap<>();
            studentData.put("collegeId", student.getCollegeId());
            studentData.put("name", student.getName());
            studentData.put("attendancePercentage", percentage);

            // Get teacher-specific attendance counts
            List<Attendance> teacherAttendance = attendanceRepository.findByStudentCollegeIdAndTeacherEmployeeId(
                    student.getCollegeId(), teacherEmployeeId);

            studentData.put("totalClasses", teacherAttendance.size());
            studentData.put("presentClasses", teacherAttendance.stream()
                    .filter(Attendance::isPresent)
                    .count());

            result.add(studentData);
        }

        return result;
}
}



