import { useState, useEffect } from 'react';
import { ngoApi } from '../../api/apiService';

const AvailableDonations = () => {
  const [data, setData]     = useState({ content: [], totalPages: 0 });
  const [page, setPage]     = useState(0);
  const [loading, setLoading] = useState(true);
  const [accepting, setAccepting] = useState(null);
  const [message, setMessage] = useState({ type: '', text: '' });

  const fetchDonations = async () => {
    setLoading(true);
    try {
      const res = await ngoApi.getAvailableDonations({ page, size: 9 });
      setData(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchDonations(); }, [page]);

  const handleAccept = async (donationId) => {
    setAccepting(donationId);
    setMessage({ type: '', text: '' });
    try {
      await ngoApi.acceptDonation(donationId);
      setMessage({ type: 'success', text: '✅ Donation accepted! Check My Pickups for details.' });
      fetchDonations();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Failed to accept donation.' });
    } finally {
      setAccepting(null);
    }
  };

  const timeLeft = (expiryTime) => {
    const diff = new Date(expiryTime) - Date.now();
    if (diff <= 0) return '⚠️ Expired';
    const h = Math.floor(diff / 3600000);
    const m = Math.floor((diff % 3600000) / 60000);
    return `${h}h ${m}m remaining`;
  };

  return (
    <div className="page">
      <h1 className="page-title">🍱 Available Donations</h1>
      <p className="page-subtitle">Browse and accept food donations near you</p>

      {message.text && (
        <div className={`alert alert-${message.type === 'success' ? 'success' : 'error'}`}>
          {message.text}
        </div>
      )}

      {loading ? (
        <div className="spinner-center"><div className="spinner"></div></div>
      ) : data.content.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: 48 }}>
          <p style={{ fontSize: 48 }}>🍽️</p>
          <p style={{ color: '#6c757d', marginTop: 12 }}>No available donations right now. Check back soon!</p>
        </div>
      ) : (
        <>
          <div className="donation-grid">
            {data.content.map(d => (
              <div key={d.id} className="donation-card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 8 }}>
                  <h3 className="donation-card-title">{d.foodName}</h3>
                  {d.vegetarian && <span title="Vegetarian" style={{ fontSize: 20 }}>🌱</span>}
                </div>
                <div className="donation-card-info">
                  <span>🏷️ {d.foodCategory?.replace('_', ' ')}</span>
                  <span>⚖️ {d.quantity} {d.quantityUnit}</span>
                  <span>📍 {d.pickupAddress}</span>
                  <span>🏪 {d.donorName}</span>
                  <span style={{ color: d.expiryTime && new Date(d.expiryTime) < new Date() ? 'var(--danger)' : 'var(--success)' }}>
                    ⏳ {timeLeft(d.expiryTime)}
                  </span>
                </div>
                {d.description && (
                  <p style={{ fontSize: 13, color: '#6c757d', marginBottom: 12 }}>{d.description}</p>
                )}
                <button
                  className="btn btn-primary"
                  style={{ width: '100%', justifyContent: 'center' }}
                  onClick={() => handleAccept(d.id)}
                  disabled={accepting === d.id}>
                  {accepting === d.id ? 'Accepting...' : '✅ Accept Donation'}
                </button>
              </div>
            ))}
          </div>
          {data.totalPages > 1 && (
            <div className="pagination">
              {Array.from({ length: data.totalPages }, (_, i) => (
                <button key={i} className={`page-btn${page === i ? ' active' : ''}`} onClick={() => setPage(i)}>{i + 1}</button>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default AvailableDonations;
