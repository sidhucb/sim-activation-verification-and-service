import { useNavigate } from 'react-router-dom';

function Landing() {
  const navigate = useNavigate();

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      minHeight: '100vh',      // ensures full viewport height
      width: '100%',            // full width
      boxSizing: 'border-box',  // avoid padding/margin issues
      gap: '20px',
      textAlign: 'center'
    }}>
      <h1>Welcome</h1>
      <div style={{ display: 'flex', gap: '10px' }}>
        <button onClick={() => navigate('/login')}>Login</button>
        <button onClick={() => navigate('/signup')}>Create Account</button>
      </div>
    </div>
  );
}

export default Landing;
