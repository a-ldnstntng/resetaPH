// src/components/TabBar.tsx
import clsx from "clsx";
import { useLanguage } from "../context/LanguageContext";

export type ResultsTab = "presyo" | "inumin" | "botika";

interface TabBarProps {
  activeTab: ResultsTab;
  onSelectTab: (tab: ResultsTab) => void;
}

export function TabBar({ activeTab, onSelectTab }: TabBarProps) {
  const { t } = useLanguage();

  const tabs: Array<{ id: ResultsTab; label: string }> = [
    { id: "presyo", label: t.tabPrice },
    { id: "inumin", label: t.tabHowToTake },
    { id: "botika", label: t.tabPharmacy },
  ];

  const activeIndex = tabs.findIndex((item) => item.id === activeTab);

  return (
    <div className="sticky top-0 z-20 bg-bg pt-1 pb-3">
      <div
        role="tablist"
        aria-label={t.stepResults}
        className="relative grid grid-cols-3 border-b border-border bg-surface rounded-t-[16px]"
      >
        {tabs.map((tab) => {
          const isSelected = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              id={`tab-${tab.id}`}
              role="tab"
              type="button"
              aria-selected={isSelected}
              aria-controls={`tabpanel-${tab.id}`}
              onClick={() => onSelectTab(tab.id)}
              className={clsx(
                "min-h-[48px] px-2 py-2.5 text-center transition-colors duration-150 ease-out cursor-pointer whitespace-nowrap",
                isSelected ? "text-body-strong text-primary" : "text-body text-ink-muted hover:text-ink"
              )}
            >
              {tab.label}
            </button>
          );
        })}
        <div
          aria-hidden="true"
          className="absolute bottom-0 left-0 h-[3px] w-1/3 bg-primary transition-transform duration-200 ease-out"
          style={{ transform: `translateX(${activeIndex * 100}%)` }}
        />
      </div>
    </div>
  );
}
