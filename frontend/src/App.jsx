import React, { useState } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Container,
  Box,
  Tabs,
  Tab,
  ThemeProvider,
  createTheme,
  CssBaseline,
} from '@mui/material';
import { Dashboard as DashboardIcon, PlayArrow, RateReview } from '@mui/icons-material';
import Dashboard from './pages/Dashboard';
import ScrapeForm from './pages/ScrapeForm';
import StagingReview from './pages/StagingReview';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  const [currentTab, setCurrentTab] = useState(0);

  const handleTabChange = (event, newValue) => {
    setCurrentTab(newValue);
  };

  const handleNavigate = (page) => {
    const tabMap = {
      dashboard: 0,
      scrape: 1,
      review: 2,
    };
    setCurrentTab(tabMap[page] || 0);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
              Product Scraper Admin
            </Typography>
          </Toolbar>
        </AppBar>

        <Container maxWidth="xl" sx={{ mt: 4, mb: 4, flexGrow: 1 }}>
          <Tabs
            value={currentTab}
            onChange={handleTabChange}
            sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}
          >
            <Tab icon={<DashboardIcon />} label="Dashboard" />
            <Tab icon={<PlayArrow />} label="Start Scrape" />
            <Tab icon={<RateReview />} label="Review Products" />
          </Tabs>

          <Box>
            {currentTab === 0 && <Dashboard onNavigate={handleNavigate} />}
            {currentTab === 1 && <ScrapeForm />}
            {currentTab === 2 && <StagingReview />}
          </Box>
        </Container>

        <Box component="footer" sx={{ py: 3, px: 2, mt: 'auto', backgroundColor: '#f5f5f5' }}>
          <Container maxWidth="xl">
            <Typography variant="body2" color="text.secondary" align="center">
              Product Recommender AI - Scraper Admin UI
            </Typography>
          </Container>
        </Box>
      </Box>
    </ThemeProvider>
  );
}

export default App;
