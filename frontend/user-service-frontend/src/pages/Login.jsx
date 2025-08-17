import { useNavigate } from 'react-router-dom';
import { useState } from "react";
import { loginUser } from "../services/apiService";

function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    try {
      const result = await loginUser(email, password);
      console.log("Token:", result.token);

      // Optionally save the token
      localStorage.setItem("token", result.token);

      // Navigate to dashboard
      navigate('/dashboard');
    } catch (error) {
      alert("Invalid credentials");
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
      <h2>Login</h2>
      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button onClick={handleLogin}>Login</button>
    </div>
  );
}

export default Login;
