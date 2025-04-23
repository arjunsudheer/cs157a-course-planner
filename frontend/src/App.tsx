import { BrowserRouter } from "react-router-dom";
import Footer from "./navigation/footer";
import Navbar from "./navigation/navbar";

function App() {
	return (
		<>
			<BrowserRouter>
				<Navbar />
			</BrowserRouter>
			<Footer />
		</>
	);
}

export default App;
