import { BrowserRouter, Routes, Route } from "react-router-dom";
import Footer from "./navigation/footer";
import Navbar from "./navigation/navbar";
import AllCourses from "./all_courses";
import Home from "./home";

function App() {
	return (
		<>
			<BrowserRouter>
				<Navbar />
				<Routes>
					<Route path='/' element={<Home />} />
					<Route path='/courses' element={<AllCourses />} />
				</Routes>
			</BrowserRouter>
			<Footer />
		</>
	);
}

export default App;
