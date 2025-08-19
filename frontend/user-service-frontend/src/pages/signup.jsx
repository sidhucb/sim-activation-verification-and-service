import { useNavigate } from 'react-router-dom';

function Signup() {
  const navigate = useNavigate();

  return (
    <div style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',       // center horizontally
        justifyContent: 'center',   // center vertically
        height: '100vh',            // full viewport height
        gap: '20px'                 // space between buttons
      }}>
      <h2>Create Account</h2>
      <input type="text" placeholder="Name" /><br /><br />
      <input type="email" placeholder="Email" /><br /><br />
      <input type="password" placeholder="Password" /><br /><br />
      <button onClick={() => navigate('/dashboard')}>Create Account</button>
    </div>
  );
}

export default Signup;
