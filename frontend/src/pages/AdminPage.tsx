import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

interface Course {
  courseID: number;
  courseName: string;
  professorName: string;
  units: number;
  seatsOpen: number;
  termsOffered: string;
  daysOfWeek: string;
}

interface Student {
  studentID: number;
  studentName: string;
  year: string;
}

const AdminPage: React.FC = () => {
  const navigate = useNavigate();
  const [courses, setCourses] = useState<Course[]>([]);
  const [students, setStudents] = useState<Student[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState<Student[]>([]);
  const [newCourse, setNewCourse] = useState({
    courseName: "",
    professorName: "",
    units: 3,
    seatsOpen: 10,
    termsOffered: "All",
    daysOfWeek: "Monday"
  });
  const [newStudent, setNewStudent] = useState({
    studentID: "",
    studentName: "",
    year: "Freshman"
  });

  useEffect(() => {
    const isAdmin = localStorage.getItem("isAdmin") === "true";
    const studentID = localStorage.getItem("studentID");
    
    if (!isAdmin || studentID !== "0") {
      navigate("/");
      return;
    }

    fetchCourses();
    fetchStudents();
  }, [navigate]);

  const fetchCourses = async () => {
    try {
      const response = await axios.get("http://localhost:8080/courses");
      setCourses(response.data);
    } catch (error) {
      console.error("Error fetching courses:", error);
    }
  };

  const fetchStudents = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/admin/students");
      setStudents(response.data);
    } catch (error) {
      console.error("Error fetching students:", error);
    }
  };

  const handleAddCourse = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const studentID = localStorage.getItem("studentID");
      await axios.post(`http://localhost:8080/api/admin/courses?studentID=${studentID}`, newCourse);
      setNewCourse({
        courseName: "",
        professorName: "",
        units: 3,
        seatsOpen: 10,
        termsOffered: "All",
        daysOfWeek: "Monday"
      });
      fetchCourses();
    } catch (error) {
      console.error("Error adding course:", error);
      alert("Failed to add course. Please try again.");
    }
  };

  const handleDeleteCourse = async (courseId: number) => {
    if (!window.confirm("Are you sure you want to delete this course?")) return;
    try {
      const studentID = localStorage.getItem("studentID");
      await axios.delete(`http://localhost:8080/api/admin/courses/${courseId}?studentID=${studentID}`);
      fetchCourses();
    } catch (error) {
      console.error("Error deleting course:", error);
      alert("Failed to delete course. Please try again.");
    }
  };

  const handleAddStudent = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validate student ID format
    if (!newStudent.studentID.match(/^S\d{3}$/)) {
      alert("Student ID must be in format SXXX (e.g., S001)");
      return;
    }

    try {
      const studentID = localStorage.getItem("studentID");
      const response = await axios.post(`http://localhost:8080/api/admin/students?studentID=${studentID}`, newStudent);
      if (response.data.status === "success") {
        setNewStudent({
          studentID: "",
          studentName: "",
          year: "Freshman"
        });
        fetchStudents();
      } else {
        alert(response.data.message || "Failed to add student. Please try again.");
      }
    } catch (error) {
      console.error("Error adding student:", error);
      if (axios.isAxiosError(error) && error.response) {
        alert(error.response.data || "Failed to add student. Please try again.");
      } else {
        alert("Failed to add student. Please try again.");
      }
    }
  };

  const handleDeleteStudent = async (studentId: number) => {
    if (!window.confirm("Are you sure you want to delete this student?")) return;
    try {
      const studentID = localStorage.getItem("studentID");
      await axios.delete(`http://localhost:8080/api/admin/students/${studentId}?studentID=${studentID}`);
      fetchStudents();
    } catch (error) {
      console.error("Error deleting student:", error);
      alert("Failed to delete student. Please try again.");
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      alert("Please enter a search query");
      return;
    }

    try {
      const response = await axios.get(`http://localhost:8080/api/admin/students/search?query=${encodeURIComponent(searchQuery)}`);
      setSearchResults(response.data);
    } catch (error) {
      console.error("Error searching students:", error);
      if (axios.isAxiosError(error) && error.response) {
        alert(error.response.data || "Error searching students");
      } else {
        alert("Error searching students");
      }
    }
  };

  const handleDeleteBySearch = async () => {
    if (!searchQuery.trim()) {
      alert("Please enter a search query");
      return;
    }

    if (!window.confirm(`Are you sure you want to delete all students matching "${searchQuery}"?`)) {
      return;
    }

    try {
      const studentID = localStorage.getItem("studentID");
      const response = await axios.delete(`http://localhost:8080/api/admin/students/search?query=${encodeURIComponent(searchQuery)}&studentID=${studentID}`);
      alert(response.data.message);
      setSearchQuery("");
      setSearchResults([]);
      fetchStudents();
    } catch (error) {
      console.error("Error deleting students:", error);
      if (axios.isAxiosError(error) && error.response) {
        alert(error.response.data || "Error deleting students");
      } else {
        alert("Error deleting students");
      }
    }
  };

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>

      {/* Course Management Section */}
      <div className="mb-8">
        <h2 className="text-2xl font-bold mb-4">Course Management</h2>
        <form onSubmit={handleAddCourse} className="bg-gray-100 p-4 rounded-lg mb-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <input
              type="text"
              placeholder="Course Name"
              value={newCourse.courseName}
              onChange={(e) => setNewCourse({ ...newCourse, courseName: e.target.value })}
              className="border p-2 rounded"
              required
            />
            <input
              type="text"
              placeholder="Professor Name"
              value={newCourse.professorName}
              onChange={(e) => setNewCourse({ ...newCourse, professorName: e.target.value })}
              className="border p-2 rounded"
              required
            />
            <input
              type="number"
              placeholder="Units"
              value={newCourse.units}
              onChange={(e) => setNewCourse({ ...newCourse, units: parseInt(e.target.value) })}
              className="border p-2 rounded"
              min="1"
              max="4"
              required
            />
            <input
              type="number"
              placeholder="Seats Open"
              value={newCourse.seatsOpen}
              onChange={(e) => setNewCourse({ ...newCourse, seatsOpen: parseInt(e.target.value) })}
              className="border p-2 rounded"
              min="1"
              max="100"
              required
            />
            <select
              value={newCourse.termsOffered}
              onChange={(e) => setNewCourse({ ...newCourse, termsOffered: e.target.value })}
              className="border p-2 rounded"
              required
            >
              <option value="All">All</option>
              <option value="Spring">Spring</option>
              <option value="Fall">Fall</option>
              <option value="Winter">Winter</option>
              <option value="Summer">Summer</option>
            </select>
            <select
              value={newCourse.daysOfWeek}
              onChange={(e) => setNewCourse({ ...newCourse, daysOfWeek: e.target.value })}
              className="border p-2 rounded"
              required
            >
              <option value="Monday">Monday</option>
              <option value="Tuesday">Tuesday</option>
              <option value="Wednesday">Wednesday</option>
              <option value="Thursday">Thursday</option>
              <option value="Friday">Friday</option>
            </select>
          </div>
          <button type="submit" className="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
            Add Course
          </button>
        </form>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {courses.map((course) => (
            <div key={course.courseID} className="bg-white p-4 rounded-lg shadow">
              <h3 className="font-bold">{course.courseName}</h3>
              <p>Professor: {course.professorName}</p>
              <p>Units: {course.units}</p>
              <p>Seats: {course.seatsOpen}</p>
              <p>Term: {course.termsOffered}</p>
              <p>Day: {course.daysOfWeek}</p>
              <button
                onClick={() => handleDeleteCourse(course.courseID)}
                className="mt-2 bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
              >
                Delete
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* Student Management Section */}
      <div>
        <h2 className="text-2xl font-bold mb-4">Student Management</h2>

        {/* Search and Delete Section */}
        <div className="bg-gray-100 p-4 rounded-lg mb-4">
          <h3 className="text-xl font-semibold mb-2">Search and Delete Students</h3>
          <div className="flex gap-2">
            <input
              type="text"
              placeholder="Search by Student ID or Name"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="border p-2 rounded flex-grow"
            />
            <button
              onClick={handleSearch}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Search
            </button>
            <button
              onClick={handleDeleteBySearch}
              className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
            >
              Delete Matches
            </button>
          </div>

          {/* Search Results */}
          {searchResults.length > 0 && (
            <div className="mt-4">
              <h4 className="font-semibold mb-2">Search Results:</h4>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {searchResults.map((student) => (
                  <div key={student.studentID} className="bg-white p-4 rounded-lg shadow">
                    <h3 className="font-bold">{student.studentName}</h3>
                    <p>ID: {student.studentID}</p>
                    <p>Year: {student.year}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Add Student Form */}
        <form onSubmit={handleAddStudent} className="bg-gray-100 p-4 rounded-lg mb-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <input
                type="text"
                placeholder="Student ID (SXXX)"
                value={newStudent.studentID}
                onChange={(e) => {
                  const value = e.target.value.toUpperCase();
                  if (value === "" || /^S\d{0,3}$/.test(value)) {
                    setNewStudent({ ...newStudent, studentID: value });
                  }
                }}
                className="border p-2 rounded w-full"
                required
                pattern="S\d{3}"
                title="Student ID must be in format SXXX (e.g., S001)"
              />
              <p className="text-sm text-gray-500 mt-1">Format: SXXX (e.g., S001)</p>
            </div>
            <input
              type="text"
              placeholder="Student Name"
              value={newStudent.studentName}
              onChange={(e) => setNewStudent({ ...newStudent, studentName: e.target.value })}
              className="border p-2 rounded"
              required
            />
            <select
              value={newStudent.year}
              onChange={(e) => setNewStudent({ ...newStudent, year: e.target.value })}
              className="border p-2 rounded"
              required
            >
              <option value="Freshman">Freshman</option>
              <option value="Sophomore">Sophomore</option>
              <option value="Junior">Junior</option>
              <option value="Senior">Senior</option>
            </select>
          </div>
          <button type="submit" className="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
            Add Student
          </button>
        </form>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {students.map((student) => (
            <div key={student.studentID} className="bg-white p-4 rounded-lg shadow">
              <h3 className="font-bold">{student.studentName}</h3>
              <p>Year: {student.year}</p>
              <button
                onClick={() => handleDeleteStudent(student.studentID)}
                className="mt-2 bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
              >
                Delete
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default AdminPage; 