import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Dashboard from './components/Dashboard';
import SensorDetail from './components/SensorDetail';
// import SystemMonitoring from './components/SystemMonitoring';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app">
        <nav className="navbar">
          <div className="nav-container">
            <Link to="/" className="nav-logo">
              🌡️ ThermoWatch
            </Link>
            <div className="nav-links">
              <Link to="/" className="nav-link">Dashboard</Link>
              {/*<Link to="/monitoring" className="nav-link">Monitoring Systemu</Link>*/}
            </div>
          </div>
        </nav>

        <main className="main-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/sensor/:id" element={<SensorDetail />} />
            {/*<Route path="/monitoring" element={<SystemMonitoring />} />*/}
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;




