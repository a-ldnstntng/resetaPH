// src/components/SavingsHero.tsx
import { useEffect, useState } from "react";
import { Info } from "lucide-react";
import type { BasketSummary } from "../lib/pricing";
import { formatPhp } from "../lib/pricing";
import { PHARMACY_LABEL } from "../lib/sample-data";
import { useLanguage } from "../context/LanguageContext";
import { Card } from "./Card";
import { Chip } from "./Chip";
import { EmptyState } from "./EmptyState";

interface SavingsHeroProps {
  summary: BasketSummary;
}

export function SavingsHero({ summary }: SavingsHeroProps) {
  const { t } = useLanguage();
  const [displayedSavings, setDisplayedSavings] = useState(summary.totalSavings);

  useEffect(() => {
    if (!summary.hasComparable) {
      setDisplayedSavings(0);
      return;
    }

    const prefersReducedMotion =
      typeof window !== "undefined" &&
      typeof window.matchMedia === "function" &&
      window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    if (prefersReducedMotion) {
      setDisplayedSavings(summary.totalSavings);
      return;
    }

    const target = summary.totalSavings;
    const durationMs = 700;
    const startTime = performance.now();
    let frameId = 0;

    const tick = (now: number) => {
      const elapsed = now - startTime;
      const progress = Math.min(elapsed / durationMs, 1);
      const eased = 1 - Math.pow(1 - progress, 3);
      setDisplayedSavings(target * eased);
      if (progress < 1) {
        frameId = window.requestAnimationFrame(tick);
      } else {
        setDisplayedSavings(target);
      }
    };

    frameId = window.requestAnimationFrame(tick);
    return () => {
      window.cancelAnimationFrame(frameId);
    };
  }, [summary.hasComparable, summary.totalSavings]);

  const priceStampText =
    summary.latestAsOf !== null
      ? t.priceDateAsOf(summary.latestAsOf)
      : t.priceDateMissing;

  return (
    <div className="flex flex-col gap-4">
      {summary.hasComparable ? (
        <>
          <Card variant="primary-soft" className="flex flex-col gap-3">
            <span className="text-body-strong text-ink">
              {t.estimatedSavings}
            </span>
            <div className="flex items-baseline gap-3 flex-wrap">
              <span
                className="text-display-num text-primary"
                data-testid="total-savings-amount"
              >
                {formatPhp(displayedSavings)}
              </span>
              <Chip tone="primary">{t.cheaperGenericBadge(summary.savingsPercent)}</Chip>
            </div>
            <span className="text-body text-ink-muted">
              {t.ifBuyingGeneric}
            </span>
          </Card>

          <Card className="grid grid-cols-2 gap-4">
            <div className="flex flex-col gap-1 border-r border-border pr-3">
              <span className="text-body text-ink-muted">{t.brandedLabel}</span>
              <span
                className="text-h2 text-ink tabular-nums"
                data-testid="brand-total-amount"
              >
                {formatPhp(summary.totalBrand)}
              </span>
            </div>
            <div className="flex flex-col gap-1 pl-1">
              <span className="text-body text-ink-muted">
                {t.genericCheapestLabel}
              </span>
              <span
                className="text-h2 text-primary tabular-nums"
                data-testid="cheapest-generic-total"
              >
                {formatPhp(summary.totalCheapestGeneric)}
              </span>
              {summary.cheapestPharmacyId && (
                <span className="text-body text-ink-muted">
                  {PHARMACY_LABEL[summary.cheapestPharmacyId]}
                </span>
              )}
            </div>
          </Card>
        </>
      ) : (
        <EmptyState message={t.notEnoughPrices} />
      )}

      <div className="flex flex-col gap-1.5 px-1">
        <div className="flex items-start gap-2 text-ink-muted">
          <Info className="w-5 h-5 shrink-0 mt-1" aria-hidden="true" />
          <p className="text-body m-0">
            {t.priceEstimateDisclaimer}
          </p>
        </div>
        <p className="text-caption text-ink-muted m-0 pl-7">{priceStampText}</p>
      </div>

      {summary.nonComparableItems.length > 0 && (
        <div className="flex flex-col gap-1 px-1">
          {summary.nonComparableItems.map((nc) => (
            <p key={nc.id} className="text-body text-ink-muted m-0">
              {t.excludedItemNotice(nc.genericName, nc.strength)}
            </p>
          ))}
        </div>
      )}
    </div>
  );
}
