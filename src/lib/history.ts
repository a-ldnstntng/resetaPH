// src/lib/history.ts
import type { ScanResult } from "./types";

const STORAGE_KEY = "resetaph_local_scans_v1";
const MAX_HISTORY_ITEMS = 10;

export interface SavedScan {
  id: string;
  savedAt: string;
  doctorName: string | null;
  dateOnSlip: string | null;
  itemSummary: string;
  itemCount: number;
  scanResult: ScanResult;
}

export function loadScanHistory(): SavedScan[] {
  if (typeof window === "undefined" || !window.localStorage) return [];
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

export function saveScanToHistory(scanResult: ScanResult): SavedScan[] {
  if (typeof window === "undefined" || !window.localStorage) return [];
  try {
    const existing = loadScanHistory();
    const itemSummary = scanResult.items
      .slice(0, 3)
      .map((i) => `${i.genericName} ${i.strength}`.trim())
      .join(", ") + (scanResult.items.length > 3 ? "..." : "");

    const newEntry: SavedScan = {
      id: `scan-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`,
      savedAt: new Date().toISOString(),
      doctorName: scanResult.doctorName,
      dateOnSlip: scanResult.dateOnSlip,
      itemSummary: itemSummary || "Reseta",
      itemCount: scanResult.items.length,
      scanResult,
    };

    // Prepend and cap at MAX_HISTORY_ITEMS
    const updated = [newEntry, ...existing.filter((e) => e.itemSummary !== itemSummary)].slice(
      0,
      MAX_HISTORY_ITEMS
    );
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
    return updated;
  } catch {
    return [];
  }
}

export function deleteScanFromHistory(id: string): SavedScan[] {
  if (typeof window === "undefined" || !window.localStorage) return [];
  try {
    const updated = loadScanHistory().filter((item) => item.id !== id);
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
    return updated;
  } catch {
    return [];
  }
}

export function clearAllScanHistory(): void {
  if (typeof window === "undefined" || !window.localStorage) return;
  try {
    window.localStorage.removeItem(STORAGE_KEY);
  } catch {
    // Ignore storage quota or access errors
  }
}
