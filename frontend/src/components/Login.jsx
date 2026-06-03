import React, { useState } from 'react';

const Login = ({ apiFetch, onLoginSuccess, onNavigate, showToast }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};
    if (!username.trim()) {
      newErrors.username = 'Username is required';
    }
    if (!password) {
      newErrors.password = 'Password is required';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      const response = await apiFetch('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password }),
      });

      if (response.success && response.data) {
        showToast('success', 'Logged In', 'Welcome back to your workspace!');
        onLoginSuccess(response.data.accessToken, {
          username: response.data.userName,
          email: response.data.email,
          role: response.data.role,
        });
      } else {
        // Fallback error fallback if response parses but success = false
        showToast('error', 'Login Failed', response.message || 'Invalid credentials');
      }
    } catch (err) {
      // Custom handler for specific exceptions
      if (err.status === 401) {
        if (err.message && err.message.toLowerCase().includes('verify')) {
          showToast('warning', 'Unverified Account', 'Please verify your email before logging in.');
          // Automatically redirect to OTP verification screen for this email
          // We don't have the user's email here directly from LoginRequest, but we can redirect to OTP verify 
          // and let them enter their email, or we can prompt them. Let's redirect to 'verify-otp'.
          onNavigate('verify-otp');
        } else {
          showToast('error', 'Unauthorized', 'Invalid username or password.');
        }
      } else if (err.errors) {
        setErrors(err.errors);
        showToast('error', 'Validation Error', 'Please check the input values.');
      } else {
        showToast('error', 'Error', err.message || 'Something went wrong. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="glass-card">
      <h2 className="title-gradient">Welcome back</h2>
      <p className="subtitle">Enter your credentials to access your secure portal</p>

      <form onSubmit={handleSubmit} noValidate>
        {/* Username */}
        <div className="form-group">
          <label className="form-label" htmlFor="username">Username</label>
          <div className="input-container">
            <input
              type="text"
              id="username"
              className="form-input"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => {
                setUsername(e.target.value);
                if (errors.username) setErrors({ ...errors, username: null });
              }}
              disabled={loading}
            />
            <svg className="form-input-icon" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
          {errors.username && <span className="error-msg">{errors.username}</span>}
        </div>

        {/* Password */}
        <div className="form-group">
          <label className="form-label" htmlFor="password">Password</label>
          <div className="input-container">
            <input
              type={showPassword ? 'text' : 'password'}
              id="password"
              className="form-input"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                if (errors.password) setErrors({ ...errors, password: null });
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
          {errors.password && <span className="error-msg">{errors.password}</span>}
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '1.5rem', marginTop: '-0.5rem' }}>
          <span className="card-link" style={{ fontSize: '0.85rem' }} onClick={() => onNavigate('forgot-password')}>
            Forgot Password?
          </span>
        </div>

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? <span className="spinner"></span> : 'Sign In'}
        </button>
      </form>

      <div className="card-footer">
        Don't have an account?{' '}
        <span className="card-link" onClick={() => onNavigate('register')}>
          Sign Up
        </span>
      </div>
    </div>
  );
};

export default Login;
