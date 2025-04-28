interface CourseInfoProps {
	courseID: number;
	courseName: string;
	professorName: string;
	seatsOpen: number;
	termsOffered: string[];
	daysOfWeek: string[];
}

const CourseInfo = ({
	courseID,
	courseName,
	professorName,
	seatsOpen,
	termsOffered,
	daysOfWeek,
}: CourseInfoProps) => {
	return (
		// Animate the Course Info box when hovering
		<div className='bg-gray-200 rounded-lg shadow-lg transition ease-in-out delay-75 hover:-translate-y-1 hover:bg-gray-300 duration-150 w-11/12 p-1 mt-7 mb-3 mx-auto text-center'>
			<h3 className='m-1 font-bold text-xl'>{courseName}</h3>

			{/* Display Course ID information */}
			<p className='m-1'>
				<span className='bold underline'>Course ID:</span> {courseID}
			</p>
			{/* Display Professor Name information */}
			<p className='m-1'>
				<span className='bold underline'>Professor:</span> {professorName}
			</p>
			{/* Display Number of Seats Open information */}
			<p className='m-1'>
				<span className='bold underline'>Number of Seats Open:</span> {seatsOpen}
			</p>
			{/* Display the terms this course of offered */}
			<p className='m-1'>
				<span className='bold underline'>Terms Offered:</span> {termsOffered}
			</p>
			{/* Display the days of the week the class is held */}
			<p className='m-1'>
				<span className='bold underline'>Days offered:</span> {daysOfWeek}
			</p>
		</div>
	);
};

export default CourseInfo;
