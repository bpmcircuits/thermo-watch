# ThermoWatch Frontend

Frontend aplikacji do monitorowania czujników temperatury i wilgotności.

## Funkcjonalności

### Dashboard
- **Kafelki z aktualną temperaturą/wilgotnością** - wyświetlanie danych per pomieszczenie
- **Wykres liniowy** - temperatura w wybranej lokalizacji w ostatnich 24h
- **Status czujników** - lista wszystkich czujników z informacją o statusie (online/offline)

### Widok szczegółowy czujnika
- Wykres temperatury i wilgotności w czasie
- Informacje o czujniku: sensorId, lokalizacja, typ sensora
- Status online/offline

### Monitoring systemu
- Integracja z Grafana (iframe lub linki)
- Konfigurowalny URL Grafany
- Możliwość przełączania między trybem iframe a linkami

## Technologie

- **React 18** - framework UI
- **TypeScript** - typowanie
- **React Router** - routing
- **Recharts** - wykresy
- **Axios** - komunikacja z API
- **Vite** - build tool
- **date-fns** - obsługa dat

## Instalacja

```bash
npm install
```

## Konfiguracja

Utwórz plik `.env` w katalogu głównym:

```env
# URL backendu API (WYMAGANE jeśli chcesz używać backendu)
VITE_API_URL=http://localhost:8080/api/v1

# Grafana URL (opcjonalne)
VITE_GRAFANA_URL=http://localhost:3001

# Wymuszenie użycia mocka (opcjonalne)
# VITE_USE_MOCK=true

# Włączenie fallbacku na mock przy błędach backendu (opcjonalne)
# VITE_ENABLE_FALLBACK=true
```

### Tryby pracy:

**1. Tylko Backend (domyślnie gdy VITE_API_URL jest ustawione):**
```env
VITE_API_URL=http://localhost:8080/api/v1
```
- Używa tylko backendu
- Błędy backendu będą widoczne w aplikacji
- **Bez automatycznego fallbacku na mock**

**2. Backend z fallbackiem na mock:**
```env
VITE_API_URL=http://localhost:8080/api/v1
VITE_ENABLE_FALLBACK=true
```
- Próbuje użyć backendu
- Jeśli backend nie działa → automatycznie przełącza na mock

**3. Tylko Mock (bez backendu):**
```env
# Nie ustawiaj VITE_API_URL lub:
VITE_USE_MOCK=true
```
- Zawsze używa mocka
- Nie próbuje łączyć się z backendem

### Mock zawiera:
- 6 przykładowych czujników w różnych lokalizacjach (Biuro, Sala konferencyjna, Kuchnia, Magazyn, Recepcja)
- Symulowane pomiary z ostatnich 24h (co 15 minut)
- Naturalne wahania temperatury i wilgotności
- Czujniki online/offline
- Opóźnienia symulujące prawdziwe zapytania API

## Uruchomienie

```bash
npm run dev
```

Aplikacja będzie dostępna pod adresem `http://localhost:3000`

## Build produkcyjny

```bash
npm run build
```

## API Endpoints

Aplikacja oczekuje następujących endpointów w backendzie:

- `GET /api/sensors` - lista wszystkich czujników
- `GET /api/sensors/:id` - szczegóły czujnika
- `GET /api/sensors/:id/measurements?hours=24` - pomiary czujnika
- `GET /api/rooms` - dane per pomieszczenie
- `GET /api/locations/:location/measurements?hours=24` - pomiary dla lokalizacji

## Struktura projektu

```
src/
├── components/          # Komponenty React
│   ├── Dashboard.tsx    # Główny dashboard
│   ├── SensorDetail.tsx # Szczegóły czujnika
│   ├── SystemMonitoring.tsx # Monitoring systemu
│   └── ...
├── services/            # Serwisy API
│   ├── api.ts           # Główny serwis API (mock/prawdziwe)
│   └── mockData.ts      # Mock z przykładowymi danymi
├── types/               # Definicje TypeScript
│   └── index.ts
├── App.tsx              # Główny komponent aplikacji
└── main.tsx             # Entry point
```

## Format danych

### Sensor
```typescript
{
  id: string;
  sensorId: string;
  location: string;
  sensorType: string;
  lastSeen: string; // ISO 8601
  isOnline: boolean;
}
```

### Measurement
```typescript
{
  id: string;
  sensorId: string;
  temperature: number;
  humidity: number;
  timestamp: string; // ISO 8601
}
```

### RoomData
```typescript
{
  location: string;
  currentTemperature: number;
  currentHumidity: number;
  sensorCount: number;
}
```

