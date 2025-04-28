package com.arjunsudheer.backend;

// Used for packaging the data for each course to send to the frontend
public class Course {
    private int courseID;
    private String courseName;
    private String professorName;
    private int units;
    private int seatsOpen;
    private String termsOffered;
    private String daysOfWeek;

    public Course(int courseID, String courseName, String professorName, int units, int seatsOpen,
            String termsOffered, String daysOfWeek) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.professorName = professorName;
        this.units = units;
        this.seatsOpen = seatsOpen;
        this.termsOffered = termsOffered;
        this.daysOfWeek = daysOfWeek;
    }

    // Getters used by Spring Boot to convert to JSON for frontend
    public int getCourseID() {
        return courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getProfessorName() {
        return professorName;
    }

    public int getUnits() {
        return units;
    }

    public int getSeatsOpen() {
        return seatsOpen;
    }

    public String getTermsOffered() {
        return termsOffered;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }
}