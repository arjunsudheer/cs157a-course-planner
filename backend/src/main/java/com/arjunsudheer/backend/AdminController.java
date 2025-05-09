package com.arjunsudheer.backend;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Added imports for the new save plan endpoint
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

// Class for course management
class CourseData {
    private String courseName;
    private String professorName;
    private int units;
    private int seatsOpen;
    private String termsOffered;
    private String daysOfWeek;

    // Getters and setters
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getSeatsOpen() {
        return seatsOpen;
    }

    public void setSeatsOpen(int seatsOpen) {
        this.seatsOpen = seatsOpen;
    }

    public String getTermsOffered() {
        return termsOffered;
    }

    public void setTermsOffered(String termsOffered) {
        this.termsOffered = termsOffered;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }
}

// Class for student management
class StudentData {
    private String studentID;
    private String studentName;
    private String year;

    // Getters and setters
    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class AdminController {
    Connection conn;
    Statement st;

    public AdminController() {
        try {
            // Initialize PostgreSQL JDBC driver to connect to the courseplanner database
            this.conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/courseplanner", "student",
                    "pass");

            this.st = this.conn.createStatement();
        } catch (SQLException e) {
            System.out.println("Unable to load the driver class.");
            return;
        }
    }

    // Admin endpoints for course management
    @PostMapping("/api/admin/courses")
    public ResponseEntity<?> addCourse(@RequestBody CourseData courseData, @RequestParam String studentID) {
        if (!studentID.equals("0")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        String sql = "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, courseData.getCourseName());
            pstmt.setString(2, courseData.getProfessorName());
            pstmt.setInt(3, courseData.getUnits());
            pstmt.setInt(4, courseData.getSeatsOpen());
            pstmt.setString(5, courseData.getTermsOffered());
            pstmt.setString(6, courseData.getDaysOfWeek());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "Course added successfully",
                                "courseId", generatedKeys.getInt(1)));
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add course");
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding course: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/admin/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable int courseId, @RequestParam String studentID) {
        if (!studentID.equals("0")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        String sql = "DELETE FROM Courses WHERE CourseID = ?";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Course deleted successfully"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        } catch (SQLException e) {
            System.out.println("Error deleting course: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting course: " + e.getMessage());
        }
    }

    // Admin endpoints for student management
    @PostMapping("/api/admin/students")
    public ResponseEntity<?> addStudent(@RequestBody StudentData studentData, @RequestParam String studentID) {
        if (!studentID.equals("0")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        // Validate student data
        if (studentData.getStudentID() == null || studentData.getStudentID().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student ID is required");
        }
        if (!studentData.getStudentID().matches("S\\d{3}")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student ID must be in format SXXX (e.g., S001)");
        }
        if (studentData.getStudentName() == null || studentData.getStudentName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student name is required");
        }
        if (studentData.getYear() == null || studentData.getYear().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Year is required");
        }
        if (!studentData.getYear().matches("Freshman|Sophomore|Junior|Senior")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Year must be one of: Freshman, Sophomore, Junior, Senior");
        }

        // Check if the ID already exists
        try (PreparedStatement checkStmt = this.conn
                .prepareStatement("SELECT StudentID FROM Students WHERE StudentID = ?")) {
            checkStmt.setString(1, studentData.getStudentID());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student ID already exists");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking student ID: " + e.getMessage());
        }

        String sql = "INSERT INTO Students (StudentID, StudentName, Year) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            pstmt.setString(1, studentData.getStudentID().trim());
            pstmt.setString(2, studentData.getStudentName().trim());
            pstmt.setString(3, studentData.getYear().trim());

            System.out.println("Executing SQL: " + sql);
            System.out.println("Parameters: " + studentData.getStudentID() + ", " +
                    studentData.getStudentName() + ", " + studentData.getYear());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);

            if (affectedRows > 0) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Student added successfully",
                        "studentId", studentData.getStudentID()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add student - no rows affected");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding student: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/admin/students/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable int studentId, @RequestParam String studentID) {
        if (!studentID.equals("0")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        String sql = "DELETE FROM Students WHERE StudentID = ?";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Student deleted successfully"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting student: " + e.getMessage());
        }
    }

    // Endpoint to get all students
    @GetMapping("/api/admin/students")
    public ResponseEntity<?> getAllStudents() {
        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        List<Map<String, Object>> students = new ArrayList<>();
        String sql = "SELECT * FROM Students ORDER BY StudentID";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> student = new HashMap<>();
                student.put("studentID", rs.getInt("StudentID"));
                student.put("studentName", rs.getString("StudentName"));
                student.put("year", rs.getString("Year"));
                students.add(student);
            }
            return ResponseEntity.ok(students);

        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching students: " + e.getMessage());
        }
    }

    // Endpoint to search students by ID or name
    @GetMapping("/api/admin/students/search")
    public ResponseEntity<?> searchStudents(@RequestParam(required = false) String query) {
        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Search query is required");
        }

        List<Map<String, Object>> students = new ArrayList<>();
        String sql = "SELECT * FROM Students WHERE StudentID ILIKE ? OR StudentName ILIKE ? ORDER BY StudentID";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            String searchPattern = "%" + query.trim() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> student = new HashMap<>();
                student.put("studentID", rs.getString("StudentID"));
                student.put("studentName", rs.getString("StudentName"));
                student.put("year", rs.getString("Year"));
                students.add(student);
            }
            return ResponseEntity.ok(students);

        } catch (SQLException e) {
            System.out.println("Error searching students: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching students: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/admin/students/search")
    public ResponseEntity<?> deleteStudentBySearch(@RequestParam String query, @RequestParam String studentID) {
        if (!studentID.equals("0")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Search query is required");
        }

        String sql = "DELETE FROM Students WHERE StudentID ILIKE ? OR StudentName ILIKE ?";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            String searchPattern = "%" + query.trim() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Deleted " + affectedRows + " student(s) successfully"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matching students found");
        } catch (SQLException e) {
            System.out.println("Error deleting students: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting students: " + e.getMessage());
        }
    }

}
