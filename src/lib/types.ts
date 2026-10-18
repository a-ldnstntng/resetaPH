// src/lib/types.ts
export type PharmacyId = "tgp" | "watsons" | "mercury";
export type Slot = "umaga" | "tanghali" | "gabi";

export interface PriceEntry {
  pharmacyId: PharmacyId;
  unitPrice: number | null;   // PHP per unit. null = unknown
  asOf: string | null;        // ISO date. null = unknown
}

export interface PrescriptionItem {
  id: string;
  writtenAs: string;              // exactly as on the slip
  sigRaw: string;                 // exactly as on the slip
  brandName: string | null;
  brandUnitPrice: number | null;  // PHP per unit
  genericName: string;
  strength: string;
  form: string;
  quantity: number;
  dosesPerDay: number;
  durationDays: number | null;
  slots: Slot[];
  mealNote: string | null;        // Tagalog/Filipino string
  mealNoteEn?: string | null;     // English translation
  noteTl: string | null;          // Tagalog/Filipino reminder
  noteEn?: string | null;         // English reminder
  uncertainFields: Array<"genericName" | "strength" | "quantity" | "sig">;
  genericPrices: PriceEntry[];
  isSample: boolean;
}

export interface ScanResult {
  doctorName: string | null;
  prcNumber: string | null;
  dateOnSlip: string | null;
  readScore: number | null;       // 0 to 100, null = do not show any percentage
  items: PrescriptionItem[];
}

export interface Branch {
  id: string;
  pharmacyId: PharmacyId;
  name: string;
  address: string | null;
  phone: string | null;
  lat: number;
  lng: number;
  open24h: boolean | null;
  stockTier: "high" | "call" | null;
  isSample: boolean;
}

export interface SamplePrescription {
  id: string;
  label: string;
  labelEn?: string;
  imageSrc: string;               // path under /public/samples/
  data: ScanResult | null;        // null = not ready, do not render this sample
}
