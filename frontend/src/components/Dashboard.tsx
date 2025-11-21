import { useEffect, useState, useCallback, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { sensorApi } from '../services/api';
import { RoomData, Sensor, Measurement } from '../types';
import RoomTile from './RoomTile';
import TemperatureChart from './TemperatureChart';
import SensorStatusList from './SensorStatusList';
import './Dashboard.css';

const Dashboard = () => {
  const [rooms, setRooms] = useState<RoomData[]>([]);
  const [sensors, setSensors] = useState<Sensor[]>([]);
  const [selectedLocation, setSelectedLocation] = useState<string>('');
  const [locationMeasurements, setLocationMeasurements] = useState<Measurement[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const selectedLocationRef = useRef<string>('');
  
  // Aktualizuj ref przy każdej zmianie selectedLocation
  useEffect(() => {
    selectedLocationRef.current = selectedLocation;
  }, [selectedLocation]);

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 30000); // Odśwież co 30 sekund
    return () => clearInterval(interval);
  }, []);

  const loadLocationMeasurements = useCallback(async (location: string) => {
    if (!location) return;
    try {
      const measurements = await sensorApi.getLocationMeasurements(location, 24);
      setLocationMeasurements(measurements);
    } catch (error) {
      console.error('Błąd ładowania pomiarów:', error);
    }
  }, []);

  useEffect(() => {
    if (selectedLocation) {
      loadLocationMeasurements(selectedLocation);
    }
  }, [selectedLocation, loadLocationMeasurements]);

  const loadData = async () => {
    try {
      setError(null);
      const [roomsData, sensorsData] = await Promise.all([
        sensorApi.getRoomsData(),
        sensorApi.getAllSensors(),
      ]);
      setRooms(roomsData);
      setSensors(sensorsData);
      
      // Użyj ref do odczytania aktualnej wartości selectedLocation
      const currentLocation = selectedLocationRef.current;
      
      // Zachowaj wybraną lokalizację, ustaw pierwszą tylko jeśli nic nie jest wybrane
      if (!currentLocation && roomsData.length > 0) {
        setSelectedLocation(roomsData[0].location);
      } else if (currentLocation) {
        // Odśwież pomiary dla wybranej lokalizacji
        loadLocationMeasurements(currentLocation);
      }
      setLoading(false);
    } catch (error) {
      console.error('Błąd ładowania danych:', error);
      setError('Nie udało się załadować danych. Sprawdź konsolę przeglądarki.');
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-loading">
        <div className="spinner"></div>
        <p>Ładowanie danych...</p>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <h1 className="dashboard-title">Dashboard</h1>
      {error && (
        <div className="error-banner">
          ⚠️ {error}
        </div>
      )}

      <section className="rooms-section">
        <h2 className="section-title">Aktualna temperatura / wilgotność per pomieszczenie</h2>
        <div className="rooms-grid">
          {rooms.map((room) => (
            <RoomTile
              key={room.location}
              room={room}
              onClick={() => setSelectedLocation(room.location)}
              isSelected={selectedLocation === room.location}
            />
          ))}
        </div>
      </section>

      <section className="chart-section">
        <h2 className="section-title">
          Temperatura w lokalizacji: {selectedLocation || 'Wybierz lokalizację'} (ostatnie 24h)
        </h2>
        {selectedLocation && locationMeasurements.length > 0 ? (
          <TemperatureChart measurements={locationMeasurements} />
        ) : (
          <div className="no-data">Brak danych do wyświetlenia</div>
        )}
      </section>

      <section className="sensors-section">
        <h2 className="section-title">Status czujników</h2>
        <SensorStatusList
          sensors={sensors}
          onSensorClick={(sensor) => navigate(`/sensor/${sensor.id}`)}
        />
      </section>
    </div>
  );
};

export default Dashboard;

