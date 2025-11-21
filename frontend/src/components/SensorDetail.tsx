import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { sensorApi } from '../services/api';
import { SensorDetail as SensorDetailType } from '../types';
import TemperatureChart from './TemperatureChart';
import { format } from 'date-fns';
import pl from 'date-fns/locale/pl';
import './SensorDetail.css';

const SensorDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [sensor, setSensor] = useState<SensorDetailType | null>(null);
  const [loading, setLoading] = useState(true);

  const loadSensorData = async () => {
    if (!id) return;
    try {
      const sensorData = await sensorApi.getSensorById(id);
      const measurements = await sensorApi.getSensorMeasurements(id, 24);
      setSensor({ ...sensorData, measurements });
      setLoading(false);
    } catch (error) {
      console.error('Błąd ładowania danych czujnika:', error);
      setLoading(false);
    }
  };

  useEffect(() => {
    if (id) {
      loadSensorData();
      const interval = setInterval(loadSensorData, 30000);
      return () => clearInterval(interval);
    }
  }, [id]);

  if (loading) {
    return (
      <div className="sensor-detail-loading">
        <div className="spinner"></div>
        <p>Ładowanie danych czujnika...</p>
      </div>
    );
  }

  if (!sensor) {
    return (
      <div className="sensor-detail-error">
        <p>Czujnik nie został znaleziony</p>
        <button onClick={() => navigate('/')} className="back-button">
          Powrót do Dashboard
        </button>
      </div>
    );
  }

  const getStatusBadge = () => {
    if (sensor.isOnline) {
      return <span className="status-badge online">Online</span>;
    }
    return <span className="status-badge offline">Offline</span>;
  };

  return (
    <div className="sensor-detail">
      <button onClick={() => navigate('/')} className="back-button">
        ← Powrót do Dashboard
      </button>

      <div className="sensor-header-section">
        <h1 className="sensor-detail-title">Szczegóły czujnika</h1>
        {getStatusBadge()}
      </div>

      <div className="sensor-meta-section">
        <h2 className="section-title">Informacje o czujniku</h2>
        <div className="meta-grid">
          <div className="meta-item">
            <span className="meta-label">ID Czujnika:</span>
            <span className="meta-value">{sensor.sensorId}</span>
          </div>
          <div className="meta-item">
            <span className="meta-label">Lokalizacja:</span>
            <span className="meta-value">{sensor.location}</span>
          </div>
          <div className="meta-item">
            <span className="meta-label">Typ sensora:</span>
            <span className="meta-value">{sensor.sensorType}</span>
          </div>
          <div className="meta-item">
            <span className="meta-label">Ostatnia aktualizacja:</span>
            <span className="meta-value">
              {format(new Date(sensor.lastSeen), 'PPpp', { locale: pl })}
            </span>
          </div>
        </div>
      </div>

      <div className="sensor-chart-section">
        <h2 className="section-title">Temperatura i wilgotność (ostatnie 24h)</h2>
        {sensor.measurements && sensor.measurements.length > 0 ? (
          <TemperatureChart measurements={sensor.measurements} />
        ) : (
          <div className="no-data">Brak danych pomiarowych</div>
        )}
      </div>
    </div>
  );
};

export default SensorDetail;

