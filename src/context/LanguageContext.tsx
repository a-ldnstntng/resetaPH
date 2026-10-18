// src/context/LanguageContext.tsx
import React, { createContext, useContext, useEffect, useState } from "react";
import {
  type Language,
  type Translations,
  DEFAULT_LANGUAGE,
  LANGUAGE_STORAGE_KEY,
  translations,
} from "../lib/i18n";
import { getItemMealNote, getItemNote } from "../lib/catalog";
import type { PrescriptionItem } from "../lib/types";

interface LanguageContextValue {
  language: Language;
  setLanguage: (lang: Language) => void;
  t: Translations;
  showOnboarding: boolean;
  dismissOnboarding: (lang?: Language) => void;
  isSettingsOpen: boolean;
  openSettings: () => void;
  closeSettings: () => void;
  getMealNote: (item: PrescriptionItem) => string | null;
  getNote: (item: PrescriptionItem) => string | null;
}

const LanguageContext = createContext<LanguageContextValue | null>(null);

function getInitialState(): { lang: Language; isFirstTime: boolean } {
  if (typeof window === "undefined" || !window.localStorage) {
    return { lang: DEFAULT_LANGUAGE, isFirstTime: false };
  }
  try {
    const saved = window.localStorage.getItem(LANGUAGE_STORAGE_KEY);
    if (saved === "en" || saved === "fil") {
      return { lang: saved, isFirstTime: false };
    }
    // No language saved yet -> default to English and show onboarding popup
    return { lang: DEFAULT_LANGUAGE, isFirstTime: true };
  } catch {
    return { lang: DEFAULT_LANGUAGE, isFirstTime: false };
  }
}

export function LanguageProvider({ children }: { children: React.ReactNode }) {
  const [initial] = useState(() => getInitialState());
  const [language, setLanguageState] = useState<Language>(initial.lang);
  const [showOnboarding, setShowOnboarding] = useState<boolean>(initial.isFirstTime);
  const [isSettingsOpen, setIsSettingsOpen] = useState<boolean>(false);

  useEffect(() => {
    if (typeof document !== "undefined") {
      document.documentElement.lang = language === "fil" ? "fil-PH" : "en-PH";
    }
  }, [language]);

  const setLanguage = (lang: Language) => {
    setLanguageState(lang);
    if (typeof window !== "undefined" && window.localStorage) {
      try {
        window.localStorage.setItem(LANGUAGE_STORAGE_KEY, lang);
      } catch {
        // Ignore localStorage quota or private browsing errors
      }
    }
  };

  const dismissOnboarding = (chosenLang?: Language) => {
    const finalLang = chosenLang || language;
    setLanguage(finalLang);
    setShowOnboarding(false);
  };

  const openSettings = () => setIsSettingsOpen(true);
  const closeSettings = () => setIsSettingsOpen(false);

  const getMealNote = (item: PrescriptionItem) => getItemMealNote(item, language);
  const getNote = (item: PrescriptionItem) => getItemNote(item, language);

  const value: LanguageContextValue = {
    language,
    setLanguage,
    t: translations[language],
    showOnboarding,
    dismissOnboarding,
    isSettingsOpen,
    openSettings,
    closeSettings,
    getMealNote,
    getNote,
  };

  return (
    <LanguageContext.Provider value={value}>
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage(): LanguageContextValue {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error("useLanguage must be used within a LanguageProvider");
  }
  return context;
}
