import { useState } from 'react';
import './SystemMonitoring.css';

const SystemMonitoring = () => {
  const [grafanaUrl, setGrafanaUrl] = useState(
    import.meta.env.VITE_GRAFANA_URL || 'http://localhost:3001'
  );
  const [useIframe, setUseIframe] = useState(true);

  // Przykładowe panele Grafany - można dostosować do własnych paneli
  const grafanaPanels = [
    {
      id: 'system-overview',
      title: 'Przegląd systemu',
      url: `${grafanaUrl}/d/system-overview?orgId=1&from=now-6h&to=now&refresh=30s`,
    },
    {
      id: 'sensor-metrics',
      title: 'Metryki czujników',
      url: `${grafanaUrl}/d/sensor-metrics?orgId=1&from=now-6h&to=now&refresh=30s`,
    },
    {
      id: 'database-performance',
      title: 'Wydajność bazy danych',
      url: `${grafanaUrl}/d/db-performance?orgId=1&from=now-6h&to=now&refresh=30s`,
    },
  ];

  return (
    <div className="system-monitoring">
      <h1 className="monitoring-title">Monitoring Systemu</h1>

      <div className="monitoring-controls">
        <div className="control-group">
          <label htmlFor="grafana-url">URL Grafany:</label>
          <input
            id="grafana-url"
            type="text"
            value={grafanaUrl}
            onChange={(e) => setGrafanaUrl(e.target.value)}
            placeholder="http://localhost:3001"
            className="url-input"
          />
        </div>
        <div className="control-group">
          <label>
            <input
              type="checkbox"
              checked={useIframe}
              onChange={(e) => setUseIframe(e.target.checked)}
            />
            Użyj iframe (w przeciwnym razie linki)
          </label>
        </div>
      </div>

      {useIframe ? (
        <div className="grafana-panels">
          {grafanaPanels.map((panel) => (
            <div key={panel.id} className="panel-container">
              <h3 className="panel-title">{panel.title}</h3>
              <div className="iframe-wrapper">
                <iframe
                  src={panel.url}
                  className="grafana-iframe"
                  title={panel.title}
                  frameBorder="0"
                />
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="grafana-links">
          <h2>Panele Grafany</h2>
          <div className="links-grid">
            {grafanaPanels.map((panel) => (
              <a
                key={panel.id}
                href={panel.url}
                target="_blank"
                rel="noopener noreferrer"
                className="grafana-link"
              >
                <div className="link-content">
                  <h3>{panel.title}</h3>
                  <span className="link-icon">🔗</span>
                </div>
              </a>
            ))}
          </div>
        </div>
      )}

      <div className="monitoring-info">
        <h2>Informacje</h2>
        <p>
          Ta sekcja pozwala na integrację z panelami Grafany do monitorowania systemu.
          Możesz wyświetlać panele bezpośrednio w aplikacji (iframe) lub otwierać je w nowych
          zakładkach (linki).
        </p>
        <p>
          <strong>Uwaga:</strong> Aby iframe działał poprawnie, Grafana musi mieć włączoną opcję
          "allow_embedding" w konfiguracji.
        </p>
      </div>
    </div>
  );
};

export default SystemMonitoring;





