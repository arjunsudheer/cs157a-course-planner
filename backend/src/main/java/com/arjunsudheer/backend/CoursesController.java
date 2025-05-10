package com.arjunsudheer.backend;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class CoursesController {
    Connection conn;
    Statement st;

    public CoursesController() {
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

    // Returns the available classes that match the search criteria
    @GetMapping("/courses/search")
    public List<Course> searchCourses(
            @RequestParam(required = false) String nameOrProfessor,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String grade) {
        List<Course> courses = new ArrayList<>();
        // Error handling when database connection is not established
        if (this.conn == null) {
            System.out.println("Database connection is not initialized. Cannot perform search.");
            return courses;
        }

        // Used to build the SQL query based on the search/filter inputs from the
        // frontend
        // Only show courses that have at least 1 seat open
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT * FROM Courses WHERE SeatsOpen > 0");
        List<Object> params = new ArrayList<>();

        // Filter by the Course Name or Professor if specified
        if (nameOrProfessor != null && !nameOrProfessor.isEmpty()) {
            // Convert to lowercase to make it a case insensitive search
            sqlBuilder.append(" AND (LOWER(CourseName) LIKE LOWER(?) OR LOWER(ProfessorName) LIKE LOWER(?))");
            String searchQuery = "%" + nameOrProfessor + "%";
            params.add(searchQuery);
            params.add(searchQuery);
        }

        // Filter by term if provided by the frontend
        if (term != null && !term.isEmpty()) {
            // We want courses that either contain the specific term or are marked as 'all'
            sqlBuilder.append(" AND (LOWER(TermOffered) LIKE LOWER(?) OR LOWER(TermOffered) = LOWER('all'))");
            params.add("%" + term + "%");
        }

        // Filter by the day the course is offered
        if (day != null && !day.isEmpty()) {
            // Make it lowercase to make it a case insensitive filter
            sqlBuilder.append(" AND LOWER(DayOfWeek) LIKE LOWER(?)");
            params.add("%" + day + "%");
        }

        sqlBuilder.append(" ORDER BY CourseID");

        System.out.println("Executing SQL for search: " + sqlBuilder.toString());

        // Error handling for JDBC errors with SQL database connection
        try (PreparedStatement pstmt = this.conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("CourseID"),
                        rs.getString("CourseName"),
                        rs.getString("ProfessorName"),
                        rs.getInt("Units"),
                        rs.getInt("SeatsOpen"),
                        rs.getString("TermOffered"),
                        rs.getString("DayOfWeek"));
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Error during course search: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }

    @GetMapping("/courses")
    public ArrayList<Course> getCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        try {
            // Fetch info for all classes
            ResultSet rs = st.executeQuery("SELECT * FROM Courses");

            // Get the data for each course in the ResultSet
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("CourseID"),
                        rs.getString("CourseName"),
                        rs.getString("ProfessorName"),
                        rs.getInt("Units"),
                        rs.getInt("SeatsOpen"),
                        rs.getString("TermOffered"),
                        rs.getString("DayOfWeek"));
                courses.add(course);
            }

            return courses;
        } catch (SQLException e) {
            System.out.println("Unable to retrieve all courses information.");
            return new ArrayList<Course>();
        }
    }
}