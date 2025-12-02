import { useMemo } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { format } from 'date-fns';
import { parseBackendTimestamp } from '../utils/dateUtils';
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
        .filter((m) => m.temperature !== null || m.humidity !== null || m.pressure !== null)
        .map((m) => {
          const date = parseBackendTimestamp(m.timestamp);
          return {
            time: format(date, 'HH:mm', { locale: dateLocale }),
            timestamp: date.getTime(),
            temperature: m.temperature !== null ? m.temperature : undefined,
            humidity: m.humidity !== null ? m.humidity : undefined,
            pressure: m.pressure !== null ? m.pressure : undefined,
          };
        })
        .sort((a, b) => a.timestamp - b.timestamp),
    [measurements, dateLocale]
  );

  const hasTemperature = chartData.some((d) => d.temperature !== undefined);
  const hasHumidity = chartData.some((d) => d.humidity !== undefined);
  const hasPressure = chartData.some((d) => d.pressure !== undefined);

  if (chartData.length === 0 || (!hasTemperature && !hasHumidity && !hasPressure)) {
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
        <LineChart data={chartData} margin={{ top: 5, right: 20, left: 20, bottom: 5 }}>
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
              label={{ value: t('chart.temperatureAxis'), angle: -90, position: 'insideLeft', offset: 15 }}
              style={{ fontSize: '12px' }}
            />
          )}
          {hasHumidity && (
            <YAxis
              yAxisId="humidity"
              orientation="right"
              stroke="#3498db"
              label={{ value: t('chart.humidityAxis'), angle: 90, position: 'insideRight', offset: 15 }}
              style={{ fontSize: '12px' }}
            />
          )}
          {hasPressure && (
            <YAxis
              yAxisId="pressure"
              orientation="right"
              stroke="#9b59b6"
              label={{ value: t('chart.pressureAxis'), angle: 90, position: 'insideRight', offset: 15 }}
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
              const dataKey = props?.dataKey;
              let unit = '';
              let label = '';
              
              if (dataKey === 'temperature') {
                unit = '°C';
                label = t('chart.temperature');
              } else if (dataKey === 'humidity') {
                unit = '%';
                label = t('chart.humidity');
              } else if (dataKey === 'pressure') {
                unit = ' hPa';
                label = t('chart.pressure');
              }
              
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
          {hasPressure && (
            <Line
              yAxisId="pressure"
              type="monotone"
              dataKey="pressure"
              stroke="#9b59b6"
              strokeWidth={2}
              dot={false}
              activeDot={{ r: 6, fill: '#9b59b6' }}
              name={t('chart.pressure')}
              connectNulls={false}
            />
          )}
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default TemperatureChart;

