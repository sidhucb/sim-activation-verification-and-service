// import React from "react";
// import "./Dashboard.css";
// import Card from "../components/Card";

// export default function Dashboard() {
//   return (
//     <div className="dashboard-page">
//       <h1 className="dashboard-title">Dashboard</h1>
//       <div className="dashboard-grid">
//         <Card className="dashboard-card">Check SIM Status</Card>
//         <Card className="dashboard-card">Get New SIM</Card>
//         <Card className="dashboard-card">Profile Settings</Card>
//         <Card className="dashboard-card">Notifications</Card>
//       </div>
//     </div>
//   );
// }

import React from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";
import Card from "../components/Card";

export default function Dashboard() {
  const navigate = useNavigate();

  return (
    <div className="dashboard-page">
      <h1 className="dashboard-title">Dashboard</h1>
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
        <Card className="dashboard-card">Profile Settings</Card>
        <Card className="dashboard-card">Notifications</Card>
      </div>
    </div>
  );
}
