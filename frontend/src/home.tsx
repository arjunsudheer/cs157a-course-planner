import { useState } from "react";
import { useNavigate } from "react-router-dom";

const Home = () => {
	const [studentID, setStudentID] = useState("");
	const navigate = useNavigate();

	const handleLogin = async (e: { preventDefault: () => void }) => {
		e.preventDefault();
		if (studentID) {
			//connects to backend endpt
			try {
				// await axios.post("http://127.0.0.1:5000/login", {
				// 	email,
				// 	password,
				// });
				navigate("/courses");
			} catch (error: any) {
				if (error.response && error.response.data && error.response.data.message) {
					alert(error.response.data.message);
				} else {
					alert("An error occurred while logging in.");
				}
			}
		} else {
			alert("Please enter your Student ID.");
		}
	};

	return (
		// Place the login box in the center of the screen
		<div className='absolute my-50 mx-50 text-center'>
			<h2 className='text-3xl mb-6 font-bold'>Enter your Student ID</h2>
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
	);
};

export default Home;
