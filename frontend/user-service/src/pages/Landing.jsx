import React from "react";
import { useNavigate } from "react-router-dom";
import Card from "../components/Card";
import Button from "../components/Button";
import "./Landing.css";

export default function Landing() {
  const navigate = useNavigate();
  return (
    <div className="landing-page">
      <div className="landing-container">
        <Card className="landing-card">
          <h1 className="landing-title">Nexus Networks</h1>
          <p className="landing-tagline">Activate. Verify. Connect.</p>
          <div className="landing-buttons">
            <Button onClick={() => navigate("/login")}>Login</Button>
            <Button onClick={() => navigate("/signup")}>Create Account</Button>
          </div>
        </Card>
        <div className="landing-showcase">
          <h2>Features</h2>
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
