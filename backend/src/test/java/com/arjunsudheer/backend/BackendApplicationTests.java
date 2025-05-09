package com.arjunsudheer.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testAdminLogin() throws Exception {
		// Test successful admin login
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"studentID\": \"0\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isAdmin").value(true))
				.andExpect(jsonPath("$.status").value("success"));

		// Test non-admin login attempt
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"studentID\": \"S001\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isAdmin").value(false));
	}

	@Test
	public void testStudentManagement() throws Exception {
		// Test adding a new student as admin
		mockMvc.perform(post("/api/admin/students")
				.param("studentID", "0")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"studentName\":\"Test Student\",\"nickname\":\"Test\",\"year\":\"Freshman\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.message").value("Student added successfully"));

		// Test adding a student as non-admin
		mockMvc.perform(post("/api/admin/students")
				.param("studentID", "S001")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"studentName\":\"Test Student\",\"nickname\":\"Test\",\"year\":\"Freshman\"}"))
				.andExpect(status().isUnauthorized());

		// Test getting all students
		mockMvc.perform(get("/students"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void testCourseManagement() throws Exception {
		// Test adding a new course as admin
		mockMvc.perform(post("/api/admin/courses")
				.param("studentID", "0")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"courseName\":\"Test Course\",\"professorName\":\"Test Prof\",\"units\":3,\"seatsOpen\":10,\"termsOffered\":\"All\",\"daysOfWeek\":\"Monday\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.message").value("Course added successfully"));

		// Test adding a course as non-admin
		mockMvc.perform(post("/api/admin/courses")
				.param("studentID", "S001")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"courseName\":\"Test Course\",\"professorName\":\"Test Prof\",\"units\":3,\"seatsOpen\":10,\"termsOffered\":\"All\",\"daysOfWeek\":\"Monday\"}"))
				.andExpect(status().isUnauthorized());

		// Test getting all courses
		mockMvc.perform(get("/courses"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void testInvalidInputs() throws Exception {
		// Test login with empty student ID
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"studentID\": \"\"}"))
				.andExpect(status().isBadRequest());

		// Test adding student with invalid year
		mockMvc.perform(post("/api/admin/students")
				.param("studentID", "0")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"studentName\":\"Test Student\",\"nickname\":\"Test\",\"year\":\"Invalid\"}"))
				.andExpect(status().isInternalServerError());

		// Test adding course with invalid units
		mockMvc.perform(post("/api/admin/courses")
				.param("studentID", "0")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"courseName\":\"Test Course\",\"professorName\":\"Test Prof\",\"units\":5,\"seatsOpen\":10,\"termsOffered\":\"All\",\"daysOfWeek\":\"Monday\"}"))
				.andExpect(status().isInternalServerError());
	}
}
