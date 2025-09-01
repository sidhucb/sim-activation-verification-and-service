import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Login.css";

export default function Login() {
  const navigate = useNavigate();
  const [userId, setUserId] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showForgotModal, setShowForgotModal] = useState(false);
  const [forgotUserId, setForgotUserId] = useState("");

  const isFormValid = userId.length > 0 && password.length > 5;
  const isForgotValid = forgotUserId.trim().length > 0;

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      // Implement actual login API logic here
      alert("Login logic goes here!");
    } catch (error) {
      alert("Invalid credentials");
    }
  };

  const openForgotModal = () => {
    setForgotUserId("");
    setShowForgotModal(true);
  };

  const closeForgotModal = () => setShowForgotModal(false);

  const handleForgotUserIdChange = (e) => {
    setForgotUserId(e.target.value);
  };

  const handleForgotSubmit = (e) => {
    e.preventDefault();
    if (!isForgotValid) return;
    alert(`Password reset link sent to: ${forgotUserId}`);
    setShowForgotModal(false);
  };

  return (
    <div className="login-page">
      <div className="login-wrapper">
        <div className="login-header">
          <h1>Sign in</h1>
          <span>to your Verizon account</span>
        </div>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="userid">User ID or Verizon mobile number</label>
            <input
              type="text"
              id="userid"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              required
            />
          </div>
          <div className="form-group password-wrapper">
            <label htmlFor="password">Password</label>
            <input
              type={showPassword ? "text" : "password"}
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <button
              type="button"
              className="toggle-password"
              onClick={() => setShowPassword(!showPassword)}
              aria-label={showPassword ? "Hide password" : "Show password"}
            >
              {showPassword ? "Hide" : "Show"}
            </button>
          </div>
          <button type="submit" className="login-btn" disabled={!isFormValid}>
            Sign In
          </button>
        </form>

        <div className="login-footer">
          <a href="#forgot" onClick={(e) => { e.preventDefault(); openForgotModal(); }}>
            Forgot your info?
          </a>
          <a href="#register" onClick={() => navigate("/signup")}>Register</a>
        </div>

        <div className="brand-showcase">
          <h2>Stay connected and save</h2>
          <ul>
            <li>Experience nationwide mobile coverage with plans for every budget.</li>
            <li>Enjoy reliable home internet with no annual contracts.</li>
          </ul>
        </div>
      </div>

      {/* Forgot Password Modal */}
      {showForgotModal && (
        <div
          className="forgot-modal-overlay"
          onClick={closeForgotModal}
          role="dialog"
          aria-modal="true"
        >
          <div className="forgot-modal" onClick={(e) => e.stopPropagation()}>
            <h3>Forgot Password</h3>
            <p>Enter your Email </p>
            <form onSubmit={handleForgotSubmit}>
              <div className="form-group">
                <label htmlFor="forgotUserId">User ID or Mobile Number</label>
                <input
                  type="text"
                  id="forgotUserId"
                  value={forgotUserId}
                  onChange={handleForgotUserIdChange}
                  required
                  autoFocus
                />
              </div>
              <div style={{ display: "flex", justifyContent: "flex-end", gap: "12px", marginTop: "12px" }}>
                <button type="button" className="login-btn" style={{ backgroundColor: "var(--verizon-gray-light)", color: "var(--verizon-gray-dark)" }} onClick={closeForgotModal}>
                  Cancel
                </button>
                <button type="submit" className="login-btn" disabled={!isForgotValid}>
                  Submit
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
