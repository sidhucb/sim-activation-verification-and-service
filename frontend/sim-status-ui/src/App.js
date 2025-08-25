import React from 'react';
import SimStatusChecker from './SimStatusChecker';
import Scene from './Scene'; // Import the new 3D Scene component
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        {/* The 3D scene will render here, in the background */}
        <Scene />
        {/* We wrap the title in a div to apply the foreground styling */}
        <div className="header-content">
          <h1>SIM Application Portal</h1>
        </div>
      </header>
      <main>
        <SimStatusChecker />
      </main>
    </div>
  );
}

export default App;