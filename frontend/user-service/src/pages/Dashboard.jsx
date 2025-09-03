import React from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";

import Card from "../components/Card";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

export default function Dashboard() {
  const navigate = useNavigate();

  // Sample data based on the chart image
  const data = [
    { age: "18-24 years", Male: 94.8, Female: 90.1 },
    { age: "24-29 years", Male: 95.9, Female: 90.5 },
    { age: "29+ years", Male: 89.5, Female: 74.9 },
  ];

  return (
    <div className="dashboard-page">
      <h1 className="dashboard-title">Dashboard</h1>

      {/* Cards Section */}
      <div className="dashboard-grid">
        <Card
          className="dashboard-card"
          onClick={() => navigate("/dashboard/check-status")}
          style={{ cursor: "pointer" }}
        >
          Check SIM Status
        </Card>
        <Card
          className="dashboard-card"
          onClick={() => navigate("/dashboard/kyc")}
          style={{ cursor: "pointer" }}
        >
          Get New SIM
        </Card>
      </div>

      {/* Chart Section */}
      <div className="chart-section">
        <h2 className="chart-title">
          Percentage of persons who used mobile with an active SIM card during
          last three months in rural and urban areas
        </h2>
        <ResponsiveContainer width="100%" height={400}>
          <BarChart data={data} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#ddd" />
            <XAxis dataKey="age" stroke="#1a1a1a" />
            <YAxis stroke="#1a1a1a" />
            <Tooltip />
            <Legend />
            <Bar dataKey="Male" fill="#e63946" radius={[6, 6, 0, 0]} />
            <Bar dataKey="Female" fill="#000" radius={[6, 6, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
