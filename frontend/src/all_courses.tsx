import CourseInfo from "./course_information/course_info";
import { useEffect, useState } from "react";
import axios from "axios";

interface Courses {
	courseID: number;
	courseName: string;
	professorName: string;
	seatsOpen: number;
	termsOffered: string[];
	daysOfWeek: string[];
}

const AllCourses = () => {
	// Keeps track of all the courses
	const [courses, setCourses] = useState<Courses[]>([]);
	// Keeps track of the courses that meet the user's search criteria
	const [filteredCourses, setFilteredCourses] = useState<Courses[]>([]);

	useEffect(() => {
		const fetchCourses = async () => {
			try {
				const response = await axios.get("http://localhost:8080/courses");
				setCourses(response.data);
				setFilteredCourses(response.data);
			} catch (error) {
				console.error("Error fetching courses:", error);
			}
		};

		fetchCourses();
	}, []);

	return (
		<>
			{/* Header */}
			<h1 className='mt-3 mb-4 font-bold text-center text-3xl'>All Courses</h1>

			{/* Courses Grid */}
			<div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3'>
				{filteredCourses.length > 0 ? (
					filteredCourses.map((course) => (
						<CourseInfo
							key={course.courseID}
							courseID={course.courseID}
							courseName={course.courseName}
							professorName={course.professorName}
							seatsOpen={course.seatsOpen}
							termsOffered={course.termsOffered}
							daysOfWeek={course.daysOfWeek}
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
