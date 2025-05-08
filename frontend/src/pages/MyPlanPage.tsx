import React, { useState, useEffect } from "react";
import axios from "axios";
import SearchBar from "../search_tools/search";
import Filters from "../search_tools/filters";

interface Course {
  courseID: number;
  courseName: string;
  professorName: string;
  units: number;
  seatsOpen: number;
  termsOffered: string;
  daysOfWeek: string;
}

type GradeOption = "all" | "graded" | "ungraded";
type TermOption = "all" | "Fall" | "Spring" | "Summer" | "Winter";
type DayOption =
  | "all"
  | "Monday"
  | "Tuesday"
  | "Wednesday"
  | "Thursday"
  | "Friday";

const MyPlanPage: React.FC = () => {
  const [currentQuery, setCurrentQuery] = useState("");
  const [gradeFilter, setGradeFilter] = useState<GradeOption>("all");
  const [termFilter, setTermFilter] = useState<TermOption>("all");
  const [dayFilter, setDayFilter] = useState<DayOption>("all");
  const [searchResults, setSearchResults] = useState<Course[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [plannedCourses, setPlannedCourses] = useState<Course[]>([]);
  const [isPlanLoaded, setIsPlanLoaded] = useState(false);
  const [loggedInStudentId, setLoggedInStudentId] = useState<string | null>(
    null
  );

  useEffect(() => {
    const storedStudentId = localStorage.getItem("studentID");
    if (storedStudentId) {
      setLoggedInStudentId(storedStudentId);
    } else {
      console.warn(
        "No studentID found in localStorage. User might not be logged in."
      );
      
    }
  }, []); 

  // effect that allows us to load the initial plan from the backend when loggedInStudentId is set
  useEffect(() => {
    const fetchInitialPlan = async () => {
      if (!loggedInStudentId) return; 

      setIsPlanLoaded(false); 
      try {
        const response = await axios.get<Course[]>(
          `http://localhost:8080/students/${loggedInStudentId}/plan`
        );
        setPlannedCourses(response.data);
        console.log(
          `Initial plan loaded for student ${loggedInStudentId}:`,
          response.data
        );
      } catch (error) {
        console.error(
          `Error fetching initial plan for student ${loggedInStudentId}:`,
          error
        );
        setPlannedCourses([]);
      } finally {
        setIsPlanLoaded(true);
      }
    };

    if (loggedInStudentId) {
      fetchInitialPlan();
    }
  }, [loggedInStudentId]); 

  const updateBackendPlan = async (currentPlan: Course[]) => {
    if (!loggedInStudentId) {
      console.error("Cannot update plan, no student ID available.");
      return;
    }

    const payload = currentPlan.map((course) => ({
      courseID: course.courseID,
      term: course.termsOffered, 
      isRetaking: false, 
    }));

    try {
      const response = await axios.post(
        `http://localhost:8080/students/${loggedInStudentId}/plan`,
        payload
      );
      console.log(
        `Plan updated on backend for student ${loggedInStudentId}:`,
        response.data
      );
    } catch (error) {
      let errorMessage =
        "Error updating plan on backend. See console for details.";
      if (axios.isAxiosError(error) && error.response) {
        errorMessage = `Error updating plan: ${
          error.response.data || error.message
        }`;
      } else if (error instanceof Error) {
        errorMessage = `Error updating plan: ${error.message}`;
      }
      console.error(
        `Error updating plan on backend for student ${loggedInStudentId}:`,
        error
      );
      console.error(errorMessage);
    }
  };

  useEffect(() => {
    if (isPlanLoaded && loggedInStudentId) {
      updateBackendPlan(plannedCourses);
    }
  }, [plannedCourses, isPlanLoaded, loggedInStudentId]);

  useEffect(() => {
    const fetchSearchResults = async () => {
      if (
        !currentQuery &&
        gradeFilter === "all" &&
        termFilter === "all" &&
        dayFilter === "all"
      ) {
        setSearchResults([]);
        return;
      }

      setIsLoading(true);
      try {
        const params = new URLSearchParams();
        if (currentQuery) params.append("nameOrProfessor", currentQuery);
        if (gradeFilter !== "all") params.append("grade", gradeFilter);
        if (termFilter !== "all") params.append("term", termFilter);
        if (dayFilter !== "all") params.append("day", dayFilter);

        const response = await axios.get(
          `http://localhost:8080/courses/search`,
          { params }
        );
        setSearchResults(response.data);
      } catch (error) {
        console.error("Error fetching search results:", error);
        setSearchResults([]);
      } finally {
        setIsLoading(false);
      }
    };

    const timerId = setTimeout(() => {
      fetchSearchResults();
    }, 500);

    return () => clearTimeout(timerId);
  }, [currentQuery, gradeFilter, termFilter, dayFilter]);

  const handleSearchChange = (query: string) => {
    setCurrentQuery(query);
  };

  const handleGradeChange = (value: GradeOption) => {
    setGradeFilter(value);
  };

  const handleTermChange = (value: TermOption) => {
    setTermFilter(value);
  };

  const handleDayChange = (value: DayOption) => {
    setDayFilter(value);
  };

  const handleAddCourseToPlan = (courseToAdd: Course) => {
    if (
      !plannedCourses.find((course) => course.courseID === courseToAdd.courseID)
    ) {
      setPlannedCourses((prevCourses) => [...prevCourses, courseToAdd]);
    }
  };

  const handleRemoveCourseFromPlan = (courseIdToRemove: number) => {
    setPlannedCourses((prevCourses) =>
      prevCourses.filter((course) => course.courseID !== courseIdToRemove)
    );
  };

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4 text-center">
        My Course Plan & Search
      </h1>

      <h2 className="text-xl font-semibold mt-6 mb-3 text-center">
        Add Courses to Plan
      </h2>
      <SearchBar onSearchChange={handleSearchChange} />
      <Filters
        onGradeChange={handleGradeChange}
        onTermChange={handleTermChange}
        onDayChange={handleDayChange}
      />

      <div className="mt-4 text-center text-sm text-gray-600">
        <p>
          Current Query: {currentQuery || "None"}, Grade: {gradeFilter}, Term:{" "}
          {termFilter}, Day: {dayFilter}
        </p>
      </div>

      {/* Display Search Results */}
      <div className="mt-8">
        <h3 className="text-lg font-semibold mb-2 text-center">
          Search Results
        </h3>
        {isLoading ? (
          <p className="text-center">Loading...</p>
        ) : searchResults.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {searchResults.map((course) => (
              <div
                key={course.courseID}
                className="border p-4 rounded-lg shadow"
              >
                <h4 className="font-bold">{course.courseName}</h4>
                <p>Professor: {course.professorName}</p>
                <p>Units: {course.units}</p>
                <p>Seats Open: {course.seatsOpen}</p>
                <p>Terms: {course.termsOffered}</p>
                <p>Days: {course.daysOfWeek}</p>
                <button
                  onClick={() => handleAddCourseToPlan(course)}
                  className="mt-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-1 px-2 rounded text-sm"
                >
                  + Add to Plan
                </button>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-center">
            No courses match your criteria. Try broadening your search.
          </p>
        )}
      </div>

      <div className="mt-8 border-t pt-4">
        <h2 className="text-xl font-semibold mb-3 text-center">
          My Current Plan
        </h2>
        {plannedCourses.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-4">
            {plannedCourses.map((course) => (
              <div
                key={`plan-${course.courseID}`}
                className="border p-4 rounded-lg shadow bg-green-50"
              >
                <h4 className="font-bold">{course.courseName}</h4>
                <p>Professor: {course.professorName}</p>
                <p>Units: {course.units}</p>
                <button
                  onClick={() => handleRemoveCourseFromPlan(course.courseID)}
                  className="mt-2 bg-red-500 hover:bg-red-700 text-white font-bold py-1 px-2 rounded text-sm"
                >
                  Remove
                </button>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-center text-gray-500">
            Your plan is empty. Add courses from the search results above.
          </p>
        )}
      </div>
    </div>
  );
};

export default MyPlanPage;
