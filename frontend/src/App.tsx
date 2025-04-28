import { BrowserRouter } from "react-router-dom";
import Footer from "./navigation/footer";
import Navbar from "./navigation/navbar";
import AllCourses from "./all_courses";
import Home from "./home";

function App() {
	return (
		<>
			<BrowserRouter>
				<Navbar />
				{/* <Home /> */}
				<AllCourses></AllCourses>
			</BrowserRouter>
			<Footer />
		</>
	);
}

export default App;
