import { parseISO } from 'date-fns';

/**
 * Konwertuje timestamp z formatu PostgreSQL na format ISO, który może być parsowany przez date-fns
 * Format PostgreSQL: "2025-11-26 16:04:43.571715+00"
 * Format ISO: "2025-11-26T16:04:43.571715+00:00" lub "2025-11-26T16:04:43.571715Z"
 */
export function parsePostgresTimestamp(timestamp: string): Date {
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

  // Obsługa timezone
  // Format może być: "+00", "+00:00", "+01", "+01:00", "-05", "-05:00"
  // Jeśli kończy się na "+00" lub "-00" (bez :00), zamień na "Z" (UTC)
  if (isoString.match(/[+-]00$/)) {
    isoString = isoString.slice(0, -3) + 'Z';
  } else if (isoString.match(/[+-]00:00$/)) {
    // Jeśli kończy się na "+00:00" lub "-00:00", zamień na "Z"
    isoString = isoString.slice(0, -6) + 'Z';
  } else {
    // Jeśli ma timezone jak "+01" lub "-05" (bez :00), dodaj ":00"
    // Regex: znajdź +XX lub -XX na końcu (gdzie XX to 2 cyfry) i dodaj ":00"
    if (isoString.match(/[+-]\d{2}$/)) {
      isoString = isoString.replace(/([+-])(\d{2})$/, '$1$2:00');
    }
    // Jeśli już ma ":00", zostaw jak jest
  }

  // Spróbuj użyć parseISO, jeśli nie zadziała, użyj new Date()
  try {
    const date = parseISO(isoString);
    if (!isNaN(date.getTime())) {
      return date;
    }
  } catch (e) {
    // Fallback do new Date()
  }

  // Fallback: użyj new Date() bezpośrednio
  return new Date(isoString);
}

