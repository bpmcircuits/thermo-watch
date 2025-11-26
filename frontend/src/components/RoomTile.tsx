import { RoomData } from '../types';
import { useTranslation } from '../i18n/useTranslation';
import './RoomTile.css';

interface RoomTileProps {
  room: RoomData;
  onClick: () => void;
  isSelected: boolean;
}

const RoomTile = ({ room, onClick, isSelected }: RoomTileProps) => {
  const { t } = useTranslation();

  return (
    <div
      className={`room-tile ${isSelected ? 'selected' : ''}`}
      onClick={onClick}
    >
      <div className="room-header">
        <h3 className="room-name">{room.location}</h3>
        <span className="sensor-count">
          {
            room.sensorCount > 1 ? t('roomTile.sensorCountMultiple', { count: room.sensorCount }) 
              : t('roomTile.sensorCountOne', { count: room.sensorCount })
          }
        </span>
      </div>
      <div className="room-metrics">
        <div className="metric">
          <span className="metric-label">{t('roomTile.temperature')}</span>
          <span className="metric-value temperature">
            {room.currentTemperature !== null 
              ? `${room.currentTemperature.toFixed(1)}°C`
              : t('roomTile.noData')}
          </span>
        </div>
        <div className="metric">
          <span className="metric-label">{t('roomTile.humidity')}</span>
          <span className="metric-value humidity">
            {room.currentHumidity !== null 
              ? `${room.currentHumidity.toFixed(1)}%`
              : t('roomTile.noData')}
          </span>
        </div>
      </div>
    </div>
  );
};

export default RoomTile;





