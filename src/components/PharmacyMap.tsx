// src/components/PharmacyMap.tsx
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import type { Branch } from "../lib/types";
import type { LatLng } from "../lib/geo";

interface PharmacyMapProps {
  center: LatLng;
  branches: Branch[];
}

// VERIFY: L.divIcon options for custom HTML circular pins without external marker images
const branchPinIcon = L.divIcon({
  className: "leaflet-div-icon",
  html: '<div style="width: 24px; height: 24px; border-radius: 999px; background-color: #0B6B4F; border: 3px solid #FFFFFF; box-shadow: 0 1px 2px rgba(15,27,22,0.06);"></div>',
  iconSize: [24, 24],
  iconAnchor: [12, 12],
});

const userPinIcon = L.divIcon({
  className: "leaflet-div-icon",
  html: '<div style="width: 20px; height: 20px; border-radius: 999px; background-color: #0F1B16; border: 3px solid #E6F4EE; box-shadow: 0 1px 2px rgba(15,27,22,0.06);"></div>',
  iconSize: [20, 20],
  iconAnchor: [10, 10],
});

export function PharmacyMap({ center, branches }: PharmacyMapProps) {
  return (
    <div
      className="w-full h-[240px] rounded-[20px] overflow-hidden border border-border card-shadow relative z-0"
      data-testid="pharmacy-map-container"
    >
      {/* VERIFY: react-leaflet MapContainer and TileLayer basic documented props */}
      <MapContainer
        center={[center.lat, center.lng]}
        zoom={15}
        scrollWheelZoom={false}
        style={{ height: "240px", width: "100%" }}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <Marker position={[center.lat, center.lng]} icon={userPinIcon} />
        {branches.map((branch) => (
          <Marker
            key={branch.id}
            position={[branch.lat, branch.lng]}
            icon={branchPinIcon}
          >
            <Popup>
              <span className="text-body-strong text-ink">{branch.name}</span>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
}
