import { createContext, ReactNode, useContext, useMemo, useState } from 'react';
import { enUS, pl as plLocale } from 'date-fns/locale';
import type { Language } from './translations';

interface LanguageContextValue {
  language: Language;
  setLanguage: (language: Language) => void;
  dateLocale: Locale;
}

const LanguageContext = createContext<LanguageContextValue | undefined>(undefined);

export const LanguageProvider = ({ children }: { children: ReactNode }) => {
  const [language, setLanguage] = useState<Language>('pl');

  const value = useMemo<LanguageContextValue>(
    () => ({
      language,
      setLanguage,
      dateLocale: language === 'pl' ? plLocale : enUS,
    }),
    [language]
  );

  return <LanguageContext.Provider value={value}>{children}</LanguageContext.Provider>;
};

export const useLanguage = () => {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useLanguage must be used within a LanguageProvider');
  }
  return context;
};

