package com.arjunsudheer.backend;

import java.sql.*;
import java.util.ArrayList;
import com.arjunsudheer.backend.Course;

import java.util.List;
import java.util.Map; // Added for login response
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

// Simple class to represent a planned course from the frontend payload
class PlannedCourseData {
    private int courseID;
    private String term;
    private boolean isRetaking;

    // Getters and setters are required by Spring for JSON deserialization
    public int getCourseID() { return courseID; }
    public void setCourseID(int courseID) { this.courseID = courseID; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public boolean getIsRetaking() { return isRetaking; }
    public void setIsRetaking(boolean isRetaking) { this.isRetaking = isRetaking; }
}

// Class for the login request payload
class LoginRequest {
    private String studentID;

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }
}

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

            // Create the database if it does not exist
            createCoursesDB();
        } catch (SQLException e) {
            System.out.println("Unable to load the driver class.");
            return;
        }
    }

    @GetMapping("/courses/search")
    public List<Course> searchCourses(
            @RequestParam(required = false) String nameOrProfessor,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String grade
    ) {
        List<Course> courses = new ArrayList<>();
        if (this.conn == null) {
            System.out.println("Database connection is not initialized. Cannot perform search.");
            return courses; 
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM Courses WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (nameOrProfessor != null && !nameOrProfessor.isEmpty()) {
            sqlBuilder.append(" AND (LOWER(CourseName) LIKE LOWER(?) OR LOWER(ProfessorName) LIKE LOWER(?))");
            String searchQuery = "%" + nameOrProfessor + "%";
            params.add(searchQuery);
            params.add(searchQuery);
        }

        // filter by term if provided by the frontend
        if (term != null && !term.isEmpty()) {
            // we want courses that either contain the specific term or are  marked as 'all'
            sqlBuilder.append(" AND (LOWER(TermsOffered) LIKE LOWER(?) OR LOWER(TermsOffered) = LOWER('all'))");
            params.add("%" + term + "%"); 
        }

        if (day != null && !day.isEmpty()) {
            sqlBuilder.append(" AND LOWER(DaysOfWeek) LIKE LOWER(?)");
            params.add("%" + day + "%"); 
        }
        
        sqlBuilder.append(" ORDER BY CourseID");

        System.out.println("Executing SQL for search: " + sqlBuilder.toString());

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
                        rs.getString("TermsOffered"),
                        rs.getString("DaysOfWeek"));
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
                        rs.getString("TermsOffered"),
                        rs.getString("DaysOfWeek"));
                courses.add(course);
            }

            return courses;

        } catch (SQLException e) {
            System.out.println("Unable to retrieve all courses information.");
            return new ArrayList<Course>();
        }
    }

    // get a student's current plan
    @GetMapping("/students/{studentId}/plan")
    public ResponseEntity<?> getStudentPlan(@PathVariable Integer studentId) {
        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        List<Course> plannedCoursesList = new ArrayList<>();
        String sql = "SELECT c.* FROM Courses c " +
                     "JOIN PlannedEnrollments pe ON c.CourseID = pe.CourseID " +
                     "WHERE pe.StudentID = ? ORDER BY c.CourseID";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("CourseID"),
                        rs.getString("CourseName"),
                        rs.getString("ProfessorName"),
                        rs.getInt("Units"),
                        rs.getInt("SeatsOpen"),
                        rs.getString("TermsOffered"),
                        rs.getString("DaysOfWeek"));
                plannedCoursesList.add(course);
            }
            return ResponseEntity.ok(plannedCoursesList);

        } catch (SQLException e) {
            System.out.println("Error fetching plan for StudentID " + studentId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching plan: " + e.getMessage());
        }
    }

    //  save/update a student's course plan
    @PostMapping("/students/{studentId}/plan")
    public ResponseEntity<String> updateStudentPlan(
            @PathVariable Integer studentId,
            @RequestBody List<PlannedCourseData> planItems) {

        if (this.conn == null) {
            System.out.println("Database connection is not initialized. Cannot save plan.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }

        String deleteSql = "DELETE FROM PlannedEnrollments WHERE StudentID = ?";
        String insertSql = "INSERT INTO PlannedEnrollments (StudentID, CourseID, Term, IsRetaking) VALUES (?, ?, ?, ?)";

        try {
            this.conn.setAutoCommit(false);

            // 1. Delete existing plan entries for the student so we can cleanly add
            try (PreparedStatement deletePstmt = this.conn.prepareStatement(deleteSql)) {
                deletePstmt.setInt(1, studentId);
                deletePstmt.executeUpdate();
            }

            // 2. add new plan entries
            if (planItems != null && !planItems.isEmpty()) {
                try (PreparedStatement insertPstmt = this.conn.prepareStatement(insertSql)) {
                    for (PlannedCourseData item : planItems) {
                        insertPstmt.setInt(1, studentId);
                        insertPstmt.setInt(2, item.getCourseID());
                        insertPstmt.setString(3, item.getTerm());
                        insertPstmt.setBoolean(4, item.getIsRetaking());
                        insertPstmt.addBatch();
                    }
                    insertPstmt.executeBatch();
                }
            }

            this.conn.commit(); // commit the transaction if all operations succeed
            return ResponseEntity.ok("Plan saved successfully for StudentID: " + studentId);

        } catch (SQLException e) {
            System.out.println("Error saving plan for StudentID " + studentId + ": " + e.getMessage());
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
                this.conn.setAutoCommit(true); // reset the auto-commit mode
            } catch (SQLException ex) {
                System.out.println("Error resetting auto-commit: " + ex.getMessage());
                ex.printStackTrace(); 
            }
        }
    }

    private void createCoursesDB() {
        try {
            // Check if the Courses table exists
            ResultSet rs = this.st.executeQuery(
                    "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'courses')");

            rs.next();
            boolean tableExists = rs.getBoolean(1);

            // Do not update the Courses table if already exists
            if (tableExists)
                return;

            // Create a Courses table
            this.st.executeUpdate("CREATE TABLE Courses (" +
            // Use SERIAL keyword in PostgreSQL DB to make auto-incremented integer value
                    "CourseID SERIAL PRIMARY KEY," +
                    "CourseName VARCHAR(100) NOT NULL," +
                    "ProfessorName VARCHAR(100) NOT NULL," +
                    "Units INTEGER NOT NULL," +
                    "SeatsOpen INTEGER NOT NULL," +
                    "TermsOffered VARCHAR(11) NOT NULL," +
                    "DaysOfWeek VARCHAR(9) NOT NULL" +
                    ")");

            // Insert 15 rows of data
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Introduction to Programming', 'Dr. Smith', 3, 10, 'All', 'Monday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Introduction to Engineering', 'Dr. Bob', 3, 15, 'All', 'Tuesday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Calculus 3', 'Dr. Jeff', 3, 20, 'All', 'Wednesday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Introduction to Data Structures', 'Dr. Samantha', 3, 5, 'All', 'Thursday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Physics Mechanics', 'Dr. Sarah', 3, 7, 'All', 'Friday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Physics Electricity and Magnetism', 'Dr. Michelle', 3, 9, 'All', 'Monday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Introduction to English', 'Dr. Martin', 3, 10, 'All Fall', 'Tuesday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('English 2', 'Dr. Maria', 3, 10, 'All Spring', 'Wednesday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Introduction to Biology', 'Dr. Wilson', 4, 3, 'All Fall', 'Monday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Discrete Math', 'Dr. Jimmy', 3, 10, 'Fall 2025', 'Friday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Introduction to Database Management Systems', 'Dr. Arabghalizi', 3, 10, 'All', 'Monday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Introduction to Programming', 'Dr. Jim', 3, 12, 'All', 'Wednesday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Operating Systems', 'Dr. Nguyen', 4, 14, 'Spring 2024', 'Thursday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Computer Networks', 'Dr. Taylor', 3, 16, 'All', 'Tuesday')");
            this.st.executeUpdate(
                    "INSERT INTO Courses (CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek)" +
                            "VALUES ('Computer Architecture', 'Dr. Singh', 3, 18, 'All', 'Friday')");
        } catch (SQLException e) {
            System.out.println("Unable to create Courses table");
            e.printStackTrace();
        }
    }

    // Endpoint for student login
    @CrossOrigin(origins = "http://localhost:5173") 
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (this.conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error.");
        }
        String studentIdStr = loginRequest.getStudentID();
        if (studentIdStr == null || studentIdStr.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student ID is required.");
        }
        int studentId;
        try {
            studentId = Integer.parseInt(studentIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Student ID format. It must be a number.");
        }

        String sql = "SELECT StudentID FROM Students WHERE StudentID = ?";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Student found!!!
                return ResponseEntity.ok("Login successful");
            } else {
                // Student not found
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Student ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error during login for StudentID " + studentIdStr + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during login: " + e.getMessage());
        }
    }

}
