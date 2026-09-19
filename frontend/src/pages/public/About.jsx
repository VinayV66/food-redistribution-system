const About = () => (
  <div style={{ maxWidth: 800, margin: '48px auto', padding: '0 24px' }}>
    <h1 style={{ fontSize: 36, fontWeight: 800, color: 'var(--primary-dark)', marginBottom: 12 }}>
      🍱 About Food Rescue
    </h1>
    <p style={{ fontSize: 18, color: '#6c757d', marginBottom: 32 }}>
      Reducing food waste, fighting hunger — one meal at a time.
    </p>
    <div className="card" style={{ marginBottom: 24 }}>
      <h2 style={{ fontSize: 22, marginBottom: 12 }}>Our Mission</h2>
      <p>Smart Food Rescue & Redistribution System connects surplus food from restaurants, hotels,
      bakeries, and event organizers with NGOs and underprivileged communities — reducing food waste
      while addressing hunger.</p>
    </div>
    <div className="card" style={{ marginBottom: 24 }}>
      <h2 style={{ fontSize: 22, marginBottom: 16 }}>How It Works</h2>
      <div style={{ display: 'grid', gap: 16 }}>
        {[
          ['🏪 Donors', 'Register your restaurant, hotel, or bakery and post surplus food in seconds.'],
          ['🏢 NGOs', 'Browse and accept donations near you. Our matching algorithm finds the closest available food.'],
          ['🚗 Volunteers', 'Pick up food from donors and deliver to NGOs. Every delivery counts.'],
          ['👨‍💼 Admins', 'Manage the platform, approve NGOs, handle complaints, and view analytics.'],
        ].map(([title, desc]) => (
          <div key={title} style={{ display: 'flex', gap: 16, alignItems: 'flex-start' }}>
            <div style={{ fontSize: 32 }}>{title.split(' ')[0]}</div>
            <div>
              <strong>{title}</strong>
              <p style={{ color: '#6c757d', fontSize: 14, marginTop: 4 }}>{desc}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
    <div className="card">
      <h2 style={{ fontSize: 22, marginBottom: 12 }}>Technology</h2>
      <p>Built with <strong>Spring Boot 3</strong> (Java 17), <strong>PostgreSQL</strong>,
      <strong> Redis</strong> caching, and a <strong>React.js</strong> frontend.
      JWT authentication, location-based matching with Haversine formula,
      and automated expiry scheduling ensure a robust, production-ready platform.</p>
    </div>
  </div>
);

export default About;
