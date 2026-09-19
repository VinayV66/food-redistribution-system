import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const roles = ['DONOR', 'NGO', 'VOLUNTEER'];

const Register = () => {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    fullName: '', email: '', password: '', phone: '',
    role: 'DONOR', organizationName: '', organizationType: '',
    ngoName: '', registrationNumber: '', address: '',
    latitude: '', longitude: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    setLoading(true);
    try {
      const payload = {
        ...form,
        latitude: form.latitude ? parseFloat(form.latitude) : null,
        longitude: form.longitude ? parseFloat(form.longitude) : null,
      };
      const user = await register(payload);
      switch (user.role) {
        case 'DONOR':     navigate('/donor/dashboard');     break;
        case 'NGO':       navigate('/ngo/dashboard');       break;
        case 'VOLUNTEER': navigate('/volunteer/dashboard'); break;
        default:          navigate('/');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container" style={{ paddingTop: 32, paddingBottom: 32 }}>
      <div className="auth-card" style={{ maxWidth: 560 }}>
        <div style={{ textAlign: 'center', fontSize: 48, marginBottom: 12 }}>🍱</div>
        <h2 className="auth-title">Create Account</h2>
        <p className="auth-subtitle">Join the food rescue community</p>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          {/* Role Selection */}
          <div className="form-group">
            <label className="form-label">I am a...</label>
            <div style={{ display: 'flex', gap: 10 }}>
              {roles.map(r => (
                <button key={r} type="button"
                  onClick={() => setForm({ ...form, role: r })}
                  style={{
                    flex: 1, padding: '10px', border: '2px solid',
                    borderColor: form.role === r ? 'var(--primary)' : 'var(--border)',
                    borderRadius: 8, background: form.role === r ? 'var(--primary)' : 'white',
                    color: form.role === r ? 'white' : 'var(--text)',
                    cursor: 'pointer', fontSize: 13, fontWeight: 500,
                  }}>
                  {r === 'DONOR' ? '🏪 Donor' : r === 'NGO' ? '🏢 NGO' : '🚗 Volunteer'}
                </button>
              ))}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Full Name / Org Name *</label>
              <input type="text" name="fullName" className="form-control"
                placeholder="Your name or organization" value={form.fullName}
                onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label className="form-label">Phone Number *</label>
              <input type="tel" name="phone" className="form-control"
                placeholder="10-digit phone" value={form.phone}
                onChange={handleChange} required />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Email Address *</label>
            <input type="email" name="email" className="form-control"
              placeholder="your@email.com" value={form.email}
              onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label className="form-label">Password * (min. 6 characters)</label>
            <input type="password" name="password" className="form-control"
              placeholder="Create a strong password" value={form.password}
              onChange={handleChange} required />
          </div>

          {/* Donor-specific */}
          {form.role === 'DONOR' && (
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Organization Name</label>
                <input type="text" name="organizationName" className="form-control"
                  placeholder="Restaurant / Hotel name" value={form.organizationName}
                  onChange={handleChange} />
              </div>
              <div className="form-group">
                <label className="form-label">Organization Type</label>
                <select name="organizationType" className="form-control" value={form.organizationType} onChange={handleChange}>
                  <option value="">Select type...</option>
                  <option>Restaurant</option>
                  <option>Hotel</option>
                  <option>Bakery</option>
                  <option>Catering</option>
                  <option>Event Organizer</option>
                  <option>Other</option>
                </select>
              </div>
            </div>
          )}

          {/* NGO-specific */}
          {form.role === 'NGO' && (
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">NGO Name *</label>
                <input type="text" name="ngoName" className="form-control"
                  placeholder="Legal NGO name" value={form.ngoName}
                  onChange={handleChange} />
              </div>
              <div className="form-group">
                <label className="form-label">Registration Number</label>
                <input type="text" name="registrationNumber" className="form-control"
                  placeholder="Reg. number" value={form.registrationNumber}
                  onChange={handleChange} />
              </div>
            </div>
          )}

          <div className="form-group">
            <label className="form-label">Address</label>
            <input type="text" name="address" className="form-control"
              placeholder="Your address (for location-based matching)"
              value={form.address} onChange={handleChange} />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Latitude (optional)</label>
              <input type="number" step="any" name="latitude" className="form-control"
                placeholder="e.g. 12.9716" value={form.latitude} onChange={handleChange} />
            </div>
            <div className="form-group">
              <label className="form-label">Longitude (optional)</label>
              <input type="number" step="any" name="longitude" className="form-control"
                placeholder="e.g. 77.5946" value={form.longitude} onChange={handleChange} />
            </div>
          </div>

          {form.role === 'NGO' && (
            <div className="alert alert-info" style={{ fontSize: 13 }}>
              ℹ️ NGO accounts require admin approval before you can accept donations.
            </div>
          )}

          <button type="submit" className="btn btn-primary"
            style={{ width: '100%', justifyContent: 'center', marginTop: 8 }}
            disabled={loading}>
            {loading ? 'Creating account...' : 'Create Account →'}
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: 20, fontSize: 14, color: '#6c757d' }}>
          Already have an account? <Link to="/login" style={{ color: 'var(--primary)' }}>Login here</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
