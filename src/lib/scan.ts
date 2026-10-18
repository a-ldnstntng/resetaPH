// src/lib/scan.ts
import type {
  PrescriptionItem,
  PriceEntry,
  ScanResult,
  Slot,
} from "./types";
import { SAMPLE_SCAN_INFECTION } from "./sample-data";
import { lookupCatalogPrices, validateMedicationSafety } from "./catalog";

export interface TranscribedLine {
  rawText: string;
  isMedication: boolean;
}

export interface ExtractedMedicationItem {
  writtenAs: string;
  sigRaw: string;
  brandName: string | null;
  genericName: string;
  strength: string;
  form: string;
  quantity: number;
  dosesPerDay: number;
  durationDays: number | null;
  slots: string[];
  mealNote: string | null;
  noteTl: string | null;
  uncertainFields: string[];
}

export interface PrescriptionExtractionResponse {
  lines: TranscribedLine[];
  medicationLineCount: number;
  doctorName: string | null;
  prcNumber: string | null;
  dateOnSlip: string | null;
  readScore: number | null;
  items: ExtractedMedicationItem[];
}

export interface AnalyzePrescriptionOutcome {
  scanResult: ScanResult;
  hasLineCountMismatch: boolean;
}

const VALID_SLOTS: Slot[] = ["umaga", "tanghali", "gabi"];
const VALID_UNCERTAIN_FIELDS: Array<"genericName" | "strength" | "quantity" | "sig"> = [
  "genericName",
  "strength",
  "quantity",
  "sig",
];

export function cloneScanResult(source: ScanResult): ScanResult {
  return {
    doctorName: source.doctorName,
    prcNumber: source.prcNumber,
    dateOnSlip: source.dateOnSlip,
    readScore: source.readScore,
    items: source.items.map((item) => ({
      ...item,
      slots: [...item.slots],
      uncertainFields: [...item.uncertainFields],
      genericPrices: item.genericPrices.map((p) => ({ ...p })),
    })),
  };
}


function normalizeSlots(rawSlots: string[]): Slot[] {
  const filtered = rawSlots
    .map((s) => s.trim().toLowerCase() as Slot)
    .filter((s): s is Slot => VALID_SLOTS.includes(s));
  return filtered.length > 0 ? filtered : ["umaga"];
}

function normalizeUncertainFields(
  rawFields: string[]
): Array<"genericName" | "strength" | "quantity" | "sig"> {
  const allowed = new Set(VALID_UNCERTAIN_FIELDS);
  return Array.from(
    new Set(
      rawFields.filter((f): f is "genericName" | "strength" | "quantity" | "sig" =>
        allowed.has(f as "genericName" | "strength" | "quantity" | "sig")
      )
    )
  );
}

