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

const MyPlanPage: React.FC = () => { // Renamed component
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
      <h1 className="text-2xl font-bold mb-4 text-center">My Course Plan & Search</h1> {/* Updated heading */}
      {/* TODO: Add actual plan display here eventually */}
      
      <h2 className="text-xl font-semibold mt-6 mb-3 text-center">Add Courses to Plan</h2>
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
      {/* Placeholder for displaying the user's actual plan items */}
      <div className="mt-8 border-t pt-4">
        <h2 className="text-xl font-semibold mb-3 text-center">My Current Plan</h2>
        <p className="text-center text-gray-500">(Plan display will go here)</p>
      </div>
    </div>
  );
};

export default MyPlanPage; // Export renamed component