import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Dashboard from './components/Dashboard';
import SensorDetail from './components/SensorDetail';
import SensorHistory from './components/SensorHistory';
import LanguageSwitcher from './components/LanguageSwitcher';
import { useTranslation } from './i18n/useTranslation';
// import SystemMonitoring from './components/SystemMonitoring';
import './App.css';

function App() {
  const { t } = useTranslation();

  return (
    <Router>
      <div className="app">
        <nav className="navbar">
          <div className="nav-container">
            <Link to="/" className="nav-logo">
              🌡️ ThermoWatch
            </Link>
            <div className="nav-actions">
              <div className="nav-links">
                <Link to="/" className="nav-link">{t('nav.dashboard')}</Link>
                <Link to="/history" className="nav-link">{t('nav.history')}</Link>
                {/*<Link to="/monitoring" className="nav-link">Monitoring Systemu</Link>*/}
              </div>
              <LanguageSwitcher />
            </div>
          </div>
        </nav>

        <main className="main-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/sensor/:id" element={<SensorDetail />} />
            <Route path="/history" element={<SensorHistory />} />
            {/*<Route path="/monitoring" element={<SystemMonitoring />} />*/}
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;




