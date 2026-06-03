import React, { useState } from 'react';

const ResetPassword = ({ apiFetch, tempEmail, onNavigate, showToast }) => {
  const [email, setEmail] = useState(tempEmail || '');
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!emailRegex.test(email)) {
      newErrors.email = 'Invalid email address';
    }

    if (!otp.trim()) {
      newErrors.otp = 'OTP code is required';
    } else if (otp.length !== 6) {
      newErrors.otp = 'OTP must be exactly 6 digits';
    }

    if (!newPassword) {
      newErrors.newPassword = 'New password is required';
    } else if (newPassword.length < 6) {
      newErrors.newPassword = 'Password must be at least 6 characters';
    }

    if (newPassword !== confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      const response = await apiFetch('/api/auth/reset-password', {
        method: 'POST',
        body: JSON.stringify({
          email,
          otp,
          newPassword,
        }),
      });

      if (response.success) {
        showToast('success', 'Password Reset Successful', 'Your password has been changed. Please log in.');
        onNavigate('login');
      } else {
        showToast('error', 'Reset Failed', response.message || 'Could not reset password.');
      }
    } catch (err) {
      if (err.status === 410) {
        showToast('error', 'OTP Expired', 'The OTP has expired. Please request a new one.');
        setErrors({ otp: 'Expired OTP. Please request a new code.' });
      } else if (err.status === 400) {
        showToast('error', 'Invalid OTP', 'The OTP you entered is incorrect.');
        setErrors({ otp: 'Invalid verification code.' });
      } else {
        showToast('error', 'Error', err.message || 'Something went wrong.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="glass-card">
      <h2 className="title-gradient">New Password</h2>
      <p className="subtitle">Enter the verification code sent to your email and your new password</p>

      <form onSubmit={handleSubmit} noValidate>
        {/* Email Address */}
        <div className="form-group">
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
              disabled={loading || tempEmail}
            />
            <svg className="form-input-icon" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
          </div>
          {errors.email && <span className="error-msg">{errors.email}</span>}
        </div>

        {/* OTP Input */}
        <div className="form-group">
          <label className="form-label" htmlFor="otp">Reset OTP Code</label>
          <div className="input-container">
            <input
              type="text"
              id="otp"
              className="form-input"
              style={{ letterSpacing: '0.25em', fontSize: '1.2rem', textAlign: 'center', paddingLeft: '1rem' }}
              placeholder="000000"
              maxLength="6"
              value={otp}
              onChange={(e) => {
                const val = e.target.value.replace(/\D/g, '');
                setOtp(val);
                if (errors.otp) setErrors({ ...errors, otp: null });
              }}
              disabled={loading}
            />
          </div>
          {errors.otp && <span className="error-msg">{errors.otp}</span>}
        </div>

        {/* New Password */}
        <div className="form-group">
          <label className="form-label" htmlFor="newPassword">New Password</label>
          <div className="input-container">
            <input
              type={showPassword ? 'text' : 'password'}
              id="newPassword"
              className="form-input"
              placeholder="Enter new password"
              value={newPassword}
              onChange={(e) => {
                setNewPassword(e.target.value);
                if (errors.newPassword) setErrors({ ...errors, newPassword: null });
              }}
              disabled={loading}
            />
            <svg className="form-input-icon" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
            <button
              type="button"
              className="form-input-action"
              onClick={() => setShowPassword(!showPassword)}
              tabIndex="-1"
            >
              {showPassword ? (
                <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
                </svg>
              ) : (
                <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              )}
            </button>
          </div>
          {errors.newPassword && <span className="error-msg">{errors.newPassword}</span>}
        </div>

        {/* Confirm Password */}
        <div className="form-group">
          <label className="form-label" htmlFor="confirmPassword">Confirm Password</label>
          <div className="input-container">
            <input
              type={showPassword ? 'text' : 'password'}
              id="confirmPassword"
              className="form-input"
              placeholder="Confirm new password"
              value={confirmPassword}
              onChange={(e) => {
                setConfirmPassword(e.target.value);
                if (errors.confirmPassword) setErrors({ ...errors, confirmPassword: null });
              }}
              disabled={loading}
            />
            <svg className="form-input-icon" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
            </svg>
          </div>
          {errors.confirmPassword && <span className="error-msg">{errors.confirmPassword}</span>}
        </div>

        <button type="submit" className="btn-primary" style={{ marginTop: '2rem' }} disabled={loading}>
          {loading ? <span className="spinner"></span> : 'Reset Password'}
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

export default ResetPassword;
