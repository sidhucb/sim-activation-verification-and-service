import React from "react";
import "./Dashboard.css";
import Card from "../components/Card";

export default function Dashboard() {
  return (
    <div className="dashboard-page">
      <h1 className="dashboard-title">Dashboard</h1>
      <div className="dashboard-grid">
        <Card className="dashboard-card">Check SIM Status</Card>
        <Card className="dashboard-card">Get New SIM</Card>
        <Card className="dashboard-card">Profile Settings</Card>
        <Card className="dashboard-card">Notifications</Card>
      </div>
    </div>
  );
}
