import CourseInfoCard from "../course_information/course_info_card";
import { useEffect, useState } from "react";
import axios from "axios";
import SearchBar from "../search_tools/search";
import Filters from "../search_tools/filters";

interface Courses {
	courseID: number;
	courseName: string;
	professorName: string;
	seatsOpen: number;
	termOffered: string;
	dayOfWeek: string;
}

type TermOption = "all" | "Fall" | "Spring" | "Summer" | "Winter";
type DayOption = "all" | "Monday" | "Tuesday" | "Wednesday" | "Thursday" | "Friday";

const AllCourses = () => {
	// Keeps track of all the courses
	const [courses, setCourses] = useState<Courses[]>([]);
	const [currentQuery, setCurrentQuery] = useState("");
	const [termFilter, setTermFilter] = useState("all");
	const [dayFilter, setDayFilter] = useState("all");

	const handleSearchChange = (query: string) => {
		setCurrentQuery(query);
	};

	const handleTermChange = (value: TermOption) => {
		setTermFilter(value);
	};

	const handleDayChange = (value: DayOption) => {
		setDayFilter(value);
	};

	useEffect(() => {
		const fetchSearchResults = async () => {
			try {
				const params = new URLSearchParams();
				if (currentQuery) params.append("nameOrProfessor", currentQuery);
				if (termFilter !== "all") params.append("term", termFilter);
				if (dayFilter !== "all") params.append("day", dayFilter);

				const response = await axios.get(`http://localhost:8080/courses/search`, { params });
				setCourses(response.data);
			} catch (error) {
				console.error("Error fetching search results:", error);
			}
		};

		const timerId = setTimeout(() => {
			fetchSearchResults();
		}, 100);

		return () => clearTimeout(timerId);
	}, [currentQuery, termFilter, dayFilter]);

	return (
		<>
			{/* Header */}
			<h1 className='mt-3 mb-4 font-bold text-center text-3xl'>All Courses</h1>

			{/* Search and Filtering */}
			<SearchBar onSearchChange={handleSearchChange} />
			<Filters onTermChange={handleTermChange} onDayChange={handleDayChange} />

			{/* Courses Grid */}
			<div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3'>
				{courses.length > 0 ? (
					courses.map((course) => (
						<CourseInfoCard
							key={course.courseID}
							courseID={course.courseID}
							courseName={course.courseName}
							professorName={course.professorName}
							seatsOpen={course.seatsOpen}
							termOffered={course.termOffered}
							dayOfWeek={course.dayOfWeek}
						/>
					))
				) : (
					<div className='col-span-full text-center text-gray-700 text-lg'>
						No courses match your search.
					</div>
				)}
			</div>
		</>
	);
};

export default AllCourses;
