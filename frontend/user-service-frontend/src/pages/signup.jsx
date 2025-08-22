import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../services/apiService";
import "./signup.css"; // Use CSS for styling

function Signup() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [passwordMessage, setPasswordMessage] = useState("");
  const [emailMessage, setEmailMessage] = useState("");

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
    <div className="signup-page"> {/* Full-screen wrapper */}
      <div className="signup-container">
        <h2>Create Account</h2>

        <input
          type="email"
          value={email}
          placeholder="Email"
          onChange={handleEmailChange}
        />
        {emailMessage && <p className={`message ${emailMessage.includes("✅") ? "success" : "error"}`}>{emailMessage}</p>}

        <div className="password-wrapper">
          <input
            type={showPassword ? "text" : "password"}
            value={password}
            placeholder="Password"
            onChange={handlePasswordChange}
          />
          <span className="toggle-password" onClick={() => setShowPassword(!showPassword)}>
            {showPassword ? "Hide" : "Show"}
          </span>
        </div>
        {passwordMessage && <p className={`message ${passwordMessage.includes("✅") ? "success" : "error"}`}>{passwordMessage}</p>}

        <button onClick={handleSignup} disabled={!isFormValid}>
          Create Account
        </button>
      </div>
    </div>
  );
}

export default Signup;
