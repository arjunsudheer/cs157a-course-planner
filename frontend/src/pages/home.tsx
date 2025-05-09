import { useState } from "react";
import { useNavigate } from "react-router-dom";

const Home = () => {
	const [studentID, setStudentID] = useState("");
	const navigate = useNavigate();

	const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		if (!studentID) {
			alert("Please enter your Student ID.");
			return;
		}

		try {
			// Tell the backend to query the Students table to check if the studentID exists
			const response = await fetch("http://localhost:8080/login", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({ studentID }),
			});

			// If the studentID exists and is valid, redirect the user
			if (response.ok) {
				const data = await response.json();
				localStorage.setItem("studentID", studentID);
				localStorage.setItem("isAdmin", data.isAdmin ? "true" : "false");

				navigate("/plan");
			} else {
				// Print the backend error message in the alert
				const errorMessage = await response.text();
				alert(errorMessage || "Login failed. Please try again.");
			}
		} catch (error) {
			console.error("Login error:", error);
			alert("An error occurred during login. Please try again.");
		}
	};

	return (
		<div className='h-screen'>
			<div className='flex flex-col items-center justify-center h-screen'>
				<h2 className='text-3xl mb-6 font-bold'>Enter your Student ID</h2>
				<div className='flex justify-evenly'>
					{/* Allow the student to enter their student ID number */}
					<form onSubmit={handleLogin} className='bg-gray-200 p-8 rounded-lg shadow-md w-96'>
						<div className='mb-6'>
							<input
								type='text'
								id='studentID'
								value={studentID}
								onChange={(e) => setStudentID(e.target.value)}
								className='border border-black rounded p-3 w-full focus:outline-none focus:ring-2 focus:ring-blue-500'
								placeholder='Ex: 123456789'
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
