import React, { useEffect, useState } from 'react';

const Dashboard = ({ apiFetch, token, onLogout, showToast }) => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await apiFetch('/api/auth/profile', {
          method: 'GET',
        });
        if (response.success && response.data) {
          setProfile(response.data);
        } else {
          showToast('error', 'Profile Error', 'Failed to retrieve user profile data.');
          onLogout();
        }
      } catch (err) {
        showToast('error', 'Session Expired', 'Your session has expired. Please log in again.');
        onLogout();
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [apiFetch, onLogout, showToast]);

  if (loading) {
    return (
      <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1rem', padding: '4rem 2rem' }}>
        <span className="spinner" style={{ width: '2.5rem', height: '2.5rem', borderWidth: '3px' }}></span>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>Loading secure profile...</p>
      </div>
    );
  }

  if (!profile) return null;

  // Mock Audit Logs for premium aesthetic
  const mockLogs = [
    { id: 1, action: 'User Sign In', timestamp: 'Just now', status: 'Success', ip: '192.168.1.42' },
    { id: 2, action: 'Token Refreshed', timestamp: '5 mins ago', status: 'Success', ip: '192.168.1.42' },
    { id: 3, action: 'Account Verification', timestamp: '1 hour ago', status: 'Success', ip: '192.168.1.42' },
  ];

  return (
    <div className="glass-card dashboard-card">
      <div className="dashboard-header">
        <div className="user-badge">
          <div className="avatar">
            {profile.username ? profile.username.substring(0, 2).toUpperCase() : 'US'}
          </div>
          <div className="user-meta">
            <h3 style={{ fontSize: '1.25rem', fontFamily: 'var(--font-display)', fontWeight: 600 }}>
              {profile.username}
            </h3>
            <span className="user-role">
              {profile.role || 'USER'}
            </span>
          </div>
        </div>

        <button className="btn-secondary" onClick={() => {
          showToast('info', 'Logged Out', 'Successfully logged out.');
          onLogout();
        }}>
          <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          Sign Out
        </button>
      </div>

      <h2 className="title-gradient" style={{ textAlign: 'left', fontSize: '1.75rem', marginBottom: '1.5rem' }}>
        Secure Dashboard
      </h2>

      <div className="dashboard-grid">
        <div className="info-panel">
          <div className="info-panel-title">Email Address</div>
          <div className="info-panel-value">{profile.email}</div>
        </div>

        <div className="info-panel">
          <div className="info-panel-title">Phone Number</div>
          <div className="info-panel-value">{profile.phone || 'N/A'}</div>
        </div>

        <div className="info-panel">
          <div className="info-panel-title">Verification Status</div>
          <div className="info-panel-value" style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--success)' }}>
            <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5">
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
            </svg>
            Verified Active
          </div>
        </div>
      </div>

      <div className="stats-container">
        <h4 className="stats-header">Security Audit Logs</h4>
        <div style={{ overflowX: 'auto' }}>
          <table className="mock-table">
            <thead>
              <tr>
                <th>Action</th>
                <th>Timestamp</th>
                <th>IP Address</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {mockLogs.map((log) => (
                <tr key={log.id}>
                  <td style={{ fontWeight: 600 }}>{log.action}</td>
                  <td>{log.timestamp}</td>
                  <td style={{ fontFamily: 'monospace' }}>{log.ip}</td>
                  <td>
                    <span className="badge-status success">{log.status}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
