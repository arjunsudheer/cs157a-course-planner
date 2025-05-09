package com.arjunsudheer.backend;

import java.sql.*;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

// Added imports for the new save plan endpoint
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Class for the login request payload
class LoginRequest {
    private String studentID;

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }
}

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class HomeController {
    Connection conn;
    Statement st;

    public HomeController() {
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

    // Endpoint for student login
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Error handling for invalid database connection
        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        // Ensure that a StudentID was passed
        String studentIdStr = loginRequest.getStudentID();
        if (studentIdStr == null || studentIdStr.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student ID is required.");
        }

        // Ensure that the studentID is a number
        int studentId;
        try {
            studentId = Integer.parseInt(studentIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid Student ID format. It must be a number.");
        }

        // SQL query to check if the passed student ID exists in the Students table
        String sql = "SELECT StudentID FROM Students WHERE StudentID = ?";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Student found
                return ResponseEntity.ok("Login successful");
            } else {
                // Student not found
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Student ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error during login for StudentID " + studentIdStr + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during login: " + e.getMessage());
        }
    }
}