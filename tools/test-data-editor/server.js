const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
app.use(cors());
app.use(express.json());

const PORT = 3001;

// Path to properties files relative to this script
const projectRoot = path.join(__dirname, '..', '..');
const configPaths = {
  'testdata': path.join(projectRoot, 'src', 'test', 'resources', 'testdata.properties'),
  'uat': path.join(projectRoot, 'src', 'test', 'resources', 'config', 'uat.properties'),
  'prod': path.join(projectRoot, 'src', 'test', 'resources', 'config', 'prod.properties')
};

// API to get available configs
app.get('/api/configs', (req, res) => {
  res.json(Object.keys(configPaths));
});

// API to read a specific config
app.get('/api/config/:name', (req, res) => {
  const name = req.params.name;
  if (!configPaths[name]) return res.status(404).json({ error: 'Config not found' });
  
  try {
    const content = fs.readFileSync(configPaths[name], 'utf-8');
    const properties = {};
    const lines = content.split('\n');
    
    lines.forEach(line => {
      const trimmed = line.trim();
      if (trimmed && !trimmed.startsWith('#')) {
        const eqIdx = trimmed.indexOf('=');
        if (eqIdx > 0) {
          const key = trimmed.substring(0, eqIdx).trim();
          const val = trimmed.substring(eqIdx + 1).trim();
          properties[key] = val;
        }
      }
    });
    
    res.json({ properties, raw: content });
  } catch (err) {
    res.status(500).json({ error: 'Failed to read file', details: err.message });
  }
});

// API to update a specific config
app.post('/api/config/:name', (req, res) => {
  const name = req.params.name;
  if (!configPaths[name]) return res.status(404).json({ error: 'Config not found' });
  
  const updates = req.body; // e.g. { "default.phone": "123456" }
  
  try {
    let content = fs.readFileSync(configPaths[name], 'utf-8');
    const lines = content.split(/\r?\n/);
    
    for (let i = 0; i < lines.length; i++) {
      const trimmed = lines[i].trim();
      if (trimmed && !trimmed.startsWith('#')) {
        const eqIdx = trimmed.indexOf('=');
        if (eqIdx > 0) {
          const key = trimmed.substring(0, eqIdx).trim();
          if (updates[key] !== undefined) {
            // Replace only the value part to keep spacing intact if possible
            lines[i] = `${key}=${updates[key]}`;
            delete updates[key]; // Mark as updated
          }
        }
      }
    }
    
    // Add any new keys that weren't in the file at the end
    for (const [key, value] of Object.entries(updates)) {
      lines.push(`${key}=${value}`);
    }
    
    fs.writeFileSync(configPaths[name], lines.join('\n'), 'utf-8');
    res.json({ success: true, message: 'Configuration saved successfully!' });
    
  } catch (err) {
    res.status(500).json({ error: 'Failed to write file', details: err.message });
  }
});

app.listen(PORT, () => {
  console.log(`Test Data Editor API running on http://localhost:${PORT}`);
});
