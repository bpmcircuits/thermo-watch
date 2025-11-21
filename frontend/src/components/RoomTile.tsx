import { RoomData } from '../types';
import './RoomTile.css';

interface RoomTileProps {
  room: RoomData;
  onClick: () => void;
  isSelected: boolean;
}

const RoomTile = ({ room, onClick, isSelected }: RoomTileProps) => {
  return (
    <div
      className={`room-tile ${isSelected ? 'selected' : ''}`}
      onClick={onClick}
    >
      <div className="room-header">
        <h3 className="room-name">{room.location}</h3>
        <span className="sensor-count">{room.sensorCount} czujników</span>
      </div>
      <div className="room-metrics">
        <div className="metric">
          <span className="metric-label">Temperatura</span>
          <span className="metric-value temperature">
            {room.currentTemperature !== null 
              ? `${room.currentTemperature.toFixed(1)}°C`
              : 'Brak danych'}
          </span>
        </div>
        <div className="metric">
          <span className="metric-label">Wilgotność</span>
          <span className="metric-value humidity">
            {room.currentHumidity !== null 
              ? `${room.currentHumidity.toFixed(1)}%`
              : 'Brak danych'}
          </span>
        </div>
      </div>
    </div>
  );
};

export default RoomTile;





