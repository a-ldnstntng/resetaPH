// src/components/BranchCard.tsx
import { useState } from "react";
import { Phone, Copy, Check, CheckCircle2, PhoneCall } from "lucide-react";
import type { Branch, PrescriptionItem } from "../lib/types";
import { formatDistance } from "../lib/geo";
import { Card } from "./Card";
import { Chip } from "./Chip";
import { SecondaryButton } from "./SecondaryButton";

interface BranchCardProps {
  branch: Branch;
  distanceMeters: number;
  firstItem: PrescriptionItem | undefined;
}

export function buildInquiryText(item: PrescriptionItem): string {
  return `Magandang araw po! Mayroon po ba kayong ${item.genericName} ${item.strength}, ${item.quantity} na ${item.form}?`;
}

export function BranchCard({
  branch,
  distanceMeters,
  firstItem,
}: BranchCardProps) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    if (!firstItem) return;
    const text = buildInquiryText(firstItem);
    try {
      // VERIFY: navigator.clipboard.writeText standard promise API
      if (navigator.clipboard && typeof navigator.clipboard.writeText === "function") {
        await navigator.clipboard.writeText(text);
      }
      setCopied(true);
      window.setTimeout(() => {
        setCopied(false);
      }, 2000);
    } catch {
      setCopied(true);
      window.setTimeout(() => {
        setCopied(false);
      }, 2000);
    }
  };

  return (
    <Card className="flex flex-col gap-3">
      <div className="flex items-start justify-between gap-2 flex-wrap">
        <div className="flex items-center gap-2 flex-wrap">
          <h3 className="text-h2 text-ink m-0">{branch.name}</h3>
          {branch.isSample && <Chip tone="neutral">Sample</Chip>}
        </div>
      </div>

      <div className="flex flex-col gap-1">
        <span className="text-body-strong text-ink tabular-nums">
          {formatDistance(distanceMeters)}
        </span>
        <span className="text-caption text-ink-muted">
          humigit-kumulang, tuwid na linya
        </span>
      </div>

      {branch.address !== null && (
        <p className="text-body text-ink m-0">{branch.address}</p>
      )}

      {branch.stockTier === "high" && (
        <div>
          <Chip
            tone="primary"
            icon={<CheckCircle2 className="w-4 h-4 text-primary" />}
          >
            Karaniwang may stock
          </Chip>
        </div>
      )}

      {branch.stockTier === "call" && (
        <div>
          <Chip
            tone="warn"
            icon={<PhoneCall className="w-4 h-4 text-warn-ink" />}
          >
            Tumawag para makumpirma
          </Chip>
        </div>
      )}

      <div className="flex flex-col gap-2.5 pt-1">
        {branch.phone !== null ? (
          <a
            href={`tel:${branch.phone}`}
            className="w-full min-h-[56px] px-5 py-3 rounded-[16px] bg-primary text-surface text-body-strong flex items-center justify-center gap-2 no-underline"
          >
            <Phone className="w-5 h-5" aria-hidden="true" />
            <span>Tawagan</span>
          </a>
        ) : (
          <div className="flex flex-col gap-1">
            <SecondaryButton
              disabled
              icon={<Phone className="w-5 h-5" />}
              aria-label="Tawagan (Walang numero pa)"
            >
              Tawagan
            </SecondaryButton>
            <span className="text-body text-ink-muted text-center">
              Walang numero pa
            </span>
          </div>
        )}

        <SecondaryButton
          onClick={handleCopy}
          disabled={!firstItem}
          icon={
            copied ? (
              <Check className="w-5 h-5 text-primary" />
            ) : (
              <Copy className="w-5 h-5" />
            )
          }
        >
          {copied ? "Nakopya" : "Kopyahin ang tanong"}
        </SecondaryButton>
      </div>
    </Card>
  );
}
