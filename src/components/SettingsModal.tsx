// src/components/SettingsModal.tsx
import { useEffect, useRef } from "react";
import { X, Globe, Check } from "lucide-react";
import { useLanguage } from "../context/LanguageContext";

export function SettingsModal() {
  const { language, setLanguage, isSettingsOpen, closeSettings, t } = useLanguage();
  const modalRef = useRef<HTMLDivElement>(null);
  const closeBtnRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    if (!isSettingsOpen) return;

    closeBtnRef.current?.focus();

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        closeSettings();
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isSettingsOpen, closeSettings]);

  if (!isSettingsOpen) return null;

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="settings-modal-title"
      className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4 overflow-y-auto animate-fade-in"
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          closeSettings();
        }
      }}
    >
      <div
        ref={modalRef}
        className="w-full max-w-[420px] rounded-[24px] bg-surface text-ink border border-border p-6 card-shadow flex flex-col gap-6"
      >
        <div className="flex items-center justify-between pb-3 border-b border-border">
          <div className="flex items-center gap-2.5">
            <Globe className="w-5 h-5 text-primary" aria-hidden="true" />
            <h2 id="settings-modal-title" className="text-h2 font-extrabold text-ink m-0">
              {t.settingsTitle}
            </h2>
          </div>
          <button
            ref={closeBtnRef}
            type="button"
            onClick={closeSettings}
            aria-label={t.settingsClose}
            className="w-10 h-10 rounded-full border border-border flex items-center justify-center hover:bg-bg text-ink-muted hover:text-ink cursor-pointer transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="flex flex-col gap-3">
          <div className="flex flex-col gap-1">
            <span className="text-body-strong text-ink">{t.settingsLanguageHeading}</span>
            <p className="text-caption text-ink-muted m-0">
              {t.settingsLanguageDescription}
            </p>
          </div>

          <div className="flex flex-col gap-2.5 pt-1">
            {/* English option */}
            <button
              type="button"
              onClick={() => setLanguage("en")}
              className={`w-full p-4 rounded-[16px] border text-left flex items-center justify-between gap-3 transition-colors duration-150 cursor-pointer ${
                language === "en"
                  ? "bg-primary-soft border-primary text-ink"
                  : "bg-surface border-border text-ink hover:bg-bg"
              }`}
            >
              <div className="flex flex-col">
                <span className="text-body-strong font-bold">English</span>
                <span className="text-caption text-ink-muted">
                  English UI, reminders, and audio
                </span>
              </div>
              <div
                className={`w-6 h-6 rounded-full border flex items-center justify-center shrink-0 ${
                  language === "en"
                    ? "bg-primary border-primary text-surface"
                    : "border-border bg-surface"
                }`}
              >
                {language === "en" && <Check className="w-4 h-4 stroke-[3]" />}
              </div>
            </button>

            {/* Filipino option */}
            <button
              type="button"
              onClick={() => setLanguage("fil")}
              className={`w-full p-4 rounded-[16px] border text-left flex items-center justify-between gap-3 transition-colors duration-150 cursor-pointer ${
                language === "fil"
                  ? "bg-primary-soft border-primary text-ink"
                  : "bg-surface border-border text-ink hover:bg-bg"
              }`}
            >
              <div className="flex flex-col">
                <span className="text-body-strong font-bold">Filipino</span>
                <span className="text-caption text-ink-muted">
                  Filipino UI, paalala, at boses
                </span>
              </div>
              <div
                className={`w-6 h-6 rounded-full border flex items-center justify-center shrink-0 ${
                  language === "fil"
                    ? "bg-primary border-primary text-surface"
                    : "border-border bg-surface"
                }`}
              >
                {language === "fil" && <Check className="w-4 h-4 stroke-[3]" />}
              </div>
            </button>
          </div>
        </div>

        <button
          type="button"
          onClick={closeSettings}
          className="w-full min-h-[50px] rounded-[16px] bg-primary text-surface text-body-strong flex items-center justify-center cursor-pointer hover:opacity-95 transition-opacity"
        >
          {t.settingsClose}
        </button>
      </div>
    </div>
  );
}
