import React, { useState, useEffect } from 'react';

const VerifyOtp = ({ apiFetch, tempEmail, onNavigate, showToast }) => {
  const [email, setEmail] = useState(tempEmail || '');
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);
  const [countdown, setCountdown] = useState(60); // 60 seconds countdown
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [countdown]);

  const validate = () => {
    const newErrors = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!emailRegex.test(email)) {
      newErrors.email = 'Invalid email address';
    }

    if (!otp.trim()) {
      newErrors.otp = 'OTP is required';
    } else if (otp.length !== 6) {
      newErrors.otp = 'OTP must be exactly 6 digits';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleVerify = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      const response = await apiFetch('/api/auth/verify-otp', {
        method: 'POST',
        body: JSON.stringify({ email, otp }),
      });

      if (response.success) {
        showToast('success', 'Verified Successfully', 'Your account has been activated. Please log in.');
        onNavigate('login');
      } else {
        showToast('error', 'Verification Failed', response.message || 'Invalid code.');
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

  const handleResend = async () => {
    if (countdown > 0 || !email.trim()) return;

    setResending(true);
    try {
      const response = await apiFetch('/api/auth/generate-otp', {
        method: 'POST',
        body: JSON.stringify({ email }),
      });

      if (response.success) {
        showToast('success', 'OTP Resent', `A new OTP has been sent to ${email}`);
        setCountdown(60); // Reset timer to 60s
      } else {
        showToast('error', 'Resend Failed', response.message || 'Could not resend OTP.');
      }
    } catch (err) {
      showToast('error', 'Error', err.message || 'Failed to resend verification code.');
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="glass-card">
      <h2 className="title-gradient">Verify Account</h2>
      <p className="subtitle">Enter the 6-digit OTP code sent to your email to activate your account</p>

      <form onSubmit={handleVerify} noValidate>
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
              disabled={loading || tempEmail} // Lock if autofilled from registration
            />
            <svg className="form-input-icon" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
          </div>
          {errors.email && <span className="error-msg">{errors.email}</span>}
        </div>

        {/* OTP Input */}
        <div className="form-group">
          <label className="form-label" htmlFor="otp">6-Digit OTP</label>
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
                // only digits
                const val = e.target.value.replace(/\D/g, '');
                setOtp(val);
                if (errors.otp) setErrors({ ...errors, otp: null });
              }}
              disabled={loading}
            />
          </div>
          {errors.otp && <span className="error-msg">{errors.otp}</span>}
        </div>

        <button type="submit" className="btn-primary" style={{ marginTop: '1.5rem' }} disabled={loading}>
          {loading ? <span className="spinner"></span> : 'Verify Account'}
        </button>
      </form>

      <div className="timer-container">
        <span>Didn't receive a code?</span>
        {countdown > 0 ? (
          <span>
            Resend in <span className="timer">{countdown}s</span>
          </span>
        ) : (
          <span
            className="card-link"
            style={{ pointerEvents: resending ? 'none' : 'auto', opacity: resending ? 0.6 : 1 }}
            onClick={handleResend}
          >
            {resending ? 'Sending...' : 'Resend OTP'}
          </span>
        )}
      </div>

      <div className="card-footer" style={{ marginTop: '2rem' }}>
        Back to{' '}
        <span className="card-link" onClick={() => onNavigate('login')}>
          Sign In
        </span>
      </div>
    </div>
  );
};

export default VerifyOtp;
