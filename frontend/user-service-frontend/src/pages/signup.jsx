import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../services/apiService";
import Card from "../components/Card";
import Button from "../components/Button";
import "./Signup.css";

export default function Signup() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [emailMessage, setEmailMessage] = useState("");
  const [passwordMessage, setPasswordMessage] = useState("");

  const handleSignup = async () => {
    try {
      await registerUser(email, password);
      navigate("/login");
    } catch (error) {
      alert("Error creating account");
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

  const handlePasswordChange = (e) => {
    const value = e.target.value;
    setPassword(value);
    if (value.length < 6) setPasswordMessage("Password must be at least 6 characters");
    else if (!/[A-Z]/.test(value)) setPasswordMessage("Password must contain an uppercase letter");
    else if (!/[0-9]/.test(value)) setPasswordMessage("Password must contain a number");
    else setPasswordMessage("Looks good ✅");
  };

  const isFormValid = emailMessage.includes("✅") && passwordMessage.includes("✅");

  return (
    <div className="signup-page">
      <div className="signup-container">
        <Card className="signup-card">
          <h2 className="signup-title">Create Account</h2>

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
              onChange={handlePasswordChange}
            />
            <span className="toggle-password" onClick={() => setShowPassword(!showPassword)}>
              {showPassword ? "Hide" : "Show"}
            </span>
          </div>
          {passwordMessage && (
            <p className={`message ${passwordMessage.includes("✅") ? "success" : "error"}`}>
              {passwordMessage}
            </p>
          )}

          <Button onClick={handleSignup} disabled={!isFormValid}>
            Create Account
          </Button>

          <div className="signup-bottom">
            <Button onClick={() => navigate("/")}>Back to Landing</Button>
            <Button onClick={() => navigate("/login")}>Login</Button>
          </div>
        </Card>

        <div className="signup-showcase">
          <h2>Why Join?</h2>
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
