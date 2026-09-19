import { useState, useEffect } from 'react';
import { volunteerApi } from '../../api/apiService';

const MyTasks = () => {
  const [data, setData]       = useState({ content: [], totalPages: 0 });
  const [page, setPage]       = useState(0);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);
  const [message, setMessage] = useState({ type: '', text: '' });

  const fetchTasks = () => {
    setLoading(true);
    volunteerApi.getMyTasks({ page, size: 10 })
      .then(res => setData(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchTasks(); }, [page]);

  const action = async (fn, id, successMsg) => {
    setActionLoading(id);
    setMessage({ type: '', text: '' });
    try {
      await fn(id);
      setMessage({ type: 'success', text: successMsg });
      fetchTasks();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Action failed.' });
    } finally {
      setActionLoading(null);
    }
  };

  return (
    <div className="page">
      <h1 className="page-title">My Pickup Tasks</h1>
      <p className="page-subtitle">Track all your food delivery tasks</p>

      {message.text && (
        <div className={`alert alert-${message.type === 'success' ? 'success' : 'error'}`}>
          {message.text}
        </div>
      )}

      {loading ? (
        <div className="spinner-center"><div className="spinner"></div></div>
      ) : (
        <div className="card">
          {data.content.length === 0 ? (
            <div style={{ textAlign: 'center', padding: 40, color: '#6c757d' }}>
              <p>No tasks yet. Accept a task from Available Tasks.</p>
            </div>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>#</th><th>Food</th><th>NGO</th><th>Pickup Address</th>
                    <th>Status</th><th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.content.map(t => (
                    <tr key={t.id}>
                      <td>{t.id}</td>
                      <td><strong>{t.donationFoodName}</strong></td>
                      <td>{t.ngoName}</td>
                      <td style={{ fontSize: 13 }}>{t.pickupAddress}</td>
                      <td><span className={`badge badge-${t.status.toLowerCase()}`}>{t.status}</span></td>
                      <td>
                        <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                          {t.status === 'ACCEPTED' && (
                            <button className="btn btn-warning btn-sm"
                              disabled={actionLoading === t.id}
                              onClick={() => action(volunteerApi.collectFood, t.id, '✅ Food marked as collected!')}>
                              {actionLoading === t.id ? '...' : '📦 Mark Collected'}
                            </button>
                          )}
                          {t.status === 'COLLECTED' && (
                            <button className="btn btn-success btn-sm"
                              disabled={actionLoading === t.id}
                              onClick={() => action(volunteerApi.deliverFood, t.id, '🎉 Food delivered!')}>
                              {actionLoading === t.id ? '...' : '🚚 Mark Delivered'}
                            </button>
                          )}
                          {t.status === 'DELIVERED' && (
                            <span style={{ color: 'var(--success)', fontSize: 13 }}>
                              ✅ Delivered {t.deliveredAt ? new Date(t.deliveredAt).toLocaleDateString() : ''}
                            </span>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          {data.totalPages > 1 && (
            <div className="pagination">
              {Array.from({ length: data.totalPages }, (_, i) => (
                <button key={i} className={`page-btn${page === i ? ' active' : ''}`} onClick={() => setPage(i)}>{i + 1}</button>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default MyTasks;
