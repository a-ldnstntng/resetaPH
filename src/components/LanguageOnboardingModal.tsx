import { useState, useEffect } from "react";
import { Check, Globe } from "lucide-react";
import { useLanguage } from "../context/LanguageContext";
import { type Language, translations } from "../lib/i18n";

export function LanguageOnboardingModal() {
  const { language, dismissOnboarding } = useLanguage();
  const [selected, setSelected] = useState<Language>(language);

  // Sync internal state if context language changes
  useEffect(() => {
    setSelected(language);
  }, [language]);

  const handleConfirm = () => {
    dismissOnboarding(selected);
  };

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        handleConfirm();
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [selected]);

  const activeT = translations[selected];

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="onboarding-modal-title"
      className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4 overflow-y-auto animate-fade-in"
    >
      <div className="w-full max-w-[420px] rounded-[24px] bg-surface text-ink border border-border p-6 card-shadow flex flex-col gap-6">
        <div className="flex flex-col items-center text-center gap-3">
          <div className="w-14 h-14 rounded-full bg-primary-soft border border-border flex items-center justify-center text-primary">
            <Globe className="w-7 h-7" aria-hidden="true" />
          </div>
          <div className="flex flex-col gap-1.5">
            <h2
              id="onboarding-modal-title"
              className="text-h2 font-extrabold text-ink m-0"
            >
              {activeT.onboardingTitle}
            </h2>
            <p className="text-body text-ink-muted m-0">
              {activeT.onboardingSubtitle}
            </p>
          </div>
        </div>

        {/* Language Options */}
        <div className="flex flex-col gap-3">
          {/* English Option */}
          <button
            type="button"
            onClick={() => setSelected("en")}
            className={`w-full p-4 rounded-[16px] border text-left flex items-center justify-between gap-3 transition-colors duration-150 cursor-pointer ${
              selected === "en"
                ? "bg-primary-soft border-primary text-ink"
                : "bg-surface border-border text-ink hover:bg-bg"
            }`}
          >
            <div className="flex flex-col">
              <span className="text-body-strong font-bold">English (Default)</span>
              <span className="text-caption text-ink-muted">
                Prices, dosage instructions, and AI deciphering in English
              </span>
            </div>
            <div
              className={`w-6 h-6 rounded-full border flex items-center justify-center shrink-0 ${
                selected === "en"
                  ? "bg-primary border-primary text-surface"
                  : "border-border bg-surface"
              }`}
            >
              {selected === "en" && <Check className="w-4 h-4 stroke-[3]" />}
            </div>
          </button>

          {/* Filipino Option */}
          <button
            type="button"
            onClick={() => setSelected("fil")}
            className={`w-full p-4 rounded-[16px] border text-left flex items-center justify-between gap-3 transition-colors duration-150 cursor-pointer ${
              selected === "fil"
                ? "bg-primary-soft border-primary text-ink"
                : "bg-surface border-border text-ink hover:bg-bg"
            }`}
          >
            <div className="flex flex-col">
              <span className="text-body-strong font-bold">Filipino / Tagalog</span>
              <span className="text-caption text-ink-muted">
                Mga presyo, paraan ng pag-inom, at gabay sa botika sa Filipino
              </span>
            </div>
            <div
              className={`w-6 h-6 rounded-full border flex items-center justify-center shrink-0 ${
                selected === "fil"
                  ? "bg-primary border-primary text-surface"
                  : "border-border bg-surface"
              }`}
            >
              {selected === "fil" && <Check className="w-4 h-4 stroke-[3]" />}
            </div>
          </button>
        </div>

        {/* Change later in settings notice */}
        <div className="rounded-[12px] bg-bg border border-border p-3 text-center">
          <p className="text-caption text-ink-muted m-0">
            {activeT.onboardingSettingsHint}
          </p>
        </div>

        {/* Continue Button */}
        <button
          type="button"
          onClick={handleConfirm}
          className="w-full min-h-[52px] rounded-[16px] bg-primary text-surface text-body-strong flex items-center justify-center cursor-pointer hover:opacity-95 transition-opacity"
        >
          {activeT.onboardingContinue}
        </button>
      </div>
    </div>
  );
}
