package com.arjunsudheer.backend;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class MyPlanController {
    Connection conn;
    Statement st;

    public MyPlanController() {
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

    // Get a student's current plan
    @GetMapping("/plan/{studentId}")
    public ResponseEntity<?> getStudentPlan(@PathVariable Integer studentId) {
        // Raise an internal server error if there is no database connection established
        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        List<Course> plannedCoursesList = new ArrayList<>();
        // Query to use in prepared statement for efficiency
        // Use INNER JOIN over cartesian product for performance
        String sql = "SELECT * FROM Courses c " +
                "INNER JOIN PlannedEnrollments pe ON c.CourseID = pe.CourseID " +
                "WHERE pe.StudentID = ? ORDER BY c.CourseID";

        // Use prepared statement for efficiency
        // Error handling for SQL error from JDBC
        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            // Filter by the specified StudentID in the WHERE clause
            pstmt.setInt(1, studentId);

            ResultSet rs = pstmt.executeQuery();

            // Get the data for each course in the ResultSet
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("CourseID"),
                        rs.getString("CourseName"),
                        rs.getString("ProfessorName"),
                        rs.getInt("Units"),
                        rs.getInt("SeatsOpen"),
                        rs.getString("Term"),
                        rs.getString("DayOfWeek"));
                plannedCoursesList.add(course);
            }
            return ResponseEntity.ok(plannedCoursesList);
        } catch (SQLException e) {
            System.out.println("Error fetching plan for StudentID " + studentId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching plan: " + e.getMessage());
        }
    }

    // Get a student's current grades
    @GetMapping("/grades/{studentId}")
    public ResponseEntity<?> getStudentGrades(@PathVariable Integer studentId) {
        // Raise an internal server error if there is no database connection established
        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        List<GradedCourse> plannedCoursesList = new ArrayList<>();
        // Query to use in prepared statement for efficiency
        // Use INNER JOIN over cartesian product for performance
        String sql = "SELECT * FROM Courses c " +
                "INNER JOIN Grades g ON c.CourseID = g.CourseID " +
                "WHERE g.StudentID = ? ORDER BY c.CourseID";

        // Use prepared statement for efficiency
        // Error handling for SQL error from JDBC
        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            // Filter by the specified StudentID in the WHERE clause
            pstmt.setInt(1, studentId);

            ResultSet rs = pstmt.executeQuery();

            // Get the data for each course in the ResultSet
            while (rs.next()) {
                GradedCourse course = new GradedCourse(
                        rs.getInt("CourseID"),
                        rs.getString("CourseName"),
                        rs.getString("ProfessorName"),
                        rs.getInt("Units"),
                        rs.getInt("SeatsOpen"),
                        rs.getString("Term"),
                        rs.getString("DayOfWeek"),
                        rs.getString("Grade"));
                plannedCoursesList.add(course);
            }
            return ResponseEntity.ok(plannedCoursesList);
        } catch (SQLException e) {
            System.out.println("Error fetching grades for StudentID " + studentId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching grades: " + e.getMessage());
        }
    }

    // Class for storing attributes received from frontend when deleting planned
    // enrollment class
    static class AddEnrollmentCourseRequest {
        private Integer courseID;
        private String term;
        private Boolean isRetaking;

        // Getters and setters used by SpringBoot to fetch and decode values from
        // frontend
        public Integer getCourseID() {
            return courseID;
        }

        public void setCourseID(Integer courseID) {
            this.courseID = courseID;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public Boolean getIsRetaking() {
            return isRetaking;
        }

        public void setIsRetaking(Boolean isRetaking) {
            this.isRetaking = isRetaking;
        }
    }

    // Update a student's course plan
    @PostMapping("/plan/add/{studentID}")
    public ResponseEntity<String> updateStudentPlan(
            @PathVariable Integer studentID,
            @RequestBody AddEnrollmentCourseRequest request) {

        // Raise an internal server error if there is no database connection established
        if (this.conn == null) {
            System.out.println("Database connection is not initialized. Cannot save plan.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        // Create query to use with PreparedStatements for efficiency
        String insertSql = "INSERT INTO PlannedEnrollments VALUES (?, ?, ?, ?)";

        try {
            // Turn off autocommit so the database update can be made using a transaction
            // for ACID compliance
            this.conn.setAutoCommit(false);

            // Add new planned enrollment course
            try (PreparedStatement insertPstmt = this.conn.prepareStatement(insertSql)) {
                insertPstmt.setInt(1, studentID);
                insertPstmt.setInt(2, request.getCourseID());
                insertPstmt.setString(3, request.getTerm());
                insertPstmt.setBoolean(4, request.getIsRetaking());
                insertPstmt.addBatch();

                insertPstmt.executeBatch();
            }

            // Commit the transaction if all operations succeed
            this.conn.commit();
            return ResponseEntity.ok("Plan saved successfully for StudentID: " + studentID);

        } catch (SQLException e) {
            System.out.println("Error saving plan for StudentID " + studentID + ": " + e.getMessage());
            e.printStackTrace();
            try {
                this.conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction: " + ex.getMessage());
                ex.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving plan: " + e.getMessage());
        } finally {
            try {
                // Reset the auto-commit mode
                this.conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error resetting auto-commit: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // Class for storing attributes received from frontend when deleting planned
    // enrollment class
    static class RemoveEnrollmentCourseRequest {
        private Integer courseID;
        private String term;

        // Getters and setters used by SpringBoot to fetch and decode values from
        // frontend
        public Integer getCourseID() {
            return courseID;
        }

        public void setCourseID(Integer courseID) {
            this.courseID = courseID;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }
    }

    // Remove a course from a student's course plan
    @PostMapping("/plan/remove/{studentID}")
    public ResponseEntity<String> updateStudentPlan(
            @PathVariable Integer studentID,
            @RequestBody RemoveEnrollmentCourseRequest request) {

        // Raise an internal server error if there is no database connection established
        if (this.conn == null) {
            System.out.println("Database connection is not initialized. Cannot save plan.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        // Create query to use with PreparedStatements for efficiency
        String deleteSql = "DELETE FROM PlannedEnrollments WHERE StudentID = ? AND CourseID = ? AND Term = ?";

        try {
            // Turn off autocommit so the database update can be made using a transaction
            // for ACID compliance
            this.conn.setAutoCommit(false);

            // Remove planned enrollment course
            try (PreparedStatement insertPstmt = this.conn.prepareStatement(deleteSql)) {
                insertPstmt.setInt(1, studentID);
                insertPstmt.setInt(2, request.getCourseID());
                insertPstmt.setString(3, request.getTerm());
                insertPstmt.addBatch();

                insertPstmt.executeBatch();
            }

            // Commit the transaction if all operations succeed
            this.conn.commit();
            return ResponseEntity.ok("Plan saved successfully for StudentID: " + studentID);

        } catch (SQLException e) {
            System.out.println("Error saving plan for StudentID " + studentID + ": " + e.getMessage());
            e.printStackTrace();
            try {
                this.conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction: " + ex.getMessage());
                ex.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving plan: " + e.getMessage());
        } finally {
            try {
                // Reset the auto-commit mode
                this.conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error resetting auto-commit: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // Class for storing attributes received from frontend when deleting planned
    // enrollment class
    static class AddGradesCourseRequest {
        private Integer courseID;
        private String term;
        private String grade;

        // Getters and setters used by SpringBoot to fetch and decode values from
        // frontend
        public Integer getCourseID() {
            return courseID;
        }

        public void setCourseID(Integer courseID) {
            this.courseID = courseID;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }
    }

    // Update a student's course plan
    @PostMapping("/grades/add/{studentID}")
    public ResponseEntity<String> updateStudentGrades(
            @PathVariable Integer studentID,
            @RequestBody AddGradesCourseRequest request) {

        // Raise an internal server error if there is no database connection
        // established
        if (this.conn == null) {
            System.out.println("Database connection is not initialized. Cannot save plan.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        // Create queries to use with PreparedStatements for efficiency
        String insertSql = "INSERT INTO Grades VALUES (?, ?, ?, ?)";

        try {
            // Turn off autocommit so the database update can be made using a transaction
            // for ACID compliance
            this.conn.setAutoCommit(false);

            // Add new grades course
            try (PreparedStatement insertPstmt = this.conn.prepareStatement(insertSql)) {
                insertPstmt.setInt(1, studentID);
                insertPstmt.setInt(2, request.getCourseID());
                insertPstmt.setString(3, request.getTerm());
                insertPstmt.setString(4, request.getGrade());
                insertPstmt.addBatch();

                insertPstmt.executeBatch();
            }

            // Commit the transaction if all operations succeed
            this.conn.commit();
            return ResponseEntity.ok("Plan saved successfully for StudentID: " +
                    studentID);

        } catch (SQLException e) {
            System.out.println("Error saving plan for StudentID " + studentID + ": " +
                    e.getMessage());
            e.printStackTrace();

            try {
                this.conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction: " + ex.getMessage());
                ex.printStackTrace();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error    saving plan: " + e.getMessage());
        } finally {
            try {
                // Reset the auto-commit mode
                this.conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error resetting auto-commit: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
