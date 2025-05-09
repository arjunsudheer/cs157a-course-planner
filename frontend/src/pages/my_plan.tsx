import React, { useState, useEffect } from "react";
import axios from "axios";
import GradedCourseInfo from "../course_information/graded_course_info";
import PlannedCourseInfo from "../course_information/planned_course_info";

interface Course {
	courseID: number;
	courseName: string;
	professorName: string;
	units: number;
	seatsOpen: number;
	termOffered: string;
	dayOfWeek: string;
	grade: string;
}

const MyPlan: React.FC = () => {
	const [plannedCourses, setPlannedCourses] = useState<Course[]>([]);
	const [gradedCourses, setGradedCourses] = useState<Course[]>([]);
	const [isPlanLoaded, setIsPlanLoaded] = useState(false);
	const [loggedInStudentId, setLoggedInStudentId] = useState<string | null>(null);

	useEffect(() => {
		const storedStudentId = localStorage.getItem("studentID");
		if (storedStudentId) {
			setLoggedInStudentId(storedStudentId);
		} else {
			console.warn("No studentID found in localStorage. User might not be logged in.");
		}
	}, []);

	// effect that allows us to load the initial plan from the backend when loggedInStudentId is set
	useEffect(() => {
		const fetchInitialPlan = async () => {
			if (!loggedInStudentId) return;

			setIsPlanLoaded(false);
			try {
				const planResponse = await axios.get<Course[]>(
					`http://localhost:8080/plan/${loggedInStudentId}`
				);
				setPlannedCourses(planResponse.data);
				console.log(`Initial plan loaded for student ${loggedInStudentId}:`, planResponse.data);

				const gradesResponse = await axios.get<Course[]>(
					`http://localhost:8080/grades/${loggedInStudentId}`
				);
				setGradedCourses(gradesResponse.data);
				console.log(`Initial grades loaded for student ${loggedInStudentId}:`, gradesResponse.data);
			} catch (error) {
				console.error(
					`Error fetching initial plan or grades for student ${loggedInStudentId}:`,
					error
				);
				setPlannedCourses([]);
				setGradedCourses([]);
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
			term: course.termOffered,
			isRetaking: false,
		}));

		try {
			const response = await axios.post(`http://localhost:8080/plan/${loggedInStudentId}`, payload);
			console.log(`Plan updated on backend for student ${loggedInStudentId}:`, response.data);
		} catch (error) {
			let errorMessage = "Error updating plan on backend. See console for details.";
			if (axios.isAxiosError(error) && error.response) {
				errorMessage = `Error updating plan: ${error.response.data || error.message}`;
			} else if (error instanceof Error) {
				errorMessage = `Error updating plan: ${error.message}`;
			}
			console.error(`Error updating plan on backend for student ${loggedInStudentId}:`, error);
			console.error(errorMessage);
		}
	};

	useEffect(() => {
		if (isPlanLoaded && loggedInStudentId) {
			updateBackendPlan(plannedCourses);
		}
	}, [plannedCourses, isPlanLoaded, loggedInStudentId]);

	return (
		<div className='container mx-auto p-4'>
			<h1 className='text-2xl font-bold mb-4 text-center'>My Grades</h1>

			<table className='w-11/12 mx-auto table-auto border border-gray-400 mb-4'>
				<thead>
					<tr className='bg-gray-200'>
						<th className='border border-gray-400 px-4 py-2'>Course Name</th>
						<th className='border border-gray-400 px-4 py-2'>Course ID</th>
						<th className='border border-gray-400 px-4 py-2'>Professor</th>
						<th className='border border-gray-400 px-4 py-2'>Number of Seats Open</th>
						<th className='border border-gray-400 px-4 py-2'>Term</th>
						<th className='border border-gray-400 px-4 py-2'>Day Of Week</th>
						<th className='border border-gray-400 px-4 py-2'>Grade</th>
					</tr>
				</thead>
				<tbody>
					{gradedCourses.map((course, index) => (
						<GradedCourseInfo
							key={index}
							courseID={course.courseID}
							courseName={course.courseName}
							professorName={course.professorName}
							seatsOpen={course.seatsOpen}
							term={course.termOffered}
							dayOfWeek={course.dayOfWeek}
							grade={course.grade}
						/>
					))}
				</tbody>
			</table>

			<hr />
			<h1 className='text-2xl font-bold mb-4 text-center pt-4'>My Course Plan</h1>

			<table className='w-11/12 mx-auto table-auto border border-gray-400 mb-4'>
				<thead>
					<tr className='bg-gray-200'>
						<th className='border border-gray-400 px-4 py-2'>Course Name</th>
						<th className='border border-gray-400 px-4 py-2'>Course ID</th>
						<th className='border border-gray-400 px-4 py-2'>Professor</th>
						<th className='border border-gray-400 px-4 py-2'>Number of Seats Open</th>
						<th className='border border-gray-400 px-4 py-2'>Term</th>
						<th className='border border-gray-400 px-4 py-2'>Day Offered</th>
						<th className='border border-gray-400 px-4 py-2'>Remove?</th>
						<th className='border border-gray-400 px-4 py-2'>Completed?</th>
					</tr>
				</thead>
				<tbody>
					{plannedCourses.map((course, index) => (
						<PlannedCourseInfo
							key={index}
							courseID={course.courseID}
							courseName={course.courseName}
							professorName={course.professorName}
							seatsOpen={course.seatsOpen}
							term={course.termOffered}
							dayOfWeek={course.dayOfWeek}
						/>
					))}
				</tbody>
			</table>
		</div>
	);
};

export default MyPlan;
