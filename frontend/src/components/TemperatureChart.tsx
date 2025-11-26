import { useMemo } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { format } from 'date-fns';
import { Measurement } from '../types';
import { useLanguage } from '../i18n/LanguageContext';
import { useTranslation } from '../i18n/useTranslation';
import './TemperatureChart.css';

interface TemperatureChartProps {
  measurements: Measurement[];
}

const TemperatureChart = ({ measurements }: TemperatureChartProps) => {
  const { dateLocale } = useLanguage();
  const { t } = useTranslation();

  const chartData = useMemo(
    () =>
      measurements
        .filter((m) => m.temperature !== null || m.humidity !== null)
        .map((m) => ({
          time: format(new Date(m.timestamp), 'HH:mm', { locale: dateLocale }),
          timestamp: new Date(m.timestamp).getTime(),
          temperature: m.temperature !== null ? m.temperature : undefined,
          humidity: m.humidity !== null ? m.humidity : undefined,
        }))
        .sort((a, b) => a.timestamp - b.timestamp),
    [measurements, dateLocale]
  );

  const hasTemperature = chartData.some((d) => d.temperature !== undefined);
  const hasHumidity = chartData.some((d) => d.humidity !== undefined);

  if (chartData.length === 0 || (!hasTemperature && !hasHumidity)) {
    return (
      <div className="no-data">
        {measurements.length === 0
          ? t('chart.noData')
          : t('chart.noValidData')}
      </div>
    );
  }

  return (
    <div className="temperature-chart" style={{ width: '100%', minHeight: '400px' }}>
      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#ecf0f1" />
          <XAxis
            dataKey="time"
            stroke="#7f8c8d"
            style={{ fontSize: '12px' }}
            interval="preserveStartEnd"
          />
          {hasTemperature && (
            <YAxis
              yAxisId="temp"
              orientation="left"
              stroke="#e74c3c"
              label={{ value: t('chart.temperatureAxis'), angle: -90, position: 'insideLeft' }}
              style={{ fontSize: '12px' }}
            />
          )}
          {hasHumidity && (
            <YAxis
              yAxisId="humidity"
              orientation="right"
              stroke="#3498db"
              label={{ value: t('chart.humidityAxis'), angle: 90, position: 'insideRight' }}
              style={{ fontSize: '12px' }}
            />
          )}
          <Tooltip
            contentStyle={{
              backgroundColor: 'white',
              border: '1px solid #ecf0f1',
              borderRadius: '8px',
            }}
            formatter={(value: any, _name: string, props: any) => {
              if (value === null || value === undefined || typeof value !== 'number') {
                return ['N/A', ''];
              }
              const isTemperature = props?.dataKey === 'temperature';
              const unit = isTemperature ? '°C' : '%';
              const label = isTemperature ? t('chart.temperature') : t('chart.humidity');
              return [`${Number(value).toFixed(1)}${unit}`, label];
            }}
          />
          <Legend />
          {hasTemperature && (
            <Line
              yAxisId="temp"
              type="monotone"
              dataKey="temperature"
              stroke="#e74c3c"
              strokeWidth={2}
              dot={false}
              activeDot={{ r: 6, fill: '#e74c3c' }}
              name={t('chart.temperature')}
              connectNulls={false}
            />
          )}
          {hasHumidity && (
            <Line
              yAxisId="humidity"
              type="monotone"
              dataKey="humidity"
              stroke="#3498db"
              strokeWidth={2}
              dot={false}
              activeDot={{ r: 6, fill: '#3498db' }}
              name={t('chart.humidity')}
              connectNulls={false}
            />
          )}
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default TemperatureChart;

