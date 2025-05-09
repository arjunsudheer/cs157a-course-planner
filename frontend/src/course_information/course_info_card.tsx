import axios from "axios";

interface CourseInfoProps {
	courseID: number;
	courseName: string;
	professorName: string;
	seatsOpen: number;
	termOffered: string;
	dayOfWeek: string;
}

const CourseInfoCard = ({
	courseID,
	courseName,
	professorName,
	seatsOpen,
	termOffered,
	dayOfWeek,
}: CourseInfoProps) => {
	const handleAddCourseToPlan = async (courseID: number) => {
		let studentID = localStorage.getItem("studentID");

		let term = prompt("Which term will you be taking this class?");
		let isRetaking = confirm(
			'Are you retaking this course? Press OK for "yes" and Cancel for "No."'
		);
		// Ignore if term is null
		if (term === null) return;

		try {
			await axios.post(`http://localhost:8080/plan/add/${studentID}`, {
				studentID,
				courseID,
				term,
				isRetaking,
			});
			alert(`Course ${courseID} added to student ${studentID}'s plan.`);
		} catch (error) {
			console.error("Error adding course to plan:", error);
			alert(
				"Invalid term. Year must be between 2025 and 2099. Only Fall, Spring, Winter, and Summer sessions allowed."
			);
		}
	};

	return (
		// Animate the Course Info box when hovering
		<div className='border-1 rounded-lg hover:bg-gray-100 duration-150 w-11/12 p-1 mt-7 mb-3 mx-auto text-center'>
			<h3 className='text-lg font-bold'>{courseName}</h3>
			<p>
				<span className='bold underline'>Course ID:</span> {courseID}
			</p>
			<p>
				<span className='bold underline'>Professor:</span> {professorName}
			</p>
			<p>
				<span className='bold underline'>Number of Seats Open:</span> {seatsOpen}
			</p>
			<p>
				<span className='bold underline'>Term Offered:</span> {termOffered}
			</p>
			<p>
				<span className='bold underline'>Day offered:</span> {dayOfWeek}
			</p>
			<button
				onClick={() => {
					handleAddCourseToPlan(courseID);
				}}
				className='mt-2 font-bold py-1 px-2 rounded text-sm bg-blue-200 hover:cursor-pointer hover:bg-blue-400'
			>
				Add Course
			</button>
		</div>
	);
};

export default CourseInfoCard;
