import React, { useState } from "react";

interface SearchBarProps {
  onSearchChange: (query: string) => void;
}

const SearchBar: React.FC<SearchBarProps> = ({ onSearchChange }) => {
  const [query, setQuery] = useState("");

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newQuery = event.target.value;
    setQuery(newQuery);
    onSearchChange(newQuery); 
  };

  return (
    <div className="flex justify-center items-center p-4">
      <input
        type="text"
        className="w-full max-w-md p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" 
        value={query}
        onChange={handleInputChange}
        placeholder="Search by course or professor..." 
      />
    </div>
  );
};

export default SearchBar;
