import { BrowserRouter, Routes, Route } from "react-router-dom";
import Footer from "./navigation/footer";
import Navbar from "./navigation/navbar";
import AllCourses from "./pages/all_courses";
import Home from "./pages/home";
import MyPlan from "./pages/my_plan";

function App() {
	return (
		<>
			<BrowserRouter>
				<Navbar />
				<Routes>
					<Route path='/' element={<Home />} />
					<Route path='/courses' element={<AllCourses />} />
					<Route path='/plan' element={<MyPlan />} />
				</Routes>
			</BrowserRouter>
			<Footer />
		</>
	);
}

export default App;
