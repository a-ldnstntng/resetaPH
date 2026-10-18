// src/lib/geo.ts
import type { Branch, PharmacyId } from "./types";
import { SAMPLE_BRANCHES } from "./sample-data";

export interface LatLng {
  lat: number;
  lng: number;
}

const EARTH_RADIUS_M = 6371000;

function toRadians(deg: number): number {
  return (deg * Math.PI) / 180;
}

export function haversineMeters(a: LatLng, b: LatLng): number {
  const dLat = toRadians(b.lat - a.lat);
  const dLng = toRadians(b.lng - a.lng);
  const lat1 = toRadians(a.lat);
  const lat2 = toRadians(b.lat);

  const sinLat = Math.sin(dLat / 2);
  const sinLng = Math.sin(dLng / 2);
  const h =
    sinLat * sinLat +
    Math.cos(lat1) * Math.cos(lat2) * sinLng * sinLng;
  const c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
  return EARTH_RADIUS_M * c;
}

export function formatDistance(meters: number): string {
  if (meters < 1000) {
    return `${Math.round(meters)} m`;
  }
  const km = meters / 1000;
  return `${km.toFixed(1)} km`;
}

/**
 * Queries real physical pharmacies around user coordinates from OpenStreetMap Overpass API.
 * Falls back gracefully to verified sample branches if offline or API times out.
 */
export async function fetchNearbyPharmacies(
  center: LatLng,
  radiusMeters = 3000
): Promise<Branch[]> {
  try {
    const controller = new AbortController();
    const timerId = window.setTimeout(() => controller.abort(), 6000);
    const query = `[out:json][timeout:5];(node["amenity"="pharmacy"](around:${radiusMeters},${center.lat},${center.lng}););out 20;`;
    const res = await fetch(
      `https://overpass-api.de/api/interpreter?data=${encodeURIComponent(query)}`,
      { signal: controller.signal }
    );
    window.clearTimeout(timerId);

    if (!res.ok) throw new Error("Overpass query failed");
    const data = await res.json();
    const elements: Array<{
      id: number;
      lat: number;
      lon: number;
      tags?: Record<string, string>;
    }> = data.elements || [];

    if (elements.length === 0) return SAMPLE_BRANCHES;

    return elements.map((el) => {
      const name = el.tags?.name || "Botika";
      const lower = name.toLowerCase();
      let pharmacyId: PharmacyId = "tgp";
      if (lower.includes("mercury")) pharmacyId = "mercury";
      else if (lower.includes("watson")) pharmacyId = "watsons";

      const open24h =
        el.tags?.opening_hours === "24/7" ||
        lower.includes("24 oras") ||
        lower.includes("24 hours") ||
        lower.includes("24h");

      return {
        id: `osm-${el.id}`,
        pharmacyId,
        name,
        address: el.tags?.["addr:street"] || el.tags?.["addr:city"] || null,
        phone: el.tags?.phone || el.tags?.["contact:phone"] || null,
        lat: el.lat,
        lng: el.lon,
        open24h: open24h ? true : null,
        stockTier: "high",
        isSample: false,
      };
    });
  } catch {
    return SAMPLE_BRANCHES;
  }
}
