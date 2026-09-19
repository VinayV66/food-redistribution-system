import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { donationApi } from '../../api/apiService';

const categories = ['COOKED_FOOD','BAKERY','FRUITS','VEGETABLES','GROCERIES','PACKAGED_FOOD','OTHER'];
const units = ['kg','g','litres','pieces','servings','boxes','packets'];

const CreateDonation = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Default expiry: 4 hours from now
  const defaultExpiry = () => {
    const d = new Date(Date.now() + 4 * 60 * 60 * 1000);
    return d.toISOString().slice(0, 16);
  };

  const [form, setForm] = useState({
    foodName: '', foodCategory: 'COOKED_FOOD', description: '',
    quantity: '', quantityUnit: 'kg',
    preparationTime: '', expiryTime: defaultExpiry(),
    pickupStartTime: '', pickupEndTime: '',
    vegetarian: false, allergens: '', packagingInformation: '',
    pickupAddress: '', latitude: '', longitude: '',
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await donationApi.create({
        ...form,
        quantity: parseFloat(form.quantity),
        latitude: form.latitude ? parseFloat(form.latitude) : null,
        longitude: form.longitude ? parseFloat(form.longitude) : null,
        expiryTime: new Date(form.expiryTime).toISOString(),
        pickupStartTime: new Date(form.pickupStartTime).toISOString(),
        pickupEndTime: new Date(form.pickupEndTime).toISOString(),
        preparationTime: form.preparationTime ? new Date(form.preparationTime).toISOString() : null,
      });
      setSuccess('Donation posted successfully! NGOs can now see your donation.');
      setTimeout(() => navigate('/donor/my-donations'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create donation.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page" style={{ maxWidth: 720 }}>
      <h1 className="page-title">🍱 Donate Food</h1>
      <p className="page-subtitle">Fill in the details about the food you want to donate</p>

      {error   && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <form onSubmit={handleSubmit} className="card">
        <div className="form-group">
          <label className="form-label">Food Name *</label>
          <input type="text" name="foodName" className="form-control"
            placeholder="e.g. Biryani, Bread loaves, Mixed vegetables"
            value={form.foodName} onChange={handleChange} required />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label">Category *</label>
            <select name="foodCategory" className="form-control" value={form.foodCategory} onChange={handleChange}>
              {categories.map(c => <option key={c}>{c.replace('_', ' ')}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">Vegetarian?</label>
            <div style={{ padding: '10px 0' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: 8, cursor: 'pointer' }}>
                <input type="checkbox" name="vegetarian" checked={form.vegetarian} onChange={handleChange} style={{ width: 18, height: 18 }} />
                Yes, this is vegetarian food 🌱
              </label>
            </div>
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Description</label>
          <textarea name="description" className="form-control" rows={3}
            placeholder="Describe the food: freshness, contents, special notes..."
            value={form.description} onChange={handleChange} style={{ resize: 'vertical' }} />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label">Quantity *</label>
            <input type="number" name="quantity" className="form-control"
              placeholder="0" min="0.1" step="0.1"
              value={form.quantity} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label className="form-label">Unit *</label>
            <select name="quantityUnit" className="form-control" value={form.quantityUnit} onChange={handleChange}>
              {units.map(u => <option key={u}>{u}</option>)}
            </select>
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label">Prepared At</label>
            <input type="datetime-local" name="preparationTime" className="form-control"
              value={form.preparationTime} onChange={handleChange} />
          </div>
          <div className="form-group">
            <label className="form-label">Expires At *</label>
            <input type="datetime-local" name="expiryTime" className="form-control"
              value={form.expiryTime} onChange={handleChange} required />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label">Pickup Available From *</label>
            <input type="datetime-local" name="pickupStartTime" className="form-control"
              value={form.pickupStartTime} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label className="form-label">Pickup Available Until *</label>
            <input type="datetime-local" name="pickupEndTime" className="form-control"
              value={form.pickupEndTime} onChange={handleChange} required />
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Pickup Address *</label>
          <input type="text" name="pickupAddress" className="form-control"
            placeholder="Full address where food can be picked up"
            value={form.pickupAddress} onChange={handleChange} required />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label">Latitude (GPS)</label>
            <input type="number" step="any" name="latitude" className="form-control"
              placeholder="e.g. 12.9716" value={form.latitude} onChange={handleChange} />
          </div>
          <div className="form-group">
            <label className="form-label">Longitude (GPS)</label>
            <input type="number" step="any" name="longitude" className="form-control"
              placeholder="e.g. 77.5946" value={form.longitude} onChange={handleChange} />
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Allergens</label>
          <input type="text" name="allergens" className="form-control"
            placeholder="e.g. Contains nuts, dairy, gluten..."
            value={form.allergens} onChange={handleChange} />
        </div>

        <div className="form-group">
          <label className="form-label">Packaging Information</label>
          <input type="text" name="packagingInformation" className="form-control"
            placeholder="e.g. In sealed containers, bring own bags..."
            value={form.packagingInformation} onChange={handleChange} />
        </div>

        <div style={{ display: 'flex', gap: 12 }}>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Posting...' : '✅ Post Donation'}
          </button>
          <button type="button" className="btn btn-outline" onClick={() => navigate('/donor/my-donations')}>
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateDonation;
