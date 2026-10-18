// src/components/ItemPriceCard.tsx
import type { PrescriptionItem, PharmacyId } from "../lib/types";
import { cheapest, genericTotal } from "../lib/pricing";
import { Card } from "./Card";
import { PharmacyPriceRow } from "./PharmacyPriceRow";

interface ItemPriceCardProps {
  item: PrescriptionItem;
}

const PHARMACY_ORDER: PharmacyId[] = ["tgp", "watsons", "mercury"];

export function ItemPriceCard({ item }: ItemPriceCardProps) {
  const cheapestEntry = cheapest(item);

  return (
    <Card className="flex flex-col gap-3">
      <div className="flex items-baseline justify-between gap-2 flex-wrap border-b border-border pb-3">
        <h3 className="text-h2 text-ink m-0">
          {`${item.genericName} ${item.strength}`}
        </h3>
        <span className="text-body text-ink-muted">
          {`${item.quantity} ${item.form}`}
        </span>
      </div>

      <div className="flex flex-col gap-2.5">
        {PHARMACY_ORDER.map((pharmacyId) => {
          const entry = item.genericPrices.find((p) => p.pharmacyId === pharmacyId);
          const unitPrice = entry?.unitPrice ?? null;
          const total = genericTotal(item, pharmacyId);
          const isCheapest =
            cheapestEntry !== null &&
            cheapestEntry.pharmacyId === pharmacyId &&
            unitPrice !== null;

          return (
            <PharmacyPriceRow
              key={pharmacyId}
              pharmacyId={pharmacyId}
              unitPrice={unitPrice}
              totalPrice={total}
              isCheapest={isCheapest}
            />
          );
        })}
      </div>
    </Card>
  );
}
