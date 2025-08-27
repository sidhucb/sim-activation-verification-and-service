import React, { useState, useEffect } from "react";
import "./AdminDashboard.css";
import Card from "../components/Card";
import { Link } from "react-router-dom"; 
import { getPendingManualDocs, getPendingOcrDocs, getAllUsers } from "../services/apiService";

export default function AdminDashboard() {
  const [pendingManualCount, setPendingManualCount] = useState(0);
  const [pendingOcrCount, setPendingOcrCount] = useState(0);
  const [totalUsers, setTotalUsers] = useState(0);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const manualDocs = await getPendingManualDocs();
        setPendingManualCount(manualDocs.length);

        const ocrDocs = await getPendingOcrDocs();
        setPendingOcrCount(ocrDocs.length);

        const users = await getAllUsers();
        setTotalUsers(users.length);
      } catch (error) {
        console.error("Failed to fetch dashboard data:", error);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="admin-page">
      <h1 className="admin-title">Admin Dashboard</h1>
      <div className="admin-grid">
        {/* Link card to unified KYC approval page */}
        <Link to="/admin-dashboard/kyc-approval" className="card-link">
          <Card className="admin-card">
            <span>Pending Manual & OCR Docs</span>
            <span className="card-number">{pendingManualCount + pendingOcrCount}</span>
          </Card>
        </Link>

        {/* Optionally remove or comment out individual cards if you unify */}
        {/* <Card className="admin-card">
          <span>Pending Manual Docs</span>
          <span className="card-number">{pendingManualCount}</span>
        </Card> */}

        {/* <Card className="admin-card">
          <span>Pending OCR Docs</span>
          <span className="card-number">{pendingOcrCount}</span>
        </Card> */}

        <Link to="/admin-dashboard/users" className="card-link"> 
          <Card className="admin-card">
            <span>Total Users</span>
            <span className="card-number">{totalUsers}</span>
          </Card>
        </Link>
      </div>
    </div>
  );
}
