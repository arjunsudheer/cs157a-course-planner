import { BrowserRouter, Routes, Route } from "react-router-dom";
import Footer from "./navigation/footer";
import Navbar from "./navigation/navbar";
import AllCourses from "./pages/all_courses";
import Home from "./home";
import MyPlanPage from "./pages/my_plan";
import AdminPage from "./pages/AdminPage";

function App() {
	return (
		<>
			<BrowserRouter>
				<Navbar />
				<Routes>
					<Route path='/' element={<Home />} />
					<Route path='/courses' element={<AllCourses />} />
					<Route path='/plan' element={<MyPlanPage />} />
					<Route path='/admin' element={<AdminPage />} />
				</Routes>
			</BrowserRouter>
			<Footer />
		</>
	);
}

export default App;
