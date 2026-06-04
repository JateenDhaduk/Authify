import React, { useState, useEffect } from 'react';
import { ToastContainer } from './components/Toast';
import Login from './components/Login';
import Register from './components/Register';
import VerifyOtp from './components/VerifyOtp';
import ForgotPassword from './components/ForgotPassword';
import ResetPassword from './components/ResetPassword';
import Dashboard from './components/Dashboard';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

function App() {
  const [token, setToken] = useState(() => localStorage.getItem('authify_token') || '');
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('authify_user');
    try {
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });

  const [view, setView] = useState(() => {
    // If token exists, go straight to dashboard
    return localStorage.getItem('authify_token') ? 'dashboard' : 'login';
  });

  const [tempEmail, setTempEmail] = useState('');
  const [toasts, setToasts] = useState([]);

  // Toast handler
  const showToast = (type, title, message) => {
    const id = Date.now() + Math.random().toString(36).substr(2, 9);
    setToasts((prev) => [...prev, { id, type, title, message }]);
  };

  const removeToast = (id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  // API Call Wrapper
  const apiFetch = async (endpoint, options = {}) => {
    const url = `${API_BASE_URL}${endpoint}`;
    
    // Setup headers
    const headers = new Headers(options.headers || {});
    if (!(options.body instanceof FormData)) {
      headers.set('Content-Type', 'application/json');
    }
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }

    const config = {
      ...options,
      headers,
    };

    try {
      const response = await fetch(url, config);
      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        // Build error object from spring boot ErrorResponse
        const error = new Error(data.message || 'An error occurred');
        error.status = response.status;
        error.errors = data.errors || null; // validation errors map
        throw error;
      }

      return data;
    } catch (err) {
      // Re-throw fetch network failures or parsed HTTP errors
      if (!err.status) {
        err.message = 'Unable to connect to the authentication server. Please ensure the backend is running.';
      }
      throw err;
    }
  };

  // Auth actions
  const handleLoginSuccess = (accessToken, userData) => {
    setToken(accessToken);
    setUser(userData);
    localStorage.setItem('authify_token', accessToken);
    localStorage.setItem('authify_user', JSON.stringify(userData));
    setView('dashboard');
  };

  const handleLogout = () => {
    setToken('');
    setUser(null);
    localStorage.removeItem('authify_token');
    localStorage.removeItem('authify_user');
    setView('login');
  };

  // Router dispatcher
  const renderCurrentView = () => {
    switch (view) {
      case 'login':
        return (
          <Login
            apiFetch={apiFetch}
            onLoginSuccess={handleLoginSuccess}
            onNavigate={setView}
            showToast={showToast}
          />
        );
      case 'register':
        return (
          <Register
            apiFetch={apiFetch}
            onNavigate={setView}
            showToast={showToast}
            setTempEmail={setTempEmail}
          />
        );
      case 'verify-otp':
        return (
          <VerifyOtp
            apiFetch={apiFetch}
            tempEmail={tempEmail}
            onNavigate={setView}
            showToast={showToast}
          />
        );
      case 'forgot-password':
        return (
          <ForgotPassword
            apiFetch={apiFetch}
            onNavigate={setView}
            showToast={showToast}
            setTempEmail={setTempEmail}
          />
        );
      case 'reset-password':
        return (
          <ResetPassword
            apiFetch={apiFetch}
            tempEmail={tempEmail}
            onNavigate={setView}
            showToast={showToast}
          />
        );
      case 'dashboard':
        return (
          <Dashboard
            apiFetch={apiFetch}
            token={token}
            onLogout={handleLogout}
            showToast={showToast}
          />
        );
      default:
        return <Login apiFetch={apiFetch} onLoginSuccess={handleLoginSuccess} onNavigate={setView} showToast={showToast} />;
    }
  };

  return (
    <div className="app-container">
      {/* Toast Notification Container */}
      <ToastContainer toasts={toasts} removeToast={removeToast} />
      
      {/* Dynamic Screen View */}
      {renderCurrentView()}
    </div>
  );
}

export default App;
