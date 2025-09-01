import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "./Header.css";

export default function Header() {
  const navigate = useNavigate();
  const location = useLocation();

  // Don't render header on login or landing pages
  const noHeaderPaths = ["/login", "/", "/landing"];
  if (noHeaderPaths.includes(location.pathname)) return null;

  return (
    <header className="app-header">
      <div className="header-content">
        <nav className="nav-left">
          <button onClick={() => navigate("/")} className="nav-link">Home</button>
          <button onClick={() => navigate("/sim-activation")} className="nav-link">SimActivation</button>
          <button onClick={() => navigate("/kyc-document")} className="nav-link">KYC Document</button>
          <button onClick={() => navigate("/contact-us")} className="nav-link">Contact Us</button>
          <button onClick={() => navigate("/generate-number")} className="nav-link">Generate Number</button>
        </nav>
        <div className="header-logo" onClick={() => navigate("/")}>
          Nexus Networks
        </div>
        <nav className="nav-right">
          <button className="nav-btn" onClick={() => navigate("/login")}>Login</button>
          <button className="nav-btn nav-btn-primary" onClick={() => navigate("/signup")}>Create Account</button>
        </nav>
      </div>
    </header>
  );
}
