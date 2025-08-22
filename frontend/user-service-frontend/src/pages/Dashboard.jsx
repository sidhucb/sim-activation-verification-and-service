import "./Dashboard.css";

function Dashboard() {
  return (
    <div className="dashboard-page">
      {/* Optional background graphics */}
      <div className="bg-circle circle1"></div>
      <div className="bg-circle circle2"></div>

      <div className="dashboard-container">
        <h1 className="dashboard-title">Dashboard</h1>
        <div className="boxes-wrapper">
          <div className="dashboard-box">Check SIM Status</div>
          <div className="dashboard-box">Get New SIM</div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
