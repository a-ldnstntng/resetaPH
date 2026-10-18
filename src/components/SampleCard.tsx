// src/components/SampleCard.tsx
import { useState } from "react";
import { FileText } from "lucide-react";
import type { SamplePrescription } from "../lib/types";

interface SampleCardProps {
  sample: SamplePrescription;
  onSelect: (sample: SamplePrescription) => void;
}

export function SampleCard({ sample, onSelect }: SampleCardProps) {
  const [imageError, setImageError] = useState(false);

  return (
    <button
      type="button"
      onClick={() => onSelect(sample)}
      className="w-full min-h-[72px] p-4 rounded-[20px] border border-border bg-surface card-shadow flex items-center gap-4 text-left hover:bg-bg transition-colors duration-150 ease-out cursor-pointer"
    >
      <div className="w-16 h-16 rounded-[12px] bg-bg border border-border flex items-center justify-center shrink-0 overflow-hidden">
        {!imageError ? (
          <img
            src={sample.imageSrc}
            alt=""
            onError={() => setImageError(true)}
            className="w-full h-full object-cover"
          />
        ) : (
          <FileText className="w-7 h-7 text-ink-muted" aria-hidden="true" />
        )}
      </div>
      <div className="flex-1 min-w-0">
        <span className="text-body-strong text-ink block">{sample.label}</span>
      </div>
    </button>
  );
}
