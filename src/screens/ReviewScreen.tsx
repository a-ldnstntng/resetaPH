// src/screens/ReviewScreen.tsx
import { AlertTriangle, Sparkles } from "lucide-react";
import type { ScanResult, PrescriptionItem } from "../lib/types";
import { useLanguage } from "../context/LanguageContext";
import { StepIndicator } from "../components/StepIndicator";
import { Card } from "../components/Card";
import { FieldRow } from "../components/FieldRow";
import { UncertainField } from "../components/UncertainField";
import { PrimaryButton } from "../components/PrimaryButton";
import { SecondaryButton } from "../components/SecondaryButton";
import { Chip } from "../components/Chip";
import { EmptyState } from "../components/EmptyState";

interface ReviewScreenProps {
  scanResult: ScanResult;
  hasLineCountMismatch?: boolean;
  onUpdateItem: (itemId: string, updater: (item: PrescriptionItem) => PrescriptionItem) => void;
  onConfirm: () => void;
  onRescan: () => void;
  onDeepScan?: () => void;
}

export function ReviewScreen({
  scanResult,
  hasLineCountMismatch = false,
  onUpdateItem,
  onConfirm,
  onRescan,
  onDeepScan,
}: ReviewScreenProps) {
  const { t, getNote } = useLanguage();

  const formatHowToTake = (item: PrescriptionItem): string => {
    return t.howToTakeSummary(item.dosesPerDay, item.durationDays);
  };

  return (
    <main className="flex-1 flex flex-col gap-5 pb-6">
      <StepIndicator currentStep={2} />

      <header className="flex flex-col gap-2">
        <div className="flex items-center justify-between gap-2 flex-wrap">
          <h1 className="text-h1 text-ink m-0">{t.reviewHeading}</h1>
          {scanResult.readScore !== null && (
            <Chip tone="primary">{`${scanResult.readScore}%`}</Chip>
          )}
        </div>
        <p className="text-body text-ink-muted m-0">
          {t.reviewSubheading}
        </p>
      </header>

      {hasLineCountMismatch && (
        <EmptyState
          tone="warn"
          icon={<AlertTriangle className="w-6 h-6" />}
          message={t.lineCountMismatch}
        />
      )}

      {onDeepScan && (hasLineCountMismatch || scanResult.items.some((i) => i.uncertainFields.length > 0)) && (
        <div className="rounded-[16px] bg-primary-soft border border-border p-4 flex flex-col gap-2">
          <div className="flex items-center gap-2">
            <Sparkles className="w-5 h-5 text-primary shrink-0" />
            <span className="text-body-strong text-ink">{t.deepScanTitle}</span>
          </div>
          <p className="text-body text-ink-muted m-0">
            {t.deepScanDescription}
          </p>
          <button
            type="button"
            onClick={onDeepScan}
            className="mt-1 w-full min-h-[44px] rounded-[12px] bg-primary text-surface text-body-strong flex items-center justify-center gap-2 cursor-pointer hover:opacity-95 transition-opacity"
          >
            <Sparkles className="w-4 h-4" />
            <span>{t.btnDeepScan}</span>
          </button>
        </div>
      )}

      <Card className="flex flex-col gap-2">
        <div className="grid grid-cols-3 gap-2 text-body">
          <div className="flex flex-col">
            <span className="text-ink-muted">{t.doctorLabel}</span>
            <span className={scanResult.doctorName ? "text-body-strong text-ink" : "text-ink-muted"}>
              {scanResult.doctorName ?? t.notDetected}
            </span>
          </div>
          <div className="flex flex-col">
            <span className="text-ink-muted">{t.prcLabel}</span>
            <span className={scanResult.prcNumber ? "text-body-strong text-ink" : "text-ink-muted"}>
              {scanResult.prcNumber ?? t.notDetected}
            </span>
          </div>
          <div className="flex flex-col">
            <span className="text-ink-muted">{t.dateLabel}</span>
            <span className={scanResult.dateOnSlip ? "text-body-strong text-ink" : "text-ink-muted"}>
              {scanResult.dateOnSlip ?? t.notDetected}
            </span>
          </div>
        </div>
      </Card>

      <div className="flex flex-col gap-4">
        {scanResult.items.map((item) => {
          const isGenericUncertain = item.uncertainFields.includes("genericName");
          const isStrengthUncertain = item.uncertainFields.includes("strength");
          const isQuantityUncertain = item.uncertainFields.includes("quantity");
          const isSigUncertain = item.uncertainFields.includes("sig");
          const noteText = getNote(item);
          const isWarning =
            Boolean(noteText) &&
            (noteText!.includes("BABALA") || noteText!.includes("WARNING"));

          return (
            <Card key={item.id} className="flex flex-col gap-4">
              <div className="flex flex-col gap-1.5">
                <span className="text-body-strong text-ink">
                  {t.writtenOnRx}
                </span>
                <div className="rounded-[12px] bg-bg border border-border p-3.5 flex flex-col gap-1">
                  <p className="text-body-strong text-ink m-0">{item.writtenAs}</p>
                  <p className="text-body text-ink-muted m-0">{item.sigRaw}</p>
                </div>
              </div>

              <div className="flex flex-col gap-1">
                <span className="text-body-strong text-primary">
                  {t.meaningHeading}
                </span>

                {isGenericUncertain ? (
                  <UncertainField
                    label={t.fieldGenericName}
                    value={item.genericName}
                    onChange={(val) =>
                      onUpdateItem(item.id, (prev) => ({
                        ...prev,
                        genericName: val,
                      }))
                    }
                  />
                ) : (
                  <FieldRow label={t.fieldGenericName}>
                    {item.genericName}
                  </FieldRow>
                )}

                {isStrengthUncertain ? (
                  <UncertainField
                    label={t.fieldStrength}
                    value={item.strength}
                    onChange={(val) =>
                      onUpdateItem(item.id, (prev) => ({
                        ...prev,
                        strength: val,
                      }))
                    }
                  />
                ) : (
                  <FieldRow label={t.fieldStrength}>{item.strength}</FieldRow>
                )}

                {isQuantityUncertain ? (
                  <UncertainField
                    label={t.fieldQuantity}
                    value={String(item.quantity)}
                    displaySuffix={item.form}
                    inputType="number"
                    onChange={(val) => {
                      const parsed = Number.parseInt(val, 10);
                      if (!Number.isNaN(parsed) && parsed > 0) {
                        onUpdateItem(item.id, (prev) => ({
                          ...prev,
                          quantity: parsed,
                        }));
                      }
                    }}
                  />
                ) : (
                  <FieldRow label={t.fieldQuantity}>
                    {`${item.quantity} ${item.form}`}
                  </FieldRow>
                )}

                {isSigUncertain ? (
                  <UncertainField
                    label={t.fieldHowToTake}
                    value={formatHowToTake(item)}
                    onChange={(val) =>
                      onUpdateItem(item.id, (prev) => ({
                        ...prev,
                        sigRaw: val,
                      }))
                    }
                  />
                ) : (
                  <FieldRow label={t.fieldHowToTake}>
                    {formatHowToTake(item)}
                  </FieldRow>
                )}

                {noteText && (
                  <div
                    className={`mt-2 rounded-[12px] p-3 text-caption border border-border ${
                      isWarning
                        ? "bg-warn-bg text-warn-ink"
                        : "bg-primary-soft text-ink"
                    }`}
                  >
                    <span className="font-semibold block mb-0.5">
                      {isWarning ? t.safetyWarningTitle : t.medReminderTitle}
                    </span>
                    <span>{noteText}</span>
                  </div>
                )}
              </div>
            </Card>
          );
        })}
      </div>

      <div className="flex flex-col gap-3 pt-2">
        <PrimaryButton onClick={onConfirm}>
          {t.btnConfirmReview}
        </PrimaryButton>
        <SecondaryButton variant="text" onClick={onRescan}>
          {t.scanAgain}
        </SecondaryButton>
      </div>
    </main>
  );
}
