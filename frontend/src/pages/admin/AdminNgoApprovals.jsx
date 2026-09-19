import { useState, useEffect } from 'react';
import { adminApi } from '../../api/apiService';

const AdminNgoApprovals = () => {
  const [data, setData]       = useState({ content: [], totalPages: 0 });
  const [page, setPage]       = useState(0);
  const [loading, setLoading] = useState(true);
  const [actioning, setActioning] = useState(null);
  const [message, setMessage] = useState({ type: '', text: '' });

  const fetchNgos = () => {
    setLoading(true);
    adminApi.getPendingNgos({ page, size: 10 })
      .then(res => setData(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchNgos(); }, [page]);

  const handleAction = async (userId, approve) => {
    setActioning(userId);
    setMessage({ type: '', text: '' });
    try {
      if (approve) {
        await adminApi.approveNgo(userId);
        setMessage({ type: 'success', text: '✅ NGO approved! They can now accept donations.' });
      } else {
        const reason = prompt('Enter rejection reason (optional):') || 'Application incomplete.';
        await adminApi.rejectNgo(userId, reason);
        setMessage({ type: 'error', text: 'NGO rejected.' });
      }
      fetchNgos();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Action failed.' });
    } finally {
      setActioning(null);
    }
  };

  return (
    <div className="page">
      <h1 className="page-title">🏢 NGO Approval Requests</h1>
      <p className="page-subtitle">Review and approve NGO registration applications</p>

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
              <p>✅ No pending NGO approval requests.</p>
            </div>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr><th>NGO Name</th><th>Reg. Number</th><th>Address</th><th>Capacity/day</th><th>Actions</th></tr>
                </thead>
                <tbody>
                  {data.content.map(ngo => (
                    <tr key={ngo.id}>
                      <td><strong>{ngo.ngoName}</strong></td>
                      <td>{ngo.registrationNumber || '—'}</td>
                      <td style={{ fontSize: 13 }}>{ngo.address || '—'}</td>
                      <td>{ngo.dailyCapacity ? `${ngo.dailyCapacity} kg` : '—'}</td>
                      <td>
                        <div style={{ display: 'flex', gap: 6 }}>
                          <button className="btn btn-success btn-sm"
                            disabled={actioning === ngo.user?.id}
                            onClick={() => handleAction(ngo.user?.id, true)}>
                            ✅ Approve
                          </button>
                          <button className="btn btn-danger btn-sm"
                            disabled={actioning === ngo.user?.id}
                            onClick={() => handleAction(ngo.user?.id, false)}>
                            ❌ Reject
                          </button>
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

export default AdminNgoApprovals;
