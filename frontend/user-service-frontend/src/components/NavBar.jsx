import { Link, useLocation } from "react-router-dom";

export default function Navbar() {
  const location = useLocation();
  const isAuthPage = location.pathname === "/login" || location.pathname === "/signup";

  return (
    <nav className="fixed top-0 left-0 w-full z-20 flex justify-end p-4 bg-gradient-to-r from-black/50 via-gray-900/50 to-black/50 backdrop-blur-md">
      {isAuthPage && (
        <Link
          to="/"
          className="px-4 py-2 rounded-md bg-purple-600 hover:bg-purple-700 text-white font-semibold transition"
        >
          Back to Landing
        </Link>
      )}
    </nav>
  );
}
