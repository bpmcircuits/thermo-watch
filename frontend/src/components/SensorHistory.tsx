import { ChangeEvent, useEffect, useMemo, useState } from 'react';
import { format } from 'date-fns';
import { parseBackendTimestamp } from '../utils/dateUtils';
import { sensorApi } from '../services/api';
import { Measurement, Sensor } from '../types';
import TemperatureChart from './TemperatureChart';
import { useLanguage } from '../i18n/LanguageContext';
import { useTranslation } from '../i18n/useTranslation';
import './SensorHistory.css';

type HistoryRangeKey =
  | 'history.range.6h'
  | 'history.range.12h'
  | 'history.range.24h'
  | 'history.range.3d'
  | 'history.range.7d'
  | 'history.range.30d';

const RANGE_OPTIONS: { labelKey: HistoryRangeKey; value: number }[] = [
  { labelKey: 'history.range.6h', value: 6 },
  { labelKey: 'history.range.12h', value: 12 },
  { labelKey: 'history.range.24h', value: 24 },
  { labelKey: 'history.range.3d', value: 72 },
  { labelKey: 'history.range.7d', value: 168 },
  { labelKey: 'history.range.30d', value: 720 },
];

type HistoryError = 'sensors' | 'history' | null;

const SensorHistory = () => {
  const [sensors, setSensors] = useState<Sensor[]>([]);
  const [selectedSensorId, setSelectedSensorId] = useState<string>('');
  const [measurements, setMeasurements] = useState<Measurement[]>([]);
  const [hoursRange, setHoursRange] = useState<number>(168);
  const [loadingSensors, setLoadingSensors] = useState(true);
  const [loadingMeasurements, setLoadingMeasurements] = useState(false);
  const [error, setError] = useState<HistoryError>(null);
  const { dateLocale } = useLanguage();
  const { t } = useTranslation();

  useEffect(() => {
    const loadSensors = async () => {
      try {
        setLoadingSensors(true);
        const sensorsData = await sensorApi.getAllSensors();
        setSensors(sensorsData);
        if (sensorsData.length > 0) {
          setSelectedSensorId(sensorsData[0].id);
        } else {
          setSelectedSensorId('');
          setMeasurements([]);
        }
        setError(null);
      } catch (err) {
        console.error('Błąd ładowania listy czujników:', err);
        setError('sensors');
      } finally {
        setLoadingSensors(false);
      }
    };

    loadSensors();
  }, []);

  useEffect(() => {
    if (!selectedSensorId) {
      setMeasurements([]);
      return;
    }

    const loadMeasurements = async () => {
      try {
        setLoadingMeasurements(true);
        setError(null);
        const data = await sensorApi.getSensorMeasurements(selectedSensorId, hoursRange);
        setMeasurements(data);
      } catch (err) {
        console.error('Błąd ładowania danych historycznych:', err);
        setError('history');
      } finally {
        setLoadingMeasurements(false);
      }
    };

    loadMeasurements();
  }, [selectedSensorId, hoursRange]);

  const selectedSensor = useMemo(
    () => sensors.find((sensor) => String(sensor.id) === String(selectedSensorId)) || null,
    [sensors, selectedSensorId]
  );

  const sortedMeasurements = useMemo(
    () =>
      [...measurements].sort(
        (a, b) => parseBackendTimestamp(b.timestamp).getTime() - parseBackendTimestamp(a.timestamp).getTime()
      ),
    [measurements]
  );

  const latestMeasurements = useMemo(() => sortedMeasurements.slice(0, 100), [sortedMeasurements]);

  const handleSensorChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setSelectedSensorId(event.target.value);
  };

  return (
    <div className="sensor-history">
      <header className="history-header">
        <div>
          <h1>{t('sensorHistory.title')}</h1>
          <p>{t('sensorHistory.subtitle')}</p>
        </div>
      </header>

      <section className="history-controls">
        <div className="control-group">
          <label htmlFor="sensor-select">{t('sensorHistory.selectSensor')}</label>
          <select
            id="sensor-select"
            className="history-select"
            value={selectedSensorId}
            onChange={handleSensorChange}
            disabled={loadingSensors}
          >
            {sensors.map((sensor) => (
              <option key={sensor.id} value={sensor.id}>
                {sensor.sensorId} — {sensor.location}
              </option>
            ))}
          </select>
        </div>

        <div className="control-group">
          <label htmlFor="range-select">{t('sensorHistory.timeRange')}</label>
          <select
            id="range-select"
            className="history-select"
            value={hoursRange}
            onChange={(event) => setHoursRange(Number(event.target.value))}
          >
            {RANGE_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {t(option.labelKey)}
              </option>
            ))}
          </select>
        </div>

        {selectedSensor ? (
          <div className="control-group meta">
            <span className="meta-label">{t('sensorHistory.lastSeen')}</span>
            <span className="meta-value">
              {format(parseBackendTimestamp(selectedSensor.lastSeen), 'PPpp', { locale: dateLocale })}
            </span>
          </div>
        ) : null}
      </section>

      {error && (
        <div className="error-banner">
          {t(
            error === 'sensors'
              ? 'sensorHistory.errorSensors'
              : 'sensorHistory.errorHistory'
          )}
        </div>
      )}

      <section className="history-content">
        <div className="history-chart-card">
          <h2>{t('sensorHistory.chartTitle')}</h2>
          {loadingMeasurements ? (
            <div className="history-loading">
              <div className="spinner"></div>
              <p>{t('sensorHistory.loading')}</p>
            </div>
          ) : measurements.length > 0 ? (
            <TemperatureChart measurements={measurements} />
          ) : (
            <div className="no-data">{t('sensorHistory.noData')}</div>
          )}
        </div>

        <div className="history-table-card">
          <h2>{t('sensorHistory.tableTitle')}</h2>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>{t('sensorHistory.tableTime')}</th>
                  <th>{t('sensorHistory.tableTemp')}</th>
                  <th>{t('sensorHistory.tableHumidity')}</th>
                </tr>
              </thead>
              <tbody>
                {latestMeasurements.length === 0 ? (
                  <tr>
                    <td colSpan={3} className="no-data">
                      {t('sensorHistory.tableEmpty')}
                    </td>
                  </tr>
                ) : (
                  latestMeasurements.map((measurement) => (
                    <tr key={measurement.id}>
                      <td>
                        {format(parseBackendTimestamp(measurement.timestamp), 'PPpp', { locale: dateLocale })}
                      </td>
                      <td>
                        {measurement.temperature !== null && measurement.temperature !== undefined
                          ? measurement.temperature.toFixed(1)
                          : 'N/A'}
                      </td>
                      <td>
                        {measurement.humidity !== null && measurement.humidity !== undefined
                          ? measurement.humidity.toFixed(1)
                          : 'N/A'}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </div>
  );
};

export default SensorHistory;

