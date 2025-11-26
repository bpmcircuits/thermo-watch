import { ChangeEvent } from 'react';
import { useLanguage } from '../i18n/LanguageContext';
import { useTranslation } from '../i18n/useTranslation';
import type { Language } from '../i18n/translations';
import './LanguageSwitcher.css';

const LanguageSwitcher = () => {
  const { language, setLanguage } = useLanguage();
  const { t } = useTranslation();

  const handleChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setLanguage(event.target.value as Language);
  };

  return (
    <label className="language-switcher" aria-label={t('languageSwitcher.accessibleLabel')}>
      <span className="language-label">{t('languageSwitcher.label')}</span>
      <select value={language} onChange={handleChange}>
        <option value="pl">{t('languageSwitcher.polish')}</option>
        <option value="en">{t('languageSwitcher.english')}</option>
      </select>
    </label>
  );
};

export default LanguageSwitcher;

