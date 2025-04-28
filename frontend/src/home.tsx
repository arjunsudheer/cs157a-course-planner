import { useState } from "react";
import { useNavigate } from "react-router-dom";

const Home = () => {
	const [studentID, setStudentID] = useState("");
	const navigate = useNavigate();

	const handleLogin = async (e: { preventDefault: () => void }) => {
		e.preventDefault();
		if (studentID) {
			navigate("/courses");
		} else {
			alert("Please enter your Student ID.");
		}
	};

	return (
		// Place the login box in the center of the screen
		<div className='h-screen'>
			<div className='flex flex-col items-center justify-center h-screen'>
				<h2 className='text-3xl mb-6 font-bold'>Enter your Student ID</h2>
				<div className='flex justify-evenly'>
					{/* Allow the student to enter their 9-digit student ID number */}
					<form onSubmit={handleLogin} className='bg-gray-200 p-8 rounded-lg shadow-md w-96'>
						<div className='mb-6'>
							<input
								type='number'
								id='studentID'
								value={studentID}
								onChange={(e) => setStudentID(e.target.value)}
								className='border border-black rounded p-3 w-full focus:outline-none focus:ring-2 focus:ring-blue-500'
								placeholder='Ex: 123456789'
								min={0}
								max={999999999}
								required
							/>
						</div>

						<button
							type='submit'
							className='bg-green-500 text-white rounded p-3 w-full hover:bg-green-600 transition duration-200'
						>
							Submit
						</button>
					</form>
				</div>
			</div>
		</div>
	);
};

export default Home;
