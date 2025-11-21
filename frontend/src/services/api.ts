import axios from 'axios';
import { Sensor, Measurement, RoomData, SensorDetail } from '../types';
import { mockApi } from './mockData';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api/v1';
const FORCE_MOCK = import.meta.env.VITE_USE_MOCK === 'true';
const HAS_API_URL = !!import.meta.env.VITE_API_URL;
const ENABLE_FALLBACK = import.meta.env.VITE_ENABLE_FALLBACK === 'true'; // Opcjonalny fallback na mock

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 5000, // 5 sekund timeout
});

// Helper do obsługi błędów z opcjonalnym fallbackiem na mock
const withFallback = async <T>(
  apiCall: () => Promise<T>,
  fallback: () => Promise<T>
): Promise<T> => {
  try {
    return await apiCall();
  } catch (error) {
    if (ENABLE_FALLBACK) {
      console.warn('Błąd API, używam mocka jako fallback:', error);
      return await fallback();
    } else {
      console.error('Błąd API:', error);
      throw error; // Rzucaj błąd dalej, żeby komponenty mogły go obsłużyć
    }
  }
};

const realApi = {
  // Pobierz wszystkie czujniki
  getAllSensors: async (): Promise<Sensor[]> => {
    const response = await api.get<Sensor[]>('/sensors');
    return response.data;
  },

  // Pobierz szczegóły czujnika
  getSensorById: async (id: string): Promise<SensorDetail> => {
    const response = await api.get<SensorDetail>(`/sensors/${id}`);
    return response.data;
  },

  // Pobierz pomiary dla czujnika w ostatnich 24h
  getSensorMeasurements: async (sensorId: string, hours: number = 24): Promise<Measurement[]> => {
    const response = await api.get<Measurement[]>(`/sensors/${sensorId}/measurements`, {
      params: { hours },
    });
    return response.data;
  },

  // Pobierz dane per pomieszczenie
  getRoomsData: async (): Promise<RoomData[]> => {
    const response = await api.get<RoomData[]>('/rooms');
    return response.data;
  },

  // Pobierz pomiary dla lokalizacji w ostatnich 24h
  getLocationMeasurements: async (location: string, hours: number = 24): Promise<Measurement[]> => {
    const response = await api.get<Measurement[]>(`/locations/${encodeURIComponent(location)}/measurements`, {
      params: { hours },
    });
    return response.data;
  },
};

// API z opcjonalnym fallbackiem na mock (tylko jeśli ENABLE_FALLBACK=true)
const apiWithFallback = {
  getAllSensors: () => withFallback(realApi.getAllSensors, mockApi.getAllSensors),
  getSensorById: (id: string) => withFallback(() => realApi.getSensorById(id), () => mockApi.getSensorById(id)),
  getSensorMeasurements: (sensorId: string, hours: number = 24) =>
    withFallback(
      () => realApi.getSensorMeasurements(sensorId, hours),
      () => mockApi.getSensorMeasurements(sensorId, hours)
    ),
  getRoomsData: () => withFallback(realApi.getRoomsData, mockApi.getRoomsData),
  getLocationMeasurements: (location: string, hours: number = 24) =>
    withFallback(
      () => realApi.getLocationMeasurements(location, hours),
      () => mockApi.getLocationMeasurements(location, hours)
    ),
};

// API bez fallbacku - rzuca błędy
const apiWithoutFallback = realApi;

// Typ dla API
type SensorApi = typeof mockApi;

// Eksportuj mock lub API w zależności od konfiguracji
let sensorApi: SensorApi;
if (FORCE_MOCK) {
  console.log('🔧 Używam mocka (VITE_USE_MOCK=true)');
  sensorApi = mockApi;
} else if (!HAS_API_URL) {
  console.log('🔧 Używam mocka (brak VITE_API_URL)');
  sensorApi = mockApi;
} else {
  console.log(`🔧 Próbuję połączyć się z API: ${API_BASE_URL}`);
  if (ENABLE_FALLBACK) {
    console.log('⚠️ Fallback na mock jest włączony - jeśli backend nie działa, użyje mocka');
    sensorApi = apiWithFallback;
  } else {
    console.log('✅ Używam tylko backendu - błędy będą widoczne (bez fallbacku na mock)');
    sensorApi = apiWithoutFallback;
  }
}

export { sensorApi };

