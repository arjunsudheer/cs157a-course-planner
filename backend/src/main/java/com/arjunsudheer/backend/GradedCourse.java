package com.arjunsudheer.backend;

// Used for packaging the data for each course to send to the frontend
public class GradedCourse {
    private int courseID;
    private String courseName;
    private String professorName;
    private int units;
    private int seatsOpen;
    private String termsOffered;
    private String daysOfWeek;
    private String grade;

    public GradedCourse(int courseID, String courseName, String professorName, int units, int seatsOpen,
            String termsOffered, String daysOfWeek, String grade) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.professorName = professorName;
        this.units = units;
        this.seatsOpen = seatsOpen;
        this.termsOffered = termsOffered;
        this.daysOfWeek = daysOfWeek;
        this.grade = grade;
    }

    // Getters and setters used by Spring Boot to serialize and deserialize objects
    // when sending data to frontend
    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

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

    public String getTermOffered() {
        return termsOffered;
    }

    public void setTermOffered(String termsOffered) {
        this.termsOffered = termsOffered;
    }

    public String getDayOfWeek() {
        return daysOfWeek;
    }

    public void setDayOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}