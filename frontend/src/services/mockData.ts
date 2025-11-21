import { Sensor, Measurement, RoomData, SensorDetail } from '../types';

// Symulacja opóźnienia sieci
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// Przykładowe czujniki
const mockSensors: Sensor[] = [
  {
    id: '1',
    sensorId: 'SENSOR-001',
    location: 'Biuro',
    sensorType: 'DHT22',
    lastSeen: new Date().toISOString(),
    isOnline: true,
  },
  {
    id: '2',
    sensorId: 'SENSOR-002',
    location: 'Biuro',
    sensorType: 'DHT22',
    lastSeen: new Date(Date.now() - 5 * 60 * 1000).toISOString(),
    isOnline: true,
  },
  {
    id: '3',
    sensorId: 'SENSOR-003',
    location: 'Sala konferencyjna',
    sensorType: 'DHT11',
    lastSeen: new Date(Date.now() - 2 * 60 * 1000).toISOString(),
    isOnline: true,
  },
  {
    id: '4',
    sensorId: 'SENSOR-004',
    location: 'Kuchnia',
    sensorType: 'DHT22',
    lastSeen: new Date(Date.now() - 15 * 60 * 1000).toISOString(),
    isOnline: true,
  },
  {
    id: '5',
    sensorId: 'SENSOR-005',
    location: 'Magazyn',
    sensorType: 'DHT11',
    lastSeen: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
    isOnline: false,
  },
  {
    id: '6',
    sensorId: 'SENSOR-006',
    location: 'Recepcja',
    sensorType: 'DHT22',
    lastSeen: new Date(Date.now() - 30 * 1000).toISOString(),
    isOnline: true,
  },
];

// Generowanie pomiarów dla czujnika w ostatnich N godzinach
const generateMeasurements = (sensorId: string, hours: number = 24): Measurement[] => {
  const measurements: Measurement[] = [];
  const now = Date.now();
  const interval = 15 * 60 * 1000; // Co 15 minut
  const count = Math.floor((hours * 60) / 15);

  // Bazowe wartości z lekkimi wariacjami per czujnik
  const baseTemp = 20 + (sensorId.charCodeAt(sensorId.length - 1) % 5);
  const baseHumidity = 45 + (sensorId.charCodeAt(sensorId.length - 1) % 15);

  for (let i = count; i >= 0; i--) {
    const timestamp = new Date(now - i * interval);
    // Symulacja naturalnych wahań temperatury i wilgotności
    const tempVariation = Math.sin((i / count) * Math.PI * 2) * 3;
    const humidityVariation = Math.cos((i / count) * Math.PI * 2) * 5;
    const randomNoise = (Math.random() - 0.5) * 1.5;

    measurements.push({
      id: `meas-${sensorId}-${i}`,
      sensorId,
      temperature: Number((baseTemp + tempVariation + randomNoise).toFixed(1)),
      humidity: Number((baseHumidity + humidityVariation + randomNoise * 2).toFixed(1)),
      timestamp: timestamp.toISOString(),
    });
  }

  return measurements;
};

// Obliczanie danych per pokój
const calculateRoomData = (): RoomData[] => {
  const rooms = new Map<string, { temps: number[]; humidities: number[]; count: number }>();

  mockSensors.forEach(sensor => {
    if (!rooms.has(sensor.location)) {
      rooms.set(sensor.location, { temps: [], humidities: [], count: 0 });
    }
    const room = rooms.get(sensor.location)!;
    room.count++;

    // Pobierz najnowszy pomiar dla tego czujnika
    const measurements = generateMeasurements(sensor.sensorId, 1);
    if (measurements.length > 0) {
      const latest = measurements[measurements.length - 1];
      room.temps.push(latest.temperature);
      room.humidities.push(latest.humidity);
    }
  });

  return Array.from(rooms.entries()).map(([location, data]) => ({
    location,
    currentTemperature: data.temps.length > 0
      ? Number((data.temps.reduce((a, b) => a + b, 0) / data.temps.length).toFixed(1))
      : null,
    currentHumidity: data.humidities.length > 0
      ? Number((data.humidities.reduce((a, b) => a + b, 0) / data.humidities.length).toFixed(1))
      : null,
    sensorCount: data.count,
  }));
};

export const mockApi = {
  getAllSensors: async (): Promise<Sensor[]> => {
    await delay(300);
    return [...mockSensors];
  },

  getSensorById: async (id: string): Promise<SensorDetail> => {
    await delay(200);
    const sensor = mockSensors.find(s => s.id === id);
    if (!sensor) {
      throw new Error(`Czujnik o ID ${id} nie został znaleziony`);
    }
    const measurements = generateMeasurements(sensor.sensorId, 24);
    return {
      ...sensor,
      measurements,
    };
  },

  getSensorMeasurements: async (sensorId: string, hours: number = 24): Promise<Measurement[]> => {
    await delay(200);
    const sensor = mockSensors.find(s => s.sensorId === sensorId || s.id === sensorId);
    if (!sensor) {
      return [];
    }
    return generateMeasurements(sensor.sensorId, hours);
  },

  getRoomsData: async (): Promise<RoomData[]> => {
    await delay(250);
    return calculateRoomData();
  },

  getLocationMeasurements: async (location: string, hours: number = 24): Promise<Measurement[]> => {
    await delay(200);
    const sensorsInLocation = mockSensors.filter(s => s.location === location);
    const allMeasurements: Measurement[] = [];

    sensorsInLocation.forEach(sensor => {
      const measurements = generateMeasurements(sensor.sensorId, hours);
      allMeasurements.push(...measurements);
    });

    // Sortuj po czasie
    return allMeasurements.sort((a, b) => 
      new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
    );
  },
};





