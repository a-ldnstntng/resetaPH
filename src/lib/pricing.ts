// src/lib/pricing.ts
import type { PrescriptionItem, PharmacyId, PriceEntry } from "./types";

const phpFormatter = new Intl.NumberFormat("en-PH", {
  style: "currency",
  currency: "PHP",
});

export function formatPhp(amount: number): string {
  return phpFormatter.format(amount);
}

export function brandTotal(item: PrescriptionItem): number | null {
  if (item.brandUnitPrice === null) {
    return null;
  }
  return item.brandUnitPrice * item.quantity;
}

export function genericTotal(item: PrescriptionItem, pharmacyId: PharmacyId): number | null {
  const entry = item.genericPrices.find((p) => p.pharmacyId === pharmacyId);
  if (!entry || entry.unitPrice === null) {
    return null;
  }
  return entry.unitPrice * item.quantity;
}

export function cheapest(item: PrescriptionItem): PriceEntry | null {
  let best: PriceEntry | null = null;
  for (const entry of item.genericPrices) {
    if (entry.unitPrice !== null) {
      if (best === null || (best.unitPrice !== null && entry.unitPrice < best.unitPrice)) {
        best = entry;
      }
    }
  }
  return best;
}

export function isComparable(item: PrescriptionItem): boolean {
  return brandTotal(item) !== null && cheapest(item) !== null;
}

export interface BasketSummary {
  hasComparable: boolean;
  totalBrand: number;
  totalCheapestGeneric: number;
  totalSavings: number;
  savingsPercent: number;
  cheapestPharmacyId: PharmacyId | null;
  nonComparableItems: PrescriptionItem[];
  latestAsOf: string | null;
}

export function computeBasketSummary(items: PrescriptionItem[]): BasketSummary {
  let totalBrand = 0;
  let totalCheapestGeneric = 0;
  let hasComparable = false;
  let cheapestPharmacyId: PharmacyId | null = null;
  let latestAsOf: string | null = null;
  const nonComparableItems: PrescriptionItem[] = [];
  const pharmacyTotals: Record<PharmacyId, number> = { tgp: 0, watsons: 0, mercury: 0 };

  for (const item of items) {
    const bTotal = brandTotal(item);
    const cheapEntry = cheapest(item);
    if (bTotal !== null && cheapEntry !== null && cheapEntry.unitPrice !== null) {
      hasComparable = true;
      totalBrand += bTotal;
      totalCheapestGeneric += cheapEntry.unitPrice * item.quantity;
      if (cheapEntry.asOf !== null) {
        latestAsOf = cheapEntry.asOf;
      }

      for (const pid of ["tgp", "watsons", "mercury"] as PharmacyId[]) {
        const pTotal = genericTotal(item, pid);
        if (pTotal !== null) {
          pharmacyTotals[pid] += pTotal;
        }
      }
    } else {
      nonComparableItems.push(item);
    }
  }

  // Find pharmacy with the lowest non-zero total basket cost
  let minCost = Infinity;
  for (const pid of ["tgp", "watsons", "mercury"] as PharmacyId[]) {
    if (pharmacyTotals[pid] > 0 && pharmacyTotals[pid] < minCost) {
      minCost = pharmacyTotals[pid];
      cheapestPharmacyId = pid;
    }
  }

  const totalSavings = hasComparable ? totalBrand - totalCheapestGeneric : 0;
  const savingsPercent =
    hasComparable && totalBrand > 0
      ? Math.round((totalSavings / totalBrand) * 100)
      : 0;

  return {
    hasComparable,
    totalBrand,
    totalCheapestGeneric,
    totalSavings,
    savingsPercent,
    cheapestPharmacyId,
    nonComparableItems,
    latestAsOf,
  };
}
