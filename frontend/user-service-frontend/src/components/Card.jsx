import React from "react";
import "./Card.css";

export default function Card({ children, className = "" }) {
  return (
    <div className={`aurora-card ${className}`}>
      {children}
    </div>
  );
}
