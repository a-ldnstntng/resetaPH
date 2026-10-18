// src/components/BranchCard.tsx
import { useState } from "react";
import { Phone, Copy, Check, CheckCircle2, PhoneCall } from "lucide-react";
import type { Branch, PrescriptionItem } from "../lib/types";
import { formatDistance } from "../lib/geo";
import { useLanguage } from "../context/LanguageContext";
import type { Language } from "../lib/i18n";
import { Card } from "./Card";
import { Chip } from "./Chip";
import { SecondaryButton } from "./SecondaryButton";

interface BranchCardProps {
  branch: Branch;
  distanceMeters: number;
  items?: PrescriptionItem[];
  firstItem?: PrescriptionItem;
}

export function buildInquiryText(
  itemsOrFirst: PrescriptionItem[] | PrescriptionItem | undefined,
  language: Language = "en"
): string {
  if (!itemsOrFirst) return "";
  const items = Array.isArray(itemsOrFirst) ? itemsOrFirst : [itemsOrFirst];
  if (items.length === 0) return "";

  if (language === "en") {
    if (items.length === 1) {
      const item = items[0];
      return `Good day! Do you have available stock for ${item.genericName} ${item.strength}, ${item.quantity} ${item.form}? Thank you!`;
    }
    const lines = items.map((i) => `- ${i.genericName} ${i.strength} (${i.quantity} ${i.form})`).join("\n");
    return `Good day! Do you have available stock for these medications:\n${lines}\nThank you!`;
  }

  if (items.length === 1) {
    const item = items[0];
    return `Magandang araw po! Mayroon po ba kayong ${item.genericName} ${item.strength}, ${item.quantity} na ${item.form}?`;
  }
  const lines = items.map((i) => `- ${i.genericName} ${i.strength} (${i.quantity} ${i.form})`).join("\n");
  return `Magandang araw po! Mayroon po ba kayong stock ng mga gamot na ito:\n${lines}\nMaraming salamat po!`;
}

export function BranchCard({
  branch,
  distanceMeters,
  items,
  firstItem,
}: BranchCardProps) {
  const { language, t } = useLanguage();
  const [copied, setCopied] = useState(false);
  const activeItems = items && items.length > 0 ? items : firstItem ? [firstItem] : [];

  const handleCopy = async () => {
    if (activeItems.length === 0) return;
    const text = buildInquiryText(activeItems, language);
    try {
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
          {t.distanceNote}
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
            {t.usuallyInStock}
          </Chip>
        </div>
      )}

      {branch.stockTier === "call" && (
        <div>
          <Chip
            tone="warn"
            icon={<PhoneCall className="w-4 h-4 text-warn-ink" />}
          >
            {t.callToConfirm}
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
            <span>{t.btnCall}</span>
          </a>
        ) : (
          <div className="flex flex-col gap-1">
            <SecondaryButton
              disabled
              icon={<Phone className="w-5 h-5" />}
              aria-label={`${t.btnCall} (${t.noPhoneYet})`}
            >
              {t.btnCall}
            </SecondaryButton>
            <span className="text-body text-ink-muted text-center">
              {t.noPhoneYet}
            </span>
          </div>
        )}

        <SecondaryButton
          onClick={handleCopy}
          disabled={activeItems.length === 0}
          icon={
            copied ? (
              <Check className="w-5 h-5 text-primary" />
            ) : (
              <Copy className="w-5 h-5" />
            )
          }
        >
          {copied ? t.btnCopied : t.btnCopyInquiry}
        </SecondaryButton>
      </div>
    </Card>
  );
}
