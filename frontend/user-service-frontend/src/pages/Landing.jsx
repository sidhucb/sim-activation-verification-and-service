// import { useNavigate } from 'react-router-dom';
// import './Landing.css'; // import the CSS file

// function Landing() {
//   const navigate = useNavigate();

//   return (
//     <div className="landing-container">
//       <h1>Welcome</h1>
//       <div className="button-group">
//         <button onClick={() => navigate('/login')}>Login</button>
//         <button onClick={() => navigate('/signup')}>Create Account</button>
//       </div>
//     </div>
//   );
// }

// export default Landing;




import { useNavigate } from 'react-router-dom';
import './Landing.css';

function Landing() {
  const navigate = useNavigate();

  return (
    <div className="landing-page">
      {/* Background tech shapes */}
      <div className="bg-shape circle1"></div>
      <div className="bg-shape circle2"></div>
      <div className="bg-shape circle3"></div>

      <div className="landing-card">
        <h1>Nexus Networks</h1>
        <p className="tagline">Activate. Verify. Connect.</p>

        <div className="button-group">
          <button onClick={() => navigate('/login')}>Login</button>
          <button onClick={() => navigate('/signup')}>Create Account</button>
        </div>
      </div>
    </div>
  );
}

export default Landing;
