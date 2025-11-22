import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  CircularProgress,
  Alert,
  LinearProgress,
} from '@mui/material';
import { PlayArrow, Refresh } from '@mui/icons-material';
import { scraperApi } from '../services/api';

const ScrapeForm = () => {
  const [sources, setSources] = useState([]);
  const [selectedSource, setSelectedSource] = useState('');
  const [loading, setLoading] = useState(false);
  const [scraping, setScraping] = useState(false);
  const [message, setMessage] = useState(null);
  const [jobId, setJobId] = useState(null);

  useEffect(() => {
    loadSources();
  }, []);

  const loadSources = async () => {
    setLoading(true);
    try {
      const response = await scraperApi.getSources();
      setSources(response.data);
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to load sources' });
    } finally {
      setLoading(false);
    }
  };

  const handleTriggerScrape = async () => {
    if (!selectedSource) {
      setMessage({ type: 'warning', text: 'Please select a website' });
      return;
    }

    setScraping(true);
    setMessage(null);
    setJobId(null);

    try {
      const response = await scraperApi.triggerScrape(selectedSource);
      setJobId(response.data.jobId);
      setMessage({
        type: 'success',
        text: `Scraping started! Job ID: ${response.data.jobId}`,
      });
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Failed to start scraping',
      });
    } finally {
      setScraping(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Start New Scrape
      </Typography>

      <Card>
        <CardContent>
          <Box component="form" sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
            <FormControl fullWidth>
              <InputLabel>Select Website</InputLabel>
              <Select
                value={selectedSource}
                onChange={(e) => setSelectedSource(e.target.value)}
                label="Select Website"
                disabled={loading || scraping}
              >
                {sources.map((source) => (
                  <MenuItem key={source.website_id} value={source.website_id}>
                    {source.website_name} ({source.base_url})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            {message && (
              <Alert severity={message.type} onClose={() => setMessage(null)}>
                {message.text}
              </Alert>
            )}

            {scraping && (
              <Box>
                <Typography variant="body2" color="textSecondary" gutterBottom>
                  Scraping in progress...
                </Typography>
                <LinearProgress />
              </Box>
            )}

            <Box display="flex" gap={2}>
              <Button
                variant="contained"
                startIcon={scraping ? <CircularProgress size={20} /> : <PlayArrow />}
                onClick={handleTriggerScrape}
                disabled={scraping || !selectedSource}
                fullWidth
              >
                {scraping ? 'Scraping...' : 'Start Scraping'}
              </Button>

              <Button
                variant="outlined"
                startIcon={<Refresh />}
                onClick={loadSources}
                disabled={loading || scraping}
              >
                Refresh Sources
              </Button>
            </Box>

            {jobId && (
              <Alert severity="info">
                <Typography variant="body2">
                  <strong>Job ID:</strong> {jobId}
                  <br />
                  <small>Products will appear in the review tab when scraping completes.</small>
                </Typography>
              </Alert>
            )}
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default ScrapeForm;
