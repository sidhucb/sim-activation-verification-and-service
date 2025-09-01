import React from "react";
import "./Footer.css";

export default function Footer() {
  return (
    <footer className="app-footer">
      <div className="footer-content">
        <p>&copy; 2025 Nexus Networks. All rights reserved.</p>
        <nav className="footer-nav">
          <button onClick={() => window.scrollTo(0, 0)} className="footer-link">Back to Top</button>
          <button onClick={() => alert("Privacy Policy")} className="footer-link">Privacy Policy</button>
          <button onClick={() => alert("Terms of Service")} className="footer-link">Terms of Service</button>
          <button onClick={() => alert("Contact Us")} className="footer-link">Contact Us</button>
        </nav>
      </div>
    </footer>
  );
}
