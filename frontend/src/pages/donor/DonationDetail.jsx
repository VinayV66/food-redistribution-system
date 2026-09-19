import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { donationApi } from '../../api/apiService';

const statusBadge = (s) => <span className={`badge badge-${s.toLowerCase()}`}>{s}</span>;

const DonationDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [donation, setDonation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  useEffect(() => {
    donationApi.getById(id)
      .then(res => setDonation(res.data))
      .catch(() => navigate('/donor/my-donations'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleCancel = async () => {
    if (!confirm('Cancel this donation?')) return;
    try {
      await donationApi.cancel(id);
      navigate('/donor/my-donations');
    } catch (err) {
      setMessage(err.response?.data?.message || 'Failed to cancel.');
    }
  };

  if (loading) return <div className="spinner-center"><div className="spinner"></div></div>;
  if (!donation) return null;

  const row = (label, value) => (
    <div style={{ display: 'flex', padding: '12px 0', borderBottom: '1px solid var(--border)' }}>
      <span style={{ width: 180, fontWeight: 500, color: '#6c757d', flexShrink: 0 }}>{label}</span>
      <span>{value ?? '—'}</span>
    </div>
  );

  return (
    <div className="page" style={{ maxWidth: 720 }}>
      <div style={{ display: 'flex', gap: 12, marginBottom: 24 }}>
        <button className="btn btn-outline btn-sm" onClick={() => navigate(-1)}>← Back</button>
      </div>

      {message && <div className="alert alert-error">{message}</div>}

      <div className="card">
        <div className="card-header">
          <div>
            <h2 style={{ fontSize: 22, fontWeight: 700 }}>{donation.foodName}</h2>
            <p style={{ color: '#6c757d', fontSize: 14 }}>Donation #{donation.id}</p>
          </div>
          {statusBadge(donation.status)}
        </div>

        {row('Category', donation.foodCategory?.replace('_', ' '))}
        {row('Quantity', `${donation.quantity} ${donation.quantityUnit}`)}
        {row('Vegetarian', donation.vegetarian ? '✅ Yes' : '❌ No')}
        {row('Description', donation.description)}
        {row('Allergens', donation.allergens)}
        {row('Packaging', donation.packagingInformation)}
        {row('Pickup Address', donation.pickupAddress)}
        {row('Prepared At', donation.preparationTime ? new Date(donation.preparationTime).toLocaleString() : null)}
        {row('Expires At', new Date(donation.expiryTime).toLocaleString())}
        {row('Pickup Window', `${new Date(donation.pickupStartTime).toLocaleString()} — ${new Date(donation.pickupEndTime).toLocaleString()}`)}
        {row('Posted On', new Date(donation.createdAt).toLocaleString())}
        {donation.assignedNgoName && row('Accepted by NGO', donation.assignedNgoName)}

        {donation.status === 'AVAILABLE' && (
          <div style={{ marginTop: 20, display: 'flex', gap: 12 }}>
            <Link to={`/donor/create-donation`} className="btn btn-outline">Edit</Link>
            <button className="btn btn-danger" onClick={handleCancel}>Cancel Donation</button>
          </div>
        )}
      </div>
    </div>
  );
};

export default DonationDetail;
