import { Sensor } from '../types';
import { formatDistanceToNow } from 'date-fns';
import { parseBackendTimestamp } from '../utils/dateUtils';
import { useLanguage } from '../i18n/LanguageContext';
import { useTranslation } from '../i18n/useTranslation';
import './SensorStatusList.css';

interface SensorStatusListProps {
  sensors: Sensor[];
  onSensorClick: (sensor: Sensor) => void;
}

const SensorStatusList = ({ sensors, onSensorClick }: SensorStatusListProps) => {
  const { dateLocale } = useLanguage();
  const { t } = useTranslation();

  const getStatusBadge = (sensor: Sensor) => {
    if (sensor.isOnline) {
      return <span className="status-badge online">{t('sensorStatus.online')}</span>;
    }
    const lastSeen = formatDistanceToNow(parseBackendTimestamp(sensor.lastSeen), {
      addSuffix: true,
      locale: dateLocale,
    });
    return (
      <span className="status-badge offline">
        {t('sensorStatus.offlineSince', { time: lastSeen })}
      </span>
    );
  };

  return (
    <div className="sensor-status-list">
      {sensors.length === 0 ? (
        <div className="no-sensors">{t('sensorStatus.noSensors')}</div>
      ) : (
        <div className="sensors-grid">
          {sensors.map((sensor) => (
            <div
              key={sensor.id}
              className="sensor-card"
              onClick={() => onSensorClick(sensor)}
            >
              <div className="sensor-header">
                <h4 className="sensor-id">{sensor.sensorId}</h4>
                {getStatusBadge(sensor)}
              </div>
              <div className="sensor-info">
                <div className="info-item">
                  <span className="info-label">{t('sensorStatus.locationLabel')}</span>
                  <span className="info-value">{sensor.location}</span>
                </div>
                <div className="info-item">
                  <span className="info-label">{t('sensorStatus.typeLabel')}</span>
                  <span className="info-value">{sensor.sensorType}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default SensorStatusList;

