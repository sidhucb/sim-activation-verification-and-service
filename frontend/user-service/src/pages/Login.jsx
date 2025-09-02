import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../services/apiService";
import Card from "../components/Card";
import Button from "../components/Button";
import "./Login.css";

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [emailMessage, setEmailMessage] = useState("");

  const handleLogin = async () => {
    try {
      const result = await loginUser(email, password);
      localStorage.setItem("token", result.token);
      const role = JSON.parse(atob(result.token.split(".")[1])).role;
      if (role === "ADMIN") navigate("/admin-dashboard");
      else navigate("/dashboard");
    } catch (error) {
      alert("Invalid credentials");
    }
  };

  const handleEmailChange = (e) => {
    const value = e.target.value;
    setEmail(value);
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!value) setEmailMessage("");
    else if (!emailRegex.test(value)) setEmailMessage("Invalid email format");
    else setEmailMessage("Looks good ✅");
  };

  const isFormValid = emailMessage.includes("✅") && password.length >= 6;

  return (
    <div className="login-page">
      <div className="login-container">
        <Card className="login-card">
          <h2 className="login-title">Login</h2>

          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={handleEmailChange}
          />
          {emailMessage && (
            <p className={`message ${emailMessage.includes("✅") ? "success" : "error"}`}>
              {emailMessage}
            </p>
          )}

          <div className="password-wrapper">
            <input
              type={showPassword ? "text" : "password"}
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <span className="toggle-password" onClick={() => setShowPassword(!showPassword)}>
              {showPassword ? "Hide" : "Show"}
            </span>
          </div>

          <Button onClick={handleLogin} disabled={!isFormValid}>
            Login
          </Button>

          <div className="login-bottom">
            <Button onClick={() => navigate("/")}>Back to Landing</Button>
            <Button onClick={() => navigate("/signup")}>Create Account</Button>
          </div>
        </Card>

        <div className="login-showcase">
          <h2>Why Nexus?</h2>
          <ul>
            <li>Fast SIM verification</li>
            <li>Secure account management</li>
            <li>Real-time notifications</li>
            <li>Modern Aurora UI</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
