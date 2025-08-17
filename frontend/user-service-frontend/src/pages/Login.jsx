import { useNavigate } from 'react-router-dom';

function Login() {
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
      <h2>Login</h2>
      <input type="email" placeholder="Email" />
      <input type="password" placeholder="Password" />
      <button onClick={() => navigate('/dashboard')}>Login</button>
    </div>
  );
}

export default Login;
