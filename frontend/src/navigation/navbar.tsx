import { NavLink, useLocation } from "react-router-dom";
import { RxHamburgerMenu } from "react-icons/rx";
import { useState } from "react";
import classNames from "classnames";

const Navbar = () => {
  // Maintains the state of the navbar (shown vs. hidden)
  const [navbarShow, setNavbarShow] = useState("none");
  const location = useLocation();

  // Hides and shows the navbar in mobile view
  const toggleNavbar = () => {
    if (navbarShow === "block") {
      setNavbarShow("none");
    } else {
      setNavbarShow("block");
    }
  };

  // Function to get the title based on the current path
  // Used to display current page on mobile view navbar
  const getCurrentPageTitle = () => {
    switch (location.pathname) {
      case "/":
        return "Home";
      case "/plan":
        return "My Plan";
      case "/courses":
        return "All Courses";
      default:
        return "Course Planner";
    }
  };

  return (
    <>
      {/* Hamburger menu shown in mobile view */}
      <div className="w-full border-b p-2 border-black md:hidden bg-gray-200 relative">
        <div className="flex justify-center items-center relative">
          {/* Displays the current active page in the mobile view */}
          <div className="text-center font-bold underline">
            {getCurrentPageTitle()}
          </div>
          <RxHamburgerMenu
            className="h-10 absolute right-5"
            onClick={toggleNavbar}
          />
        </div>
      </div>
      {/* Define the navbar */}
      <nav
        className={classNames(
          "grid grid-cols-1 text-center md:flex md:justify-evenly md:p-2.5 bg-gray-200",
          { hidden: navbarShow === "none" }
        )}
      >
        {/* Link to Home page */}
        <NavLink
          to="/"
          className={({ isActive }) => (isActive ? "font-bold" : "font-normal")}
        >
          <span
            className="py-3 md:cursor-pointer md:hover:underline"
            onClick={() => setNavbarShow("none")}
          >
            Home
          </span>
          <hr className="block md:hidden" />
        </NavLink>
        {/* Link to My Plan page */}
        <NavLink
          to="/plan"
          className={({ isActive }) => (isActive ? "font-bold" : "font-normal")}
        >
          <span
            className="py-3 md:cursor-pointer md:hover:underline"
            onClick={() => setNavbarShow("none")}
          >
            My Plan
          </span>
          <hr className="block md:hidden" />
        </NavLink>
        {/* Linke to All Courses page */}
        <NavLink
          to="/courses"
          className={({ isActive }) => (isActive ? "font-bold" : "font-normal")}
        >
          <span
            className="py-3 md:cursor-pointer md:hover:underline"
            onClick={() => setNavbarShow("none")}
          >
            All Courses
          </span>
          <hr className="block md:hidden" />
        </NavLink>
      </nav>
    </>
  );
};

export default Navbar;
