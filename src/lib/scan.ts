// src/lib/scan.ts
import type {
  PrescriptionItem,
  PriceEntry,
  ScanResult,
  Slot,
} from "./types";
import type { Language } from "./i18n";
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
  mealNoteEn?: string | null;
  noteTl: string | null;
  noteEn?: string | null;
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
      mealNoteEn: item.mealNoteEn ?? null,
      noteEn: item.noteEn ?? null,
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
  extraction: PrescriptionExtractionResponse,
  language: Language = "en"
): AnalyzePrescriptionOutcome {
  const notDetectedText = language === "en" ? "Not detected" : "Hindi nabasa";
  const rawItems = Array.isArray(extraction.items) ? extraction.items : [];
  const items: PrescriptionItem[] = rawItems.map((raw, index) => {
    const rawUncertain = normalizeUncertainFields(
      Array.isArray(raw.uncertainFields) ? raw.uncertainFields : []
    );

    const rawGeneric =
      typeof raw.genericName === "string" && raw.genericName.trim().length > 0
        ? raw.genericName.trim()
        : raw.writtenAs?.trim() || notDetectedText;

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
      raw.noteTl ?? null,
      raw.mealNoteEn ?? null,
      raw.noteEn ?? null,
      language
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
      mealNoteEn: validated.mealNoteEn,
      noteTl: validated.noteTl,
      noteEn: validated.noteEn,
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

/**
 * Enhances handwriting contrast, removes dim phone camera shadows, and downscales
 * safely to max 2000px using standard browser HTML5 Canvas.
 */
async function enhancePrescriptionImage(
  file: File
): Promise<{ mimeType: string; base64Data: string }> {
  if (typeof window === "undefined" || !file.type.startsWith("image/")) {
    return readFileAsBase64FullResolution(file);
  }

  return new Promise((resolve) => {
    const img = new Image();
    const url = URL.createObjectURL(file);

    img.onload = () => {
      URL.revokeObjectURL(url);
      const canvas = document.createElement("canvas");
      let width = img.naturalWidth || img.width;
      let height = img.naturalHeight || img.height;

      const maxEdge = 2000;
      if (width > maxEdge || height > maxEdge) {
        if (width > height) {
          height = Math.round((height * maxEdge) / width);
          width = maxEdge;
        } else {
          width = Math.round((width * maxEdge) / height);
          height = maxEdge;
        }
      }

      canvas.width = width;
      canvas.height = height;
      const ctx = canvas.getContext("2d");
      if (!ctx) {
        readFileAsBase64FullResolution(file).then(resolve);
        return;
      }

      ctx.drawImage(img, 0, 0, width, height);

      try {
        const imgData = ctx.getImageData(0, 0, width, height);
        const d = imgData.data;

        // Auto contrast boost: lighten paper background and deepen ballpoint pen strokes
        for (let i = 0; i < d.length; i += 4) {
          const lum = 0.299 * d[i] + 0.587 * d[i + 1] + 0.114 * d[i + 2];
          const factor = 1.25;
          const adjusted = factor * (lum - 128) + 128;
          const clamped = Math.max(0, Math.min(255, adjusted));

          d[i] = Math.round(d[i] * 0.3 + clamped * 0.7);
          d[i + 1] = Math.round(d[i + 1] * 0.3 + clamped * 0.7);
          d[i + 2] = Math.round(d[i + 2] * 0.3 + clamped * 0.7);
        }

        ctx.putImageData(imgData, 0, 0);
        const dataUrl = canvas.toDataURL("image/jpeg", 0.9);
        const commaIndex = dataUrl.indexOf(",");
        const base64Data = commaIndex >= 0 ? dataUrl.slice(commaIndex + 1) : dataUrl;
        resolve({ mimeType: "image/jpeg", base64Data });
      } catch {
        readFileAsBase64FullResolution(file).then(resolve);
      }
    };

    img.onerror = () => {
      URL.revokeObjectURL(url);
      readFileAsBase64FullResolution(file).then(resolve);
    };

    img.src = url;
  });
}

export async function analyzePrescription(
  input?: ScanResult | File,
  deepScan = false,
  language: Language = "en"
): Promise<AnalyzePrescriptionOutcome> {
  if (input instanceof File) {
    if (typeof navigator !== "undefined" && !navigator.onLine) {
      const offlineMsg =
        language === "fil"
          ? "Walang koneksyon sa internet. Pakisuri ang iyong Wi-Fi o mobile data."
          : "No internet connection. Please check your Wi-Fi or cellular network.";
      console.error("[ResetaNetwork] Device is offline:", { url: "/api/scan", offline: true });
      throw new Error(offlineMsg);
    }

    const { mimeType, base64Data } = await enhancePrescriptionImage(input);
    const controller = new AbortController();
    const timeoutId = window.setTimeout(() => controller.abort(), deepScan ? 90000 : 45000);

    try {
      console.log(`[ResetaNetwork] --> POST /api/scan (mimeType=${mimeType}, size=${base64Data.length} chars, deepScan=${deepScan})`);
      const response = await fetch("/api/scan", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ mimeType, base64Data, deepScan, language }),
        signal: controller.signal,
      });

      if (!response.ok) {
        const errBody = (await response.json().catch(() => ({}))) as {
          error?: string;
        };
        const errorDetail = errBody.error || `Scan request failed with HTTP ${response.status}`;
        console.error("[ResetaNetwork] <-- HTTP Error:", {
          status: response.status,
          error: errorDetail,
        });
        throw new Error(errorDetail);
      }

      const payload = (await response.json()) as {
        rawText: string;
        extraction: PrescriptionExtractionResponse;
      };

      console.log(`[ResetaNetwork] <-- SUCCESS 200 /api/scan. Parsed ${payload.extraction?.items?.length || 0} medications.`);

      return mapExtractionToScanResult(payload.extraction, language);
    } catch (err) {
      console.error("[ResetaNetwork] <-- Request Failed:", {
        url: "/api/scan",
        error: err instanceof Error ? err.message : String(err),
      });
      throw err;
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
