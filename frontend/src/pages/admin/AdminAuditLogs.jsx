import { useState, useEffect } from 'react';
import { adminApi } from '../../api/apiService';

const AdminAuditLogs = () => {
  const [data, setData]       = useState({ content: [], totalPages: 0 });
  const [page, setPage]       = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    adminApi.getAuditLogs({ page, size: 20 })
      .then(res => setData(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [page]);

  return (
    <div className="page">
      <h1 className="page-title">📋 Audit Logs</h1>
      <p className="page-subtitle">Track all important system actions</p>

      {loading ? (
        <div className="spinner-center"><div className="spinner"></div></div>
      ) : (
        <div className="card">
          <div className="table-wrapper">
            <table>
              <thead>
                <tr><th>Time</th><th>User</th><th>Action</th><th>Entity</th><th>Description</th></tr>
              </thead>
              <tbody>
                {data.content.map(log => (
                  <tr key={log.id}>
                    <td style={{ fontSize: 12, whiteSpace: 'nowrap' }}>{new Date(log.timestamp).toLocaleString()}</td>
                    <td>{log.userId || 'System'}</td>
                    <td><code style={{ fontSize: 12, background: '#f1f3f5', padding: '2px 6px', borderRadius: 4 }}>{log.action}</code></td>
                    <td style={{ fontSize: 13 }}>{log.entityType} #{log.entityId}</td>
                    <td style={{ fontSize: 13, color: '#6c757d' }}>{log.description}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
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

export default AdminAuditLogs;
