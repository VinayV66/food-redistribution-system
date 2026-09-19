import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const user = await login(form.email, form.password);
      // Redirect based on role
      switch (user.role) {
        case 'ADMIN':     navigate('/admin/dashboard');     break;
        case 'DONOR':     navigate('/donor/dashboard');     break;
        case 'NGO':       navigate('/ngo/dashboard');       break;
        case 'VOLUNTEER': navigate('/volunteer/dashboard'); break;
        default:          navigate('/');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div style={{ textAlign: 'center', fontSize: 48, marginBottom: 12 }}>🍱</div>
        <h2 className="auth-title">Welcome Back</h2>
        <p className="auth-subtitle">Login to your Food Rescue account</p>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              name="email"
              className="form-control"
              placeholder="your@email.com"
              value={form.email}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              name="password"
              className="form-control"
              placeholder="Enter your password"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%', justifyContent: 'center' }} disabled={loading}>
            {loading ? 'Logging in...' : 'Login →'}
          </button>
        </form>

        {/* Dev hints */}
        <div className="alert alert-info" style={{ marginTop: 20, fontSize: 13 }}>
          <strong>Test Accounts:</strong><br/>
          admin@foodrescue.com / Admin@123<br/>
          donor@foodrescue.com / Donor@123<br/>
          ngo@foodrescue.com / Ngo@12345<br/>
          volunteer@foodrescue.com / Vol@12345
        </div>

        <p style={{ textAlign: 'center', marginTop: 20, fontSize: 14, color: '#6c757d' }}>
          Don't have an account? <Link to="/register" style={{ color: 'var(--primary)' }}>Register here</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;
