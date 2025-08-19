import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../services/apiService";

function Signup() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");


const handleSignup = async () => {
  try {
    await registerUser(email, password);
    navigate("/login");
  } catch (error) {
    alert("Error creating account");
  }
};


  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        height: "100vh",
        gap: "20px",
      }}
    >
      <h2>Create Account</h2>
      <input
        type="email"
        value={email}
        placeholder="Email"
        onChange={(e) => setEmail(e.target.value)}
      />
      <input
        type="password"
        value={password}
        placeholder="Password"
        onChange={(e) => setPassword(e.target.value)}
      />
      <button onClick={handleSignup}>Create Account</button>
    </div>
  );
}

export default Signup;
