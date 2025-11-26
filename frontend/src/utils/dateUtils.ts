import { parseISO } from 'date-fns';

/**
 * Parsuje timestamp z backendu (format PostgreSQL: "2025-11-26 16:04:43.571715+00")
 * i konwertuje na Date, traktując go jako UTC
 */
export function parseBackendTimestamp(timestamp: string): Date {
  if (!timestamp) {
    return new Date(NaN);
  }

  // Jeśli już jest w formacie ISO (zawiera 'T'), użyj parseISO
  if (timestamp.includes('T')) {
    return parseISO(timestamp);
  }

  // Konwertuj format PostgreSQL na ISO
  // "2025-11-26 16:04:43.571715+00" -> "2025-11-26T16:04:43.571715+00:00"
  let isoString = timestamp.replace(' ', 'T');

  // Jeśli kończy się na "+00" lub "-00", zamień na "Z" (UTC)
  if (isoString.endsWith('+00')) {
    isoString = isoString.slice(0, -3) + 'Z';
  } else if (isoString.endsWith('-00')) {
    isoString = isoString.slice(0, -3) + 'Z';
  } else if (isoString.match(/[+-]\d{2}$/)) {
    // Jeśli ma timezone jak "+01" lub "-05", dodaj ":00"
    isoString = isoString.replace(/([+-])(\d{2})$/, '$1$2:00');
  }

  return parseISO(isoString);
}