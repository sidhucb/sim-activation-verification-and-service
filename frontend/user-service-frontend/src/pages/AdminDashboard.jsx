import React from "react";
import "./AdminDashboard.css";
import Card from "../components/Card";

export default function AdminDashboard() {
  return (
    <div className="admin-page">
      <h1 className="admin-title">Admin Dashboard</h1>
      <div className="admin-grid">
        <Card className="admin-card">Pending KYC</Card>
        <Card className="admin-card">User Management</Card>
        <Card className="admin-card">Reports</Card>
        <Card className="admin-card">System Settings</Card>
      </div>
    </div>
  );
}
