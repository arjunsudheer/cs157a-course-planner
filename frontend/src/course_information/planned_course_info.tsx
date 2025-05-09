import axios from "axios";

interface CourseInfoProps {
	courseID: number;
	courseName: string;
	professorName: string;
	term: string;
	dayOfWeek: string;
}

const PlannedCourseInfo = ({
	courseID,
	courseName,
	professorName,
	term,
	dayOfWeek,
}: CourseInfoProps) => {
	const handleRemoveCourseFromPlan = async () => {
		let studentID = localStorage.getItem("studentID");
		try {
			await axios.post(`http://localhost:8080/plan/remove/${studentID}`, {
				studentID,
				courseID,
				term,
			});
			window.location.reload();
		} catch (error) {
			console.error("Error removing course from plan:", error);
		}
	};

	const handleMovedCourseToGraded = async () => {
		let studentID = localStorage.getItem("studentID");

		let grade = prompt("what grade did you receive in this class?");
		// Ignore if the user enters null for their grade
		if (!grade) return;

		try {
			// First add the course to the grades table
			await axios.post(`http://localhost:8080/grades/add/${studentID}`, {
				studentID,
				courseID,
				term,
				grade,
			});
			// Then delete that course from the planned enrollments table
			await axios.post(`http://localhost:8080/plan/remove/${studentID}`, {
				studentID,
				courseID,
				term,
			});
			window.location.reload();
		} catch (error) {
			console.error("Error adding course to My Grades:", error);
			alert("Invalid grade.");
		}
	};

	return (
		// Create a new row with the course information
		<tr className='even:bg-gray-50 text-center'>
			<td className='border border-gray-300 px-4 py-2'>{courseName}</td>
			<td className='border border-gray-300 px-4 py-2'>{courseID}</td>
			<td className='border border-gray-300 px-4 py-2'>{professorName}</td>
			<td className='border border-gray-300 px-4 py-2'>
				{term === "All" ? (
					<select
						name='term'
						id='term'
						className='border border-gray-400 rounded px-2 py-1 w-full focus:outline-none focus:ring-2 focus:ring-blue-400'
					>
						<option value='Fall'>Fall</option>
						<option value='Spring'>Spring</option>
						<option value='Winter'>Winter</option>
						<option value='Summer'>Summer</option>
					</select>
				) : (
					<span>{term}</span>
				)}
			</td>

			<td className='border border-gray-300 px-4 py-2'>{dayOfWeek}</td>
			<td className='border border-gray-300 px-4 py-2'>
				<button
					className='bg-red-500 hover:bg-red-600 text-white font-semibold py-1 px-3 rounded hover:cursor-pointer'
					onClick={handleRemoveCourseFromPlan}
				>
					Remove
				</button>
			</td>
			<td className='border border-gray-300 px-4 py-2'>
				<button
					className='bg-green-500 hover:bg-green-600 text-white font-semibold py-1 px-3 rounded hover:cursor-pointer'
					onClick={handleMovedCourseToGraded}
				>
					Move to Graded Courses
				</button>
			</td>
		</tr>
	);
};

export default PlannedCourseInfo;
