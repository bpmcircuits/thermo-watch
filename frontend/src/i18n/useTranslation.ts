import { useCallback } from 'react';
import { translations, TranslationKey } from './translations';
import { useLanguage } from './LanguageContext';

const formatMessage = (
  template: string,
  params?: Record<string, string | number>
): string => {
  if (!params) {
    return template;
  }
  return Object.keys(params).reduce((message, key) => {
    const value = String(params[key]);
    return message.split(`{{${key}}}`).join(value);
  }, template);
};

export const useTranslation = () => {
  const { language } = useLanguage();

  const t = useCallback(
    (key: TranslationKey, params?: Record<string, string | number>) => {
      const dictionary = translations[language];
      const fallback = translations.pl;
      const template = dictionary[key] ?? fallback[key] ?? key;
      return formatMessage(template, params);
    },
    [language]
  );

  return { t, language };
};

