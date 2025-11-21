import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { Measurement } from '../types';
import { format } from 'date-fns';
import pl from 'date-fns/locale/pl';
import './TemperatureChart.css';

interface TemperatureChartProps {
  measurements: Measurement[];
}

const TemperatureChart = ({ measurements }: TemperatureChartProps) => {
  const chartData = measurements
    .map((m) => ({
      time: format(new Date(m.timestamp), 'HH:mm', { locale: pl }),
      timestamp: new Date(m.timestamp).getTime(),
      temperatura: m.temperature,
      wilgotność: m.humidity,
    }))
    .sort((a, b) => a.timestamp - b.timestamp);

  return (
    <div className="temperature-chart">
      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#ecf0f1" />
          <XAxis
            dataKey="time"
            stroke="#7f8c8d"
            style={{ fontSize: '12px' }}
            interval="preserveStartEnd"
          />
          <YAxis
            yAxisId="temp"
            orientation="left"
            stroke="#e74c3c"
            label={{ value: 'Temperatura (°C)', angle: -90, position: 'insideLeft' }}
            style={{ fontSize: '12px' }}
          />
          <YAxis
            yAxisId="humidity"
            orientation="right"
            stroke="#3498db"
            label={{ value: 'Wilgotność (%)', angle: 90, position: 'insideRight' }}
            style={{ fontSize: '12px' }}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: 'white',
              border: '1px solid #ecf0f1',
              borderRadius: '8px',
            }}
            formatter={(value: number, name: string) => [
              `${value.toFixed(1)}${name === 'Temperatura' ? '°C' : '%'}`,
              name,
            ]}
          />
          <Legend />
          <Line
            yAxisId="temp"
            type="monotone"
            dataKey="temperatura"
            stroke="#e74c3c"
            strokeWidth={2}
            dot={{ r: 3 }}
            activeDot={{ r: 6 }}
            name="Temperatura"
          />
          <Line
            yAxisId="humidity"
            type="monotone"
            dataKey="wilgotność"
            stroke="#3498db"
            strokeWidth={2}
            dot={{ r: 3 }}
            activeDot={{ r: 6 }}
            name="Wilgotność"
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default TemperatureChart;

