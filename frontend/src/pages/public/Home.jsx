import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Home = () => {
  const { isAuthenticated } = useAuth();

  return (
    <div>
      {/* Hero Section */}
      <div className="hero">
        <h1>🍱 Smart Food Rescue</h1>
        <p>
          Connecting food donors with NGOs and volunteers to eliminate food waste
          and feed those who need it most.
        </p>
        <div className="hero-buttons">
          {isAuthenticated ? (
            <Link to="/dashboard" className="hero-btn hero-btn-primary">
              Go to Dashboard →
            </Link>
          ) : (
            <>
              <Link to="/register" className="hero-btn hero-btn-primary">
                Get Started Free
              </Link>
              <Link to="/login" className="hero-btn hero-btn-outline">
                Login
              </Link>
            </>
          )}
        </div>
      </div>

      {/* Impact Stats */}
      <div style={{ padding: '48px 24px', background: '#f8f9fa' }}>
        <div style={{ maxWidth: 900, margin: '0 auto', textAlign: 'center' }}>
          <h2 style={{ fontSize: 28, fontWeight: 700, marginBottom: 8 }}>Making Real Impact</h2>
          <p style={{ color: '#6c757d', marginBottom: 36 }}>Together we can solve hunger and food waste</p>
          <div className="stats-grid">
            <div className="stat-card secondary">
              <div className="stat-value">50K+</div>
              <div className="stat-label">Meals Rescued</div>
            </div>
            <div className="stat-card success">
              <div className="stat-value">200+</div>
              <div className="stat-label">Active Donors</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">80+</div>
              <div className="stat-label">Partner NGOs</div>
            </div>
            <div className="stat-card warning">
              <div className="stat-value">500+</div>
              <div className="stat-label">Volunteers</div>
            </div>
          </div>
        </div>
      </div>

      {/* Features */}
      <div className="features">
        <div style={{ textAlign: 'center', marginBottom: 36 }}>
          <h2 style={{ fontSize: 28, fontWeight: 700 }}>How It Works</h2>
          <p style={{ color: '#6c757d' }}>Simple, fast, impactful</p>
        </div>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">🏪</div>
            <div className="feature-title">Donors Post Food</div>
            <div className="feature-desc">
              Restaurants, hotels, and events list surplus food with quantity, location, and pickup time.
            </div>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🏢</div>
            <div className="feature-title">NGOs Accept</div>
            <div className="feature-desc">
              Verified NGOs browse available donations nearby and accept what they need.
            </div>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🚗</div>
            <div className="feature-title">Volunteers Deliver</div>
            <div className="feature-desc">
              Community volunteers pick up and deliver food from donor to NGO.
            </div>
          </div>
          <div className="feature-card">
            <div className="feature-icon">📊</div>
            <div className="feature-title">Track Impact</div>
            <div className="feature-desc">
              Real-time dashboard shows food rescued, meals delivered, and community impact.
            </div>
          </div>
        </div>
      </div>

      {/* CTA */}
      <div style={{ background: 'var(--primary-dark)', color: 'white', padding: '60px 24px', textAlign: 'center' }}>
        <h2 style={{ fontSize: 32, fontWeight: 700, marginBottom: 16 }}>Join the Movement</h2>
        <p style={{ opacity: 0.85, marginBottom: 28 }}>Register as a donor, NGO, or volunteer today — it's free!</p>
        <Link to="/register" className="hero-btn hero-btn-primary">Create Account →</Link>
      </div>
    </div>
  );
};

export default Home;
