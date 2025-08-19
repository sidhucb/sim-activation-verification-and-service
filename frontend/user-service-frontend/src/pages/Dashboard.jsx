function Dashboard() {
    return (
        <div style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',       // center horizontally
            justifyContent: 'center',   // center vertically
            height: '100vh',            // full viewport height
            gap: '20px'                 // space between buttons
          }}>
        <h2>Dashboard</h2>
        <button>Check SIM Status</button>
        <button style={{ marginLeft: '10px' }}>Get New SIM</button>
      </div>
    );
  }
  
  export default Dashboard;
  