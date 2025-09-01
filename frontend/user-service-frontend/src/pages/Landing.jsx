import React from "react";
import { useNavigate } from "react-router-dom";
import "./Landing.css";

export default function Landing() {
  const navigate = useNavigate();
  return (
    <div className="landing-verizon">
      <nav className="vz-nav">
        <span className="logo">Nexus Networks</span>
        <button className="vz-btn vz-btn-outline" onClick={() => navigate("/login")}>Login</button>
        <button className="vz-btn vz-btn-red" onClick={() => navigate("/signup")}>Get Started</button>
      </nav>
      <header className="vz-hero-section">
        <h1 className="vz-headline">Welcome to a New Standard in Connectivity</h1>
        <p className="vz-lead">Activate. Verify. Connect. Experience the fastest SIM onboarding and secure management in a bold, modern network.</p>
        <button className="vz-btn vz-btn-red" onClick={() => navigate("/signup")}>Create Account</button>
      </header>
      <section className="vz-features">
        <div className="feature-card">
          <h2 className="feature-title">Fast SIM Verification</h2>
          <p>Instant activation and seamless onboarding for every device.</p>
        </div>
        <div className="feature-card">
          <h2 className="feature-title">Secure Account Management</h2>
          <p>Industry-leading privacy and multi-factor protection.</p>
        </div>
        <div className="feature-card">
          <h2 className="feature-title">Real-Time Notifications</h2>
          <p>Stay always updated with live alerts and activity feeds.</p>
        </div>
      </section>
      <footer className="vz-footer">
        Powered by Nexus Network
      </footer>
    </div>
  );
}
