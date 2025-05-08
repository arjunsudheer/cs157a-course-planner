import React, { useState, useEffect } from "react";
import axios from "axios";
import SearchBar from "../search_tools/search";
import Filters from "../search_tools/filters";

// Define the Course interface (assuming similar to AllCourses.tsx)
interface Course {
  courseID: number;
  courseName: string;
  professorName: string;
  units: number; // Added units based on your CourseController
  seatsOpen: number;
  termsOffered: string; // In backend it's a single string, might need adjustment if frontend expects array
  daysOfWeek: string; // In backend it's a single string, might need adjustment
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
  const [searchResults, setSearchResults] = useState<Course[]>([]); // State for search results
  const [isLoading, setIsLoading] = useState(false); // Optional: for loading state

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
        // Construct query parameters
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
        setSearchResults([]); // Clear results on error
      }
      setIsLoading(false);
    };

    const timerId = setTimeout(() => {
      fetchSearchResults();
    }, 500); 

    return () => clearTimeout(timerId); // Cleanup timeout
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
              // Assuming you have a CourseInfo component or similar
              // For now, just displaying basic info
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
        <p className="text-center text-gray-500">(Plan display will go here)</p>
      </div>
    </div>
  );
};

export default MyPlanPage;
