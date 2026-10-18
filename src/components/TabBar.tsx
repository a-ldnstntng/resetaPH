// src/components/TabBar.tsx
import clsx from "clsx";

export type ResultsTab = "presyo" | "inumin" | "botika";

interface TabBarProps {
  activeTab: ResultsTab;
  onSelectTab: (tab: ResultsTab) => void;
}

const TABS: Array<{ id: ResultsTab; label: string }> = [
  { id: "presyo", label: "Presyo" },
  { id: "inumin", label: "Paano Inumin" },
  { id: "botika", label: "Botika" },
];

export function TabBar({ activeTab, onSelectTab }: TabBarProps) {
  const activeIndex = TABS.findIndex((t) => t.id === activeTab);

  return (
    <div className="sticky top-0 z-20 bg-bg pt-1 pb-3">
      <div
        role="tablist"
        aria-label="Resulta"
        className="relative grid grid-cols-3 border-b border-border bg-surface rounded-t-[16px]"
      >
        {TABS.map((tab) => {
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
