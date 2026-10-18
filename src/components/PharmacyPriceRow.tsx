// src/components/PharmacyPriceRow.tsx
import clsx from "clsx";
import { Check } from "lucide-react";
import type { PharmacyId } from "../lib/types";
import { PHARMACY_LABEL } from "../lib/sample-data";
import { formatPhp } from "../lib/pricing";
import { useLanguage } from "../context/LanguageContext";

interface PharmacyPriceRowProps {
  pharmacyId: PharmacyId;
  unitPrice: number | null;
  totalPrice: number | null;
  isCheapest: boolean;
}

export function PharmacyPriceRow({
  pharmacyId,
  unitPrice,
  totalPrice,
  isCheapest,
}: PharmacyPriceRowProps) {
  const { t } = useLanguage();
  const label = PHARMACY_LABEL[pharmacyId];

  return (
    <div
      className={clsx(
        "rounded-[12px] p-3.5 border flex items-center justify-between gap-3",
        isCheapest
          ? "bg-primary-soft border-primary text-ink"
          : "bg-surface border-border text-ink"
      )}
    >
      <div className="flex flex-col gap-0.5 min-w-0">
        <div className="flex items-center gap-2 flex-wrap">
          <span className="text-body-strong text-ink">{label}</span>
          {isCheapest && (
            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-[999px] bg-primary text-surface text-body-strong">
              <Check className="w-4 h-4" aria-hidden="true" />
              <span>{t.cheapestBadge}</span>
            </span>
          )}
        </div>
        {unitPrice !== null && (
          <span className="text-body text-ink-muted tabular-nums">
            {t.perPiece(formatPhp(unitPrice))}
          </span>
        )}
      </div>

      <div className="text-right shrink-0">
        {totalPrice !== null ? (
          <span
            className={clsx(
              "text-body-strong tabular-nums",
              isCheapest ? "text-primary" : "text-ink"
            )}
          >
            {formatPhp(totalPrice)}
          </span>
        ) : (
          <span className="text-body text-ink-muted">{t.noPriceYet}</span>
        )}
      </div>
    </div>
  );
}
