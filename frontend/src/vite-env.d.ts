/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_URL?: string;
  readonly VITE_GRAFANA_URL?: string;
  readonly VITE_USE_MOCK?: string;
  readonly VITE_ENABLE_FALLBACK?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

