interface CourseInfoProps {
	courseID: number;
	courseName: string;
	professorName: string;
	seatsOpen: number;
	term: string;
	dayOfWeek: string;
	grade: string;
}

const GradedCourseInfo = ({
	courseID,
	courseName,
	professorName,
	seatsOpen,
	term,
	dayOfWeek,
	grade,
}: CourseInfoProps) => {
	return (
		// Create a new row with the course information
		<tr className='even:bg-gray-50 text-center'>
			<td className='border border-gray-300 px-4 py-2'>{courseName}</td>
			<td className='border border-gray-300 px-4 py-2'>{courseID}</td>
			<td className='border border-gray-300 px-4 py-2'>{professorName}</td>
			<td className='border border-gray-300 px-4 py-2'>{seatsOpen}</td>
			<td className='border border-gray-300 px-4 py-2'>{term}</td>
			<td className='border border-gray-300 px-4 py-2'>{dayOfWeek}</td>
			<td className='border border-gray-300 px-4 py-2'>{grade}</td>
		</tr>
	);
};

export default GradedCourseInfo;
