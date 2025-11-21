export interface Sensor {
  id: string;
  sensorId: string;
  location: string;
  sensorType: string;
  lastSeen: string;
  isOnline: boolean;
}

export interface Measurement {
  id: string;
  sensorId: string;
  temperature: number;
  humidity: number;
  timestamp: string;
}

export interface RoomData {
  location: string;
  currentTemperature: number | null;
  currentHumidity: number | null;
  sensorCount: number;
}

export interface SensorDetail extends Sensor {
  measurements: Measurement[];
}




