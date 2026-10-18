// src/components/ScanOverlay.tsx
import { useState } from "react";
import { FileText } from "lucide-react";
import { Card } from "./Card";

interface ScanOverlayProps {
  imageUrl: string | null;
  sampleLabel: string | null;
}

export function ScanOverlay({ imageUrl, sampleLabel }: ScanOverlayProps) {
  const [imgFailed, setImgFailed] = useState(false);
  const showImage = imageUrl !== null && !imgFailed;

  return (
    <div className="flex flex-col gap-5 py-4" aria-live="polite" aria-busy="true">
      <div className="relative w-full h-[228px] rounded-[20px] border border-border bg-surface overflow-hidden card-shadow flex flex-col items-center justify-center">
        {showImage ? (
          <img
            src={imageUrl}
            alt=""
            onError={() => setImgFailed(true)}
            className="w-full h-full object-contain bg-bg"
          />
        ) : (
          <div className="w-full h-full bg-bg flex flex-col items-center justify-center gap-2 p-4 text-center">
            <FileText className="w-10 h-10 text-ink-muted" aria-hidden="true" />
            {sampleLabel && (
              <span className="text-body text-ink-muted">{sampleLabel}</span>
            )}
          </div>
        )}
        <div
          aria-hidden="true"
          className="absolute top-0 left-0 right-0 h-[3px] bg-primary animate-scan-sweep"
        />
      </div>

      <p className="text-h2 text-ink text-center m-0">
        Binabasa ang reseta...
      </p>

      <Card className="flex flex-col gap-3" aria-hidden="true">
        <div className="h-5 w-3/4 rounded-[8px] bg-border" />
        <div className="h-5 w-1/2 rounded-[8px] bg-border" />
        <div className="h-5 w-5/6 rounded-[8px] bg-border" />
      </Card>
    </div>
  );
}
