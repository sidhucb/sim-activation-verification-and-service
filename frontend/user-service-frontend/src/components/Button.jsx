import React from "react";
import "./Button.css";

export default function Button({ children, onClick, disabled = false, className = "" }) {
  return (
    <button onClick={onClick} disabled={disabled} className={`aurora-btn ${className}`}>
      {children}
    </button>
  );
}
