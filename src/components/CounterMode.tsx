// src/components/CounterMode.tsx
import { useEffect, useRef } from "react";
import type { PrescriptionItem } from "../lib/types";
import { SecondaryButton } from "./SecondaryButton";

interface CounterModeProps {
  items: PrescriptionItem[];
  onClose: () => void;
}

export function CounterMode({ items, onClose }: CounterModeProps) {
  const closeButtonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    const previouslyFocused = document.activeElement as HTMLElement | null;
    closeButtonRef.current?.focus();

    let wakeLockSentinel: { release: () => Promise<void> } | null = null;

    // VERIFY: navigator.wakeLock.request("screen") Screen Wake Lock API check
    if (
      typeof navigator !== "undefined" &&
      "wakeLock" in navigator &&
      navigator.wakeLock &&
      typeof navigator.wakeLock.request === "function"
    ) {
      navigator.wakeLock
        .request("screen")
        .then((sentinel) => {
          wakeLockSentinel = sentinel;
        })
        .catch(() => {
          // Ignore wakeLock permission errors
        });
    }

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        onClose();
      }
    };
    window.addEventListener("keydown", handleKeyDown);

    return () => {
      window.removeEventListener("keydown", handleKeyDown);
      if (wakeLockSentinel) {
        wakeLockSentinel.release().catch(() => {});
      }
      if (previouslyFocused && typeof previouslyFocused.focus === "function") {
        previouslyFocused.focus();
      }
    };
  }, [onClose]);

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="counter-mode-heading"
      className="fixed inset-0 z-50 bg-surface text-ink flex justify-center overflow-y-auto"
    >
      <div className="w-full max-w-[480px] min-h-screen px-5 py-6 flex flex-col justify-between gap-6">
        <div className="flex flex-col gap-5">
          <header className="flex flex-col gap-2">
            <h1 id="counter-mode-heading" className="text-h1 text-ink m-0">
              Magandang araw po!
            </h1>
            <p className="text-body text-ink m-0">
              Pabili po ng generic na katumbas ng nasa reseta:
            </p>
          </header>

          <div className="flex flex-col border-t border-border">
            {items.map((item) => (
              <div
                key={item.id}
                className="py-5 border-b border-border flex flex-col gap-1.5"
              >
                <span className="text-counter-drug text-ink">
                  {`${item.genericName} ${item.strength}`}
                </span>
                <span className="text-h2 text-ink">
                  {`${item.quantity} ${item.form}`}
                </span>
              </div>
            ))}
          </div>

          <p className="text-body text-ink-muted m-0">
            Pakikumpirma po sa parmasyutiko.
          </p>
        </div>

        <div className="pt-2">
          <button
            ref={closeButtonRef}
            type="button"
            onClick={onClose}
            className="w-full min-h-[56px] px-5 py-3 rounded-[16px] bg-surface border border-border text-ink text-body-strong flex items-center justify-center cursor-pointer hover:bg-bg transition-colors duration-150 ease-out"
          >
            Isara
          </button>
        </div>
      </div>
    </div>
  );
}
