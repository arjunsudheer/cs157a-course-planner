import React from "react";

// Define types for filter options
type GradeOption = "all" | "graded" | "ungraded";
type TermOption = "all" | "Fall" | "Spring" | "Summer" | "Winter";
type DayOption =
  | "all"
  | "Monday"
  | "Tuesday"
  | "Wednesday"
  | "Thursday"
  | "Friday";

// Define props interface for the Filters component
interface FiltersProps {
  onGradeChange: (value: GradeOption) => void;
  onTermChange: (value: TermOption) => void;
  onDayChange: (value: DayOption) => void;
}

const Filters: React.FC<FiltersProps> = ({
  onGradeChange,
  onTermChange,
  onDayChange,
}) => {
  return (
    <div className="flex flex-wrap justify-center gap-4 p-4">
      {/* Graded/Ungraded Filter */}
      <div>
        <label htmlFor="grade-filter" className="mr-2 font-medium">
          Grade:
        </label>
        <select
          id="grade-filter"
          className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          onChange={(e) => onGradeChange(e.target.value as GradeOption)}
        >
          <option value="all">All</option>
          <option value="graded">Graded</option>
          <option value="ungraded">Ungraded</option>
        </select>
      </div>

      {/* Term Filter */}
      <div>
        <label htmlFor="term-filter" className="mr-2 font-medium">
          Term:
        </label>
        <select
          id="term-filter"
          className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          onChange={(e) => onTermChange(e.target.value as TermOption)}
          // defaultValue="all" // Or bind to selectedTerm prop
        >
          <option value="all">All</option>
          <option value="Fall">Fall</option>
          <option value="Spring">Spring</option>
          <option value="Summer">Summer</option>
          <option value="Winter">Winter</option>
        </select>
      </div>

      {/* Day of Week Filter */}
      <div>
        <label htmlFor="day-filter" className="mr-2 font-medium">
          Day:
        </label>
        <select
          id="day-filter"
          className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          onChange={(e) => onDayChange(e.target.value as DayOption)}
          // defaultValue="all" // Or bind to selectedDay prop
        >
          <option value="all">All</option>
          <option value="Monday">Monday</option>
          <option value="Tuesday">Tuesday</option>
          <option value="Wednesday">Wednesday</option>
          <option value="Thursday">Thursday</option>
          <option value="Friday">Friday</option>
        </select>
      </div>
    </div>
  );
};

export default Filters;
