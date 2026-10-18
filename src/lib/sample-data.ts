// src/lib/sample-data.ts
import type { ScanResult, Branch, SamplePrescription, PharmacyId } from "./types";

export const PHARMACY_LABEL: Record<PharmacyId, string> = {
  tgp: "TGP / Generika",
  watsons: "Watsons",
  mercury: "Mercury Drug",
};

// Map center. VERIFY: approximate position of DLSU Manila.
export const DEFAULT_CENTER = { lat: 14.5648, lng: 120.9932 };

export const SAMPLE_SCAN_INFECTION: ScanResult = {
  doctorName: null,
  prcNumber: null,
  dateOnSlip: null,
  readScore: null,
  items: [
    {
      id: "item-1",
      writtenAs: "Augmentin 625mg #14",
      sigRaw: "1 tab BID PC x 7d",
      brandName: "Augmentin",
      brandUnitPrice: 82.0,
      genericName: "Co-Amoxiclav",
      strength: "625 mg",
      form: "tablet",
      quantity: 14,
      dosesPerDay: 2,
      durationDays: 7,
      slots: ["umaga", "gabi"],
      mealNote: "Pagkatapos kumain",
      noteTl: "Tapusin ang buong 7 araw kahit magaling na ang pakiramdam para hindi bumalik ang impeksyon.",
      uncertainFields: [],
      genericPrices: [
        { pharmacyId: "tgp", unitPrice: 18.0, asOf: null },
        { pharmacyId: "watsons", unitPrice: 24.0, asOf: null },
        { pharmacyId: "mercury", unitPrice: 26.0, asOf: null },
      ],
      isSample: true,
    },
    {
      id: "item-2",
      writtenAs: "Amlodipine 5mg #30",
      sigRaw: "1 tab OD",
      brandName: null,
      brandUnitPrice: null,
      genericName: "Amlodipine",
      strength: "5 mg",
      form: "tablet",
      quantity: 30,
      dosesPerDay: 1,
      durationDays: 30,
      slots: ["umaga"],
      mealNote: null,
      noteTl: null,
      uncertainFields: ["quantity"],
      genericPrices: [
        { pharmacyId: "tgp", unitPrice: null, asOf: null },
        { pharmacyId: "watsons", unitPrice: null, asOf: null },
        { pharmacyId: "mercury", unitPrice: null, asOf: null },
      ],
      isSample: true,
    },
  ],
};

// SAMPLE positions only. Real branch data will replace this file.
export const SAMPLE_BRANCHES: Branch[] = [
  { id: "b1", pharmacyId: "tgp", name: "TGP / Generika (sample branch)", address: null, phone: null, lat: 14.5660, lng: 120.9945, open24h: false, stockTier: "high", isSample: true },
  { id: "b2", pharmacyId: "watsons", name: "Watsons (sample branch)", address: null, phone: null, lat: 14.5635, lng: 120.9920, open24h: null, stockTier: "call", isSample: true },
  { id: "b3", pharmacyId: "mercury", name: "Mercury Drug (sample branch)", address: null, phone: null, lat: 14.5655, lng: 120.9910, open24h: true, stockTier: "high", isSample: true },
];

export const SAMPLES: SamplePrescription[] = [
  { id: "s1", label: "Antibiotic at maintenance", imageSrc: "/samples/rx-1.jpg", data: SAMPLE_SCAN_INFECTION },
  { id: "s2", label: "Diabetes maintenance", imageSrc: "/samples/rx-2.jpg", data: null },
  { id: "s3", label: "Hypertension at cholesterol", imageSrc: "/samples/rx-3.jpg", data: null },
];
