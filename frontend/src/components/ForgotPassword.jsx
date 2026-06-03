import React, { useState } from 'react';

const ForgotPassword = ({ apiFetch, onNavigate, showToast, setTempEmail }) => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!emailRegex.test(email)) {
      newErrors.email = 'Please enter a valid email address';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      const response = await apiFetch('/api/auth/forgot-password', {
        method: 'POST',
        body: JSON.stringify({ email }),
      });

      if (response.success) {
        showToast('success', 'OTP Sent', `A password reset OTP has been sent to ${email}`);
        setTempEmail(email); // Save email globally in state so reset screen knows it
        onNavigate('reset-password');
      } else {
        showToast('error', 'Request Failed', response.message || 'Failed to request reset OTP.');
      }
    } catch (err) {
      if (err.status === 404 || (err.message && err.message.toLowerCase().includes('not found'))) {
        setErrors({ email: 'No account found with this email' });
        showToast('error', 'Account Not Found', 'No account exists with this email address.');
      } else {
        showToast('error', 'Error', err.message || 'Something went wrong. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="glass-card">
      <h2 className="title-gradient">Reset Password</h2>
      <p className="subtitle">Enter the email associated with your account and we'll send you an OTP to reset your password</p>

      <form onSubmit={handleSubmit} noValidate>
        {/* Email Address */}
        <div className="form-group" style={{ marginBottom: '2rem' }}>
          <label className="form-label" htmlFor="email">Email Address</label>
          <div className="input-container">
            <input
              type="email"
              id="email"
              className="form-input"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => {
                setEmail(e.target.value);
                if (errors.email) setErrors({ ...errors, email: null });
              }}
              disabled={loading}
            />
            <svg className="form-input-icon" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
          </div>
          {errors.email && <span className="error-msg">{errors.email}</span>}
        </div>

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? <span className="spinner"></span> : 'Send Reset Code'}
        </button>
      </form>

      <div className="card-footer">
        Back to{' '}
        <span className="card-link" onClick={() => onNavigate('login')}>
          Sign In
        </span>
      </div>
    </div>
  );
};

export default ForgotPassword;