export function mapExtractionToScanResult(
  extraction: PrescriptionExtractionResponse
): AnalyzePrescriptionOutcome {
  const rawItems = Array.isArray(extraction.items) ? extraction.items : [];
  const items: PrescriptionItem[] = rawItems.map((raw, index) => {
    const rawUncertain = normalizeUncertainFields(
      Array.isArray(raw.uncertainFields) ? raw.uncertainFields : []
    );

    const rawGeneric =
      typeof raw.genericName === "string" && raw.genericName.trim().length > 0
        ? raw.genericName.trim()
        : raw.writtenAs?.trim() || "Hindi nabasa";

    const rawStrength =
      typeof raw.strength === "string" ? raw.strength.trim() : "";

    const rawForm =
      typeof raw.form === "string" && raw.form.trim().length > 0
        ? raw.form.trim()
        : "tablet";

    const quantity =
      typeof raw.quantity === "number" &&
      Number.isFinite(raw.quantity) &&
      raw.quantity > 0
        ? Math.round(raw.quantity)
        : 1;

    const dosesPerDay =
      typeof raw.dosesPerDay === "number" &&
      Number.isFinite(raw.dosesPerDay) &&
      raw.dosesPerDay > 0
        ? Math.round(raw.dosesPerDay)
        : 1;

    const durationDays =
      typeof raw.durationDays === "number" &&
      Number.isFinite(raw.durationDays) &&
      raw.durationDays > 0
        ? Math.round(raw.durationDays)
        : null;

    // Validate against Philippine clinical dosages & standard generic nomenclature
    const validated = validateMedicationSafety(
      rawGeneric,
      rawStrength,
      rawForm,
      dosesPerDay,
      durationDays,
      rawUncertain,
      raw.mealNote ?? null,
      raw.noteTl ?? null
    );

    const rawSlots = Array.isArray(raw.slots) && raw.slots.length > 0
      ? normalizeSlots(raw.slots)
      : validated.slots;

    const { brandUnitPrice, genericPrices } = lookupCatalogPrices(
      validated.genericName,
      validated.strength
    );

    return {
      id: `item-${index + 1}`,
      writtenAs: raw.writtenAs || validated.genericName,
      sigRaw: raw.sigRaw || "",
      brandName: raw.brandName || validated.brandName,
      brandUnitPrice,
      genericName: validated.genericName,
      strength: validated.strength,
      form: validated.form,
      quantity,
      dosesPerDay,
      durationDays,
      slots: rawSlots,
      mealNote: validated.mealNote,
      noteTl: validated.noteTl,
      uncertainFields: validated.uncertainFields,
      genericPrices,
      isSample: false,
    };
  });

  const expectedCount =
    typeof extraction.medicationLineCount === "number"
      ? extraction.medicationLineCount
      : items.length;

  const hasLineCountMismatch = expectedCount !== items.length;

  return {
    scanResult: {
      doctorName: extraction.doctorName ?? null,
      prcNumber: extraction.prcNumber ?? null,
      dateOnSlip: extraction.dateOnSlip ?? null,
      readScore: null,
      items,
    },
    hasLineCountMismatch,
  };
}

function readFileAsBase64FullResolution(
  file: File
): Promise<{ mimeType: string; base64Data: string }> {
  // Send image at full resolution without cropping or resizing below 1600 px on the long side.
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result;
      if (typeof result !== "string") {
        reject(new Error("Invalid file data"));
        return;
      }
      const commaIndex = result.indexOf(",");
      const base64Data = commaIndex >= 0 ? result.slice(commaIndex + 1) : result;
      resolve({
        mimeType: file.type || "image/jpeg",
        base64Data,
      });
    };
    reader.onerror = () => reject(new Error("Failed to read file"));
    reader.readAsDataURL(file);
  });
}

export async function analyzePrescription(
  input?: ScanResult | File
): Promise<AnalyzePrescriptionOutcome> {
  if (input instanceof File) {
    const { mimeType, base64Data } = await readFileAsBase64FullResolution(input);
    const controller = new AbortController();
    const timeoutId = window.setTimeout(() => controller.abort(), 60000);

    try {
      const response = await fetch("/api/scan", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ mimeType, base64Data }),
        signal: controller.signal,
      });

      if (!response.ok) {
        const errBody = (await response.json().catch(() => ({}))) as {
          error?: string;
        };
        throw new Error(errBody.error || `Scan request failed (${response.status})`);
      }

      const payload = (await response.json()) as {
        rawText: string;
        extraction: PrescriptionExtractionResponse;
      };

      console.log("[ResetaPH Scan] Raw model response text:", payload.rawText);
      console.log(
        "[ResetaPH Scan] Parsed item count:",
        Array.isArray(payload.extraction?.items)
          ? payload.extraction.items.length
          : 0
      );

      return mapExtractionToScanResult(payload.extraction);
    } finally {
      window.clearTimeout(timeoutId);
    }
  }

  const target = input ?? SAMPLE_SCAN_INFECTION;
  return new Promise((resolve) => {
    window.setTimeout(() => {
      resolve({
        scanResult: cloneScanResult(target),
        hasLineCountMismatch: false,
      });
    }, 1800);
  });
}
