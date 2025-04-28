import React, { useState } from "react";
import SearchBar from "../search_tools/search";
import Filters from "../search_tools/filters";

type GradeOption = "all" | "graded" | "ungraded";
type TermOption = "all" | "Fall" | "Spring" | "Summer" | "Winter";
type DayOption =
  | "all"
  | "Monday"
  | "Tuesday"
  | "Wednesday"
  | "Thursday"
  | "Friday";

const SearchPage: React.FC = () => {
  const [currentQuery, setCurrentQuery] = useState("");
  // State for filters
  const [gradeFilter, setGradeFilter] = useState<GradeOption>("all");
  const [termFilter, setTermFilter] = useState<TermOption>("all");
  const [dayFilter, setDayFilter] = useState<DayOption>("all");

  // Handler for search input
  const handleSearchChange = (query: string) => {
    setCurrentQuery(query);
    console.log("Search query:", query);
    // Trigger search with query AND filters here
    console.log("Filters:", { gradeFilter, termFilter, dayFilter });
  };

  // Handlers for filter changes
  const handleGradeChange = (value: GradeOption) => {
    setGradeFilter(value);
    console.log("Grade filter:", value);
    // Trigger search with query AND filters here
    console.log("Search query:", currentQuery);
    console.log("Filters:", { gradeFilter: value, termFilter, dayFilter });
  };

  const handleTermChange = (value: TermOption) => {
    setTermFilter(value);
    console.log("Term filter:", value);
    // Trigger search with query AND filters here
    console.log("Search query:", currentQuery);
    console.log("Filters:", { gradeFilter, termFilter: value, dayFilter });
  };

  const handleDayChange = (value: DayOption) => {
    setDayFilter(value);
    console.log("Day filter:", value);
    // Trigger search with query AND filters here
    console.log("Search query:", currentQuery);
    console.log("Filters:", { gradeFilter, termFilter, dayFilter: value });
  };

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4 text-center">Search Courses</h1>
      <SearchBar onSearchChange={handleSearchChange} />
      {/* Render Filters component */}
      <Filters
        onGradeChange={handleGradeChange}
        onTermChange={handleTermChange}
        onDayChange={handleDayChange}
      />

      {/* Display current filters (optional) */}
      <div className="mt-4 text-center text-sm text-gray-600">
        <p>
          Current Filters: Grade: {gradeFilter}, Term: {termFilter}, Day:{" "}
          {dayFilter}
        </p>
      </div>

      {/* You can display search results here based on currentQuery AND filters */}
      {currentQuery && (
        <div className="mt-4 text-center">
          <p>
            Searching for: <strong>{currentQuery}</strong>
          </p>
          {/* Placeholder for results */}
        </div>
      )}
    </div>
  );
};

export default SearchPage;
