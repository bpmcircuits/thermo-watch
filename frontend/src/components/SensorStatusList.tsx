import { Sensor } from '../types';
import { formatDistanceToNow } from 'date-fns';
import pl from 'date-fns/locale/pl';
import './SensorStatusList.css';

interface SensorStatusListProps {
  sensors: Sensor[];
  onSensorClick: (sensor: Sensor) => void;
}

const SensorStatusList = ({ sensors, onSensorClick }: SensorStatusListProps) => {
  const getStatusBadge = (sensor: Sensor) => {
    if (sensor.isOnline) {
      return <span className="status-badge online">Online</span>;
    }
    const lastSeen = formatDistanceToNow(new Date(sensor.lastSeen), {
      addSuffix: true,
      locale: pl,
    });
    return <span className="status-badge offline">Offline ({lastSeen})</span>;
  };

  return (
    <div className="sensor-status-list">
      {sensors.length === 0 ? (
        <div className="no-sensors">Brak czujników</div>
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
                  <span className="info-label">Lokalizacja:</span>
                  <span className="info-value">{sensor.location}</span>
                </div>
                <div className="info-item">
                  <span className="info-label">Typ:</span>
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

