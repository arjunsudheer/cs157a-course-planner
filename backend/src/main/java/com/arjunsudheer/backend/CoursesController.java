package com.arjunsudheer.backend;

import java.sql.*;
import java.util.ArrayList;
import com.arjunsudheer.backend.Course;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://127.0.0.1:5173")
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

}
