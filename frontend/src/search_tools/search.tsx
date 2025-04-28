import React, { useState } from "react";

// Define the props interface
interface SearchBarProps {
  onSearchChange: (query: string) => void;
}

const SearchBar: React.FC<SearchBarProps> = ({ onSearchChange }) => {
  const [query, setQuery] = useState("");

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newQuery = event.target.value;
    setQuery(newQuery);
    onSearchChange(newQuery); // Call the prop function
  };

  return (
    <div className="flex justify-center items-center p-4">
      <input
        type="text"
        className="w-full max-w-md p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" // Tailwind classes for the input
        value={query}
        onChange={handleInputChange}
        placeholder="Search by course or professor..." // Updated placeholder
      />
      {/* Removed the label and query display paragraph */}
    </div>
  );
};

export default SearchBar;
