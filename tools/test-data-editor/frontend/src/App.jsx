import { useState, useEffect } from 'react';

function App() {
  const [configName, setConfigName] = useState('testdata');
  const [properties, setProperties] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [notification, setNotification] = useState(null);

  // Group definitions for the UI
  const groups = {
    'Customer Data': ['default.phone', 'default.otp', 'default.email', 'default.iban'],
    'Vehicle Data': ['default.sequence.number', 'default.custom.card', 'default.seller.id', 'default.car.year'],
    'Payment Card Data': ['default.card.number', 'default.card.expiry', 'default.card.cvv', 'default.card.holder'],
    'ID Generator': ['national.id.prefix', 'sequence.number.prefix'],
    'Timeouts (Seconds)': ['timeout.element.visible', 'timeout.payment.processing', 'timeout.policy.confirmation']
  };

  useEffect(() => {
    fetchConfig();
  }, [configName]);

  const fetchConfig = async () => {
    setLoading(true);
    try {
      const res = await fetch(`http://localhost:3001/api/config/${configName}`);
      const data = await res.json();
      setProperties(data.properties || {});
    } catch (err) {
      showNotification('Failed to load configuration. Is the server running?', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (key, value) => {
    setProperties(prev => ({ ...prev, [key]: value }));
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const res = await fetch(`http://localhost:3001/api/config/${configName}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(properties)
      });
      if (res.ok) {
        showNotification('Configuration saved successfully!', 'success');
      } else {
        throw new Error('Server returned error');
      }
    } catch (err) {
      showNotification('Failed to save configuration.', 'error');
    } finally {
      setSaving(false);
    }
  };

  const showNotification = (msg, type) => {
    setNotification({ msg, type });
    setTimeout(() => setNotification(null), 3000);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
      </div>
    );
  }

  // Get keys that don't belong to any predefined group
  const allGroupedKeys = Object.values(groups).flat();
  const otherKeys = Object.keys(properties).filter(k => !allGroupedKeys.includes(k));

  return (
    <div className="min-h-screen py-10 px-4 sm:px-6 lg:px-8 max-w-5xl mx-auto fade-in">
      <header className="mb-10 text-center relative z-10">
        <h1 className="text-5xl font-extrabold tracking-tighter text-treeNeon mb-2 lowercase" style={{ letterSpacing: '-0.05em' }}>
          tree<span className="text-treeWhite font-medium tracking-normal text-2xl ml-4">editor</span>
        </h1>
        <p className="text-treeMint/70 font-medium">Easily update test configuration properties for the automation framework.</p>
      </header>

      {/* Notification Toast */}
      {notification && (
        <div className={`fixed top-5 right-5 px-6 py-3 rounded-lg shadow-lg fade-in z-50 ${
          notification.type === 'success' ? 'bg-treeNeon text-treeDark' : 'bg-red-500 text-white'
        } backdrop-blur-sm font-bold border border-white/20`}>
          {notification.msg}
        </div>
      )}

      <div className="glass-card p-6 md:p-8 relative z-10">
        <div className="flex justify-between items-center mb-8 pb-4 border-b border-treeNeon/10">
          <div className="flex items-center space-x-4">
            <span className="text-treeMint font-semibold">Target Config:</span>
            <select 
              value={configName}
              onChange={(e) => setConfigName(e.target.value)}
              className="bg-[#0a1f1b]/80 border border-treeNeon/30 rounded-lg px-4 py-2 text-treeWhite font-medium focus:outline-none focus:ring-2 focus:ring-treeNeon/50"
            >
              <option value="testdata">testdata.properties</option>
              <option value="uat">uat.properties</option>
              <option value="prod">prod.properties</option>
            </select>
          </div>
          
          <button 
            onClick={handleSave} 
            disabled={saving}
            className="btn-primary flex items-center space-x-2"
          >
            {saving ? (
              <span className="inline-block animate-spin h-5 w-5 border-2 border-white border-t-transparent rounded-full mr-2"></span>
            ) : (
              <svg className="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4" /></svg>
            )}
            <span>{saving ? 'Saving...' : 'Save Changes'}</span>
          </button>
        </div>

        <div className="space-y-8">
          {Object.entries(groups).map(([groupName, keys]) => {
            // Only show group if we actually have these keys
            const hasKeys = keys.some(k => properties[k] !== undefined);
            if (!hasKeys) return null;
            
            return (
              <div key={groupName} className="fade-in">
                <h3 className="text-xl font-semibold text-treeNeon mb-4 flex items-center">
                  <span className="w-1.5 h-6 bg-treeMint rounded-full mr-3 inline-block"></span>
                  {groupName}
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-4">
                  {keys.map(key => {
                    if (properties[key] === undefined) return null;
                    return (
                      <div key={key} className="flex flex-col space-y-1.5">
                        <label className="text-sm font-medium text-slate-400 font-mono tracking-wide">
                          {key}
                        </label>
                        <input
                          type="text"
                          value={properties[key]}
                          onChange={(e) => handleChange(key, e.target.value)}
                          className="glass-input"
                        />
                      </div>
                    )
                  })}
                </div>
              </div>
            );
          })}

          {/* Other unspecified keys */}
          {otherKeys.length > 0 && (
            <div className="mt-8 pt-8 border-t border-slate-700/50 fade-in">
              <h3 className="text-xl font-semibold text-slate-300 mb-4 flex items-center">
                <span className="w-1.5 h-6 bg-slate-500 rounded-full mr-3 inline-block"></span>
                Other Properties
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-4">
                {otherKeys.map(key => (
                  <div key={key} className="flex flex-col space-y-1.5">
                    <label className="text-sm font-medium text-slate-400 font-mono tracking-wide truncate" title={key}>
                      {key}
                    </label>
                    <input
                      type="text"
                      value={properties[key]}
                      onChange={(e) => handleChange(key, e.target.value)}
                      className="glass-input"
                    />
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
      
      <footer className="mt-12 text-center text-slate-500 text-sm">
        <p>Changes are saved directly to the /src/test/resources/ properties files.</p>
      </footer>
    </div>
  );
}

export default App;
