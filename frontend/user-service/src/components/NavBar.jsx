import { Link, useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { FaBell, FaUserCircle, FaQuestionCircle, FaEllipsisV } from "react-icons/fa";
import "./Navbar.css";

export default function Navbar() {
  const location = useLocation();
  const pathname = location.pathname;
  const navigate = useNavigate(); // <-- add this

  const [notifOpen, setNotifOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [moreOpen, setMoreOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  const token = localStorage.getItem("token");
  const isLoggedIn = !!token;

  useEffect(() => {
    if (isLoggedIn) {
      const data = [
        { id: 1, message: "SIM activated successfully", read: false },
        { id: 2, message: "New SIM available", read: true },
      ];
      setNotifications(data);
      setUnreadCount(data.filter(n => !n.read).length);
    }
  }, [isLoggedIn, pathname]);

  const showNotifProfile = isLoggedIn && (pathname.startsWith("/dashboard") || pathname.startsWith("/admin-dashboard"));

  return (
    <nav className="navbar">
      <div className="navbar-left">
        {/* --- BACK BUTTON --- */}
        <button
          className="navbar-back-button"
          onClick={() => navigate(-1)}
        >
          ⬅
        </button>

        {/* Logo & Title */}
        <img src="src/Nexus LOGO.PNG" alt="Nexus Logo" className="navbar-logo" />
        <span className="navbar-title">Nexus Networks</span>
      </div>

      <div className="navbar-right">
        {showNotifProfile && (
          <>
            <div className="notification-wrapper">
              <FaBell className="navbar-icon" onClick={() => setNotifOpen(!notifOpen)} />
              {unreadCount > 0 && <span className="notification-badge">{unreadCount}</span>}
              {notifOpen && (
                <div className="notification-dropdown">
                  {notifications.length === 0 ? (
                    <p className="notification-empty">No notifications</p>
                  ) : (
                    notifications.map(n => (
                      <p key={n.id} className={n.read ? "read" : "unread"}>
                        {n.message}
                      </p>
                    ))
                  )}
                </div>
              )}
            </div>

            <div className="profile-wrapper">
              <FaUserCircle className="navbar-icon" onClick={() => setProfileOpen(!profileOpen)} />
              {profileOpen && (
                <div className="profile-dropdown">
                  <Link to="/profile">Profile</Link>
                  <Link to="/settings">Settings</Link>
                  <Link to="/connected-sims">Connected SIMs</Link>
                  <Link to="/logout">Logout</Link>
                </div>
              )}
            </div>
          </>
        )}

        <Link to="/faq" className="navbar-icon-link">
          <FaQuestionCircle className="navbar-icon" title="Help & FAQ" />
        </Link>

        <div className="more-wrapper">
          <FaEllipsisV className="navbar-icon" onClick={() => setMoreOpen(!moreOpen)} />
          {moreOpen && (
            <div className="more-dropdown">
              <Link to="#">Theme Toggle</Link>
              <Link to="#">API Status</Link>
              <Link to="#">Quick Actions</Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}
