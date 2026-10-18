// src/components/AppShell.tsx
import React from "react";
import { Settings } from "lucide-react";
import { useLanguage } from "../context/LanguageContext";
import { SettingsModal } from "./SettingsModal";
import { LanguageOnboardingModal } from "./LanguageOnboardingModal";

interface AppShellProps {
  children: React.ReactNode;
  onBrandClick?: () => void;
}

export function AppShell({ children, onBrandClick }: AppShellProps) {
  const { language, openSettings, showOnboarding, t } = useLanguage();

  return (
    <div className="min-h-screen w-full bg-bg text-ink flex justify-center">
      <div className="w-full max-w-[480px] min-h-screen flex flex-col px-5 py-4">
        {/* Top App Header with Brand and Settings */}
        <header className="flex items-center justify-between pb-3 pt-1 border-b border-border/70 mb-2">
          {onBrandClick ? (
            <button
              type="button"
              onClick={onBrandClick}
              className="flex items-center gap-2 cursor-pointer group text-left"
              title={t.homeBrandTitle}
            >
              <span className="text-h2 font-extrabold text-primary tracking-tight group-hover:opacity-90">
                ResetaPH
              </span>
            </button>
          ) : (
            <div className="flex items-center gap-2">
              <span className="text-h2 font-extrabold text-primary tracking-tight">
                ResetaPH
              </span>
            </div>
          )}

          <button
            type="button"
            onClick={openSettings}
            aria-label={t.settings}
            title={t.settings}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-full border border-border bg-surface text-ink hover:bg-bg transition-colors duration-150 cursor-pointer shadow-xs"
          >
            <Settings className="w-4 h-4 text-primary" />
            <span className="text-caption font-bold uppercase tracking-wider text-ink">
              {language === "en" ? "EN" : "FIL"}
            </span>
          </button>
        </header>

        {children}

        {/* First-time visitor onboarding & Settings modal */}
        {showOnboarding && <LanguageOnboardingModal />}
        <SettingsModal />
      </div>
    </div>
  );
}
