import { useState, useEffect } from 'react';
import { volunteerApi } from '../../api/apiService';

const AvailableTasks = () => {
  const [data, setData]         = useState({ content: [], totalPages: 0 });
  const [page, setPage]         = useState(0);
  const [loading, setLoading]   = useState(true);
  const [accepting, setAccepting] = useState(null);
  const [message, setMessage]   = useState({ type: '', text: '' });

  const fetchTasks = () => {
    setLoading(true);
    volunteerApi.getAvailableTasks({ page, size: 9 })
      .then(res => setData(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchTasks(); }, [page]);

  const handleAccept = async (id) => {
    setAccepting(id);
    setMessage({ type: '', text: '' });
    try {
      await volunteerApi.acceptTask(id);
      setMessage({ type: 'success', text: '✅ Task accepted! Check My Tasks for details.' });
      fetchTasks();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Failed to accept task.' });
    } finally {
      setAccepting(null);
    }
  };

  return (
    <div className="page">
      <h1 className="page-title">🚗 Available Pickup Tasks</h1>
      <p className="page-subtitle">Find food pickup tasks near you</p>

      {message.text && (
        <div className={`alert alert-${message.type === 'success' ? 'success' : 'error'}`}>
          {message.text}
        </div>
      )}

      {loading ? (
        <div className="spinner-center"><div className="spinner"></div></div>
      ) : data.content.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: 48 }}>
          <p style={{ fontSize: 48 }}>🚗</p>
          <p style={{ color: '#6c757d', marginTop: 12 }}>No pickup tasks available right now. Check back soon!</p>
        </div>
      ) : (
        <>
          <div className="donation-grid">
            {data.content.map(t => (
              <div key={t.id} className="donation-card">
                <h3 className="donation-card-title">{t.donationFoodName}</h3>
                <div className="donation-card-info">
                  <span>🏢 NGO: {t.ngoName}</span>
                  <span>📍 Pickup: {t.pickupAddress}</span>
                  {t.scheduledPickupTime && (
                    <span>🕐 {new Date(t.scheduledPickupTime).toLocaleString()}</span>
                  )}
                </div>
                <button
                  className="btn btn-success"
                  style={{ width: '100%', justifyContent: 'center' }}
                  onClick={() => handleAccept(t.id)}
                  disabled={accepting === t.id}>
                  {accepting === t.id ? 'Accepting...' : '🚗 Accept Task'}
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

export default AvailableTasks;
