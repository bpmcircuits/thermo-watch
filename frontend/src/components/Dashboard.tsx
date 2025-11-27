import { useEffect, useState, useCallback, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { sensorApi } from '../services/api';
import { RoomData, Sensor, Measurement } from '../types';
import RoomTile from './RoomTile';
import TemperatureChart from './TemperatureChart';
import SensorStatusList from './SensorStatusList';
import { useTranslation } from '../i18n/useTranslation';
import { parseBackendTimestamp } from '../utils/dateUtils';
import './Dashboard.css';

const ONE_HOUR_MS = 60 * 60 * 1000;

interface SensorMeasurements {
  sensor: Sensor;
  measurements: Measurement[];
}

const Dashboard = () => {
  const [rooms, setRooms] = useState<RoomData[]>([]);
  const [sensors, setSensors] = useState<Sensor[]>([]);
  const [selectedLocation, setSelectedLocation] = useState<string>('');
  const [sensorMeasurements, setSensorMeasurements] = useState<SensorMeasurements[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingCharts, setLoadingCharts] = useState(false);
  const [hasError, setHasError] = useState(false);
  const navigate = useNavigate();
  const selectedLocationRef = useRef<string>('');
  const { t } = useTranslation();
  
  // Aktualizuj ref przy każdej zmianie selectedLocation
  useEffect(() => {
    selectedLocationRef.current = selectedLocation;
  }, [selectedLocation]);

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 30000); // Odśwież co 30 sekund
    return () => clearInterval(interval);
  }, []);

  const loadSensorMeasurements = useCallback(async (location: string) => {
    if (!location) {
      setSensorMeasurements([]);
      return;
    }
    try {
      setLoadingCharts(true);
      // Pobierz wszystkie czujniki dla wybranej lokalizacji
      const locationSensors = sensors.filter((sensor) => sensor.location === location);
      
      // Dla każdego czujnika pobierz jego pomiary
      const measurementsPromises = locationSensors.map(async (sensor) => {
        try {
          const measurements = await sensorApi.getSensorMeasurements(sensor.id, 24);
          return { sensor, measurements };
        } catch (error) {
          console.error(`Błąd ładowania pomiarów dla czujnika ${sensor.id}:`, error);
          return { sensor, measurements: [] };
        }
      });
      
      const results = await Promise.all(measurementsPromises);
      setSensorMeasurements(results);
    } catch (error) {
      console.error('Błąd ładowania pomiarów:', error);
      setSensorMeasurements([]);
    } finally {
      setLoadingCharts(false);
    }
  }, [sensors]);

  useEffect(() => {
    if (selectedLocation) {
      loadSensorMeasurements(selectedLocation);
    } else {
      setSensorMeasurements([]);
    }
  }, [selectedLocation, loadSensorMeasurements]);

  const loadData = async () => {
    try {
      setHasError(false);
      const [roomsData, sensorsData] = await Promise.all([
        sensorApi.getRoomsData(),
        sensorApi.getAllSensors(),
      ]);
      const now = Date.now();
      const activeSensors = sensorsData.filter(
        (sensor) => now - parseBackendTimestamp(sensor.lastSeen).getTime() <= ONE_HOUR_MS
      );
      setSensors(activeSensors);

      const activeLocations = new Set(activeSensors.map((sensor) => sensor.location));
      const activeRooms = roomsData.filter((room) => activeLocations.has(room.location));
      setRooms(activeRooms);
      
      // Użyj ref do odczytania aktualnej wartości selectedLocation
      const currentLocation = selectedLocationRef.current;

      const locationStillActive =
        currentLocation && activeRooms.some((room) => room.location === currentLocation);
      const nextLocation = locationStillActive ? currentLocation : activeRooms[0]?.location || '';

      if (nextLocation !== currentLocation) {
        setSelectedLocation(nextLocation);
      }

      if (nextLocation) {
        // loadSensorMeasurements zostanie wywołane przez useEffect
      } else {
        setSensorMeasurements([]);
      }
      setLoading(false);
    } catch (error) {
      console.error('Błąd ładowania danych:', error);
      setHasError(true);
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-loading">
        <div className="spinner"></div>
        <p>{t('loading.generic')}</p>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <h1 className="dashboard-title">{t('dashboard.title')}</h1>
      {hasError && (
        <div className="error-banner">
          ⚠️ {t('dashboard.errorLoadData')}
        </div>
      )}

      <section className="rooms-section">
        <h2 className="section-title">{t('dashboard.roomsTitle')}</h2>
        {rooms.length === 0 ? (
          <div className="no-data">{t('dashboard.noActiveRooms')}</div>
        ) : (
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
        )}
      </section>

      <section className="chart-section">
        <h2 className="section-title">
          {selectedLocation
            ? t('dashboard.chartTitle', { location: selectedLocation })
            : t('dashboard.chartPlaceholder')}
        </h2>
        {loadingCharts ? (
          <div className="dashboard-loading">
            <div className="spinner"></div>
            <p>{t('loading.generic')}</p>
          </div>
        ) : selectedLocation && sensorMeasurements.length > 0 ? (
          <div className="charts-container">
            {sensorMeasurements.map(({ sensor, measurements }) => (
              <div key={sensor.id} className="sensor-chart-card">
                <h3 className="sensor-chart-title">
                  {sensor.sensorId} — {sensor.location}
                </h3>
                {measurements.length > 0 ? (
                  <TemperatureChart measurements={measurements} />
                ) : (
                  <div className="no-data">{t('dashboard.noRecentReadings')}</div>
                )}
              </div>
            ))}
          </div>
        ) : (
          <div className="no-data">
            {selectedLocation
              ? t('dashboard.noRecentReadings')
              : t('dashboard.noActiveLocations')}
          </div>
        )}
      </section>

      <section className="sensors-section">
        <h2 className="section-title">{t('dashboard.sensorsTitle')}</h2>
        <SensorStatusList
          sensors={sensors}
          onSensorClick={(sensor) => navigate(`/sensor/${sensor.id}`)}
        />
      </section>
    </div>
  );
};

export default Dashboard;

