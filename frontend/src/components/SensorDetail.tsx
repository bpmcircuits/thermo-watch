import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { format } from 'date-fns';
import { parseBackendTimestamp } from '../utils/dateUtils';
import { sensorApi } from '../services/api';
import { SensorDetail as SensorDetailType } from '../types';
import TemperatureChart from './TemperatureChart';
import { useLanguage } from '../i18n/LanguageContext';
import { useTranslation } from '../i18n/useTranslation';
import './SensorDetail.css';

const SensorDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [sensor, setSensor] = useState<SensorDetailType | null>(null);
  const [loading, setLoading] = useState(true);
  const { dateLocale } = useLanguage();
  const { t } = useTranslation();

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
        <p>{t('sensorDetail.loading')}</p>
      </div>
    );
  }

  if (!sensor) {
    return (
      <div className="sensor-detail-error">
        <p>{t('sensorDetail.notFound')}</p>
        <button onClick={() => navigate('/')} className="back-button">
          {t('sensorDetail.backButton')}
        </button>
      </div>
    );
  }

  const getStatusBadge = () => {
    if (sensor.isOnline) {
      return <span className="status-badge online">{t('sensorStatus.online')}</span>;
    }
    return <span className="status-badge offline">{t('sensorStatus.offline')}</span>;
  };

  return (
    <div className="sensor-detail">
      <button onClick={() => navigate('/')} className="back-button">
        ← {t('sensorDetail.backButton')}
      </button>

      <div className="sensor-header-section">
        <h1 className="sensor-detail-title">{t('sensorDetail.title')}</h1>
        {getStatusBadge()}
      </div>

      <div className="sensor-meta-section">
        <h2 className="section-title">{t('sensorDetail.infoTitle')}</h2>
        <div className="meta-grid">
          <div className="meta-item">
            <span className="meta-label">{t('sensorDetail.idLabel')}</span>
            <span className="meta-value">{sensor.sensorId}</span>
          </div>
          <div className="meta-item">
            <span className="meta-label">{t('sensorDetail.locationLabel')}</span>
            <span className="meta-value">{sensor.location}</span>
          </div>
          <div className="meta-item">
            <span className="meta-label">{t('sensorDetail.typeLabel')}</span>
            <span className="meta-value">{sensor.sensorType}</span>
          </div>
          <div className="meta-item">
            <span className="meta-label">{t('sensorDetail.lastUpdateLabel')}</span>
            <span className="meta-value">
              {format(parseBackendTimestamp(sensor.lastSeen), 'PPpp', { locale: dateLocale })}
            </span>
          </div>
        </div>
      </div>

      <div className="sensor-chart-section">
        <h2 className="section-title">{t('sensorDetail.chartTitle')}</h2>
        {sensor.measurements && sensor.measurements.length > 0 ? (
          <TemperatureChart measurements={sensor.measurements} />
        ) : (
          <div className="no-data">{t('sensorDetail.noMeasurements')}</div>
        )}
      </div>
    </div>
  );
};

export default SensorDetail;

