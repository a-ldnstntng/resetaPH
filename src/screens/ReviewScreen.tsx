// src/screens/ReviewScreen.tsx
import { AlertTriangle } from "lucide-react";
import type { ScanResult, PrescriptionItem } from "../lib/types";
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
}

export function ReviewScreen({
  scanResult,
  hasLineCountMismatch = false,
  onUpdateItem,
  onConfirm,
  onRescan,
}: ReviewScreenProps) {
  const formatHowToTake = (item: PrescriptionItem): string => {
    const parts: string[] = [`${item.dosesPerDay}x isang araw`];
    if (item.durationDays !== null) {
      parts.push(`${item.durationDays} araw`);
    }
    return parts.join(" · ");
  };

  return (
    <main className="flex-1 flex flex-col gap-5 pb-6">
      <StepIndicator currentStep={2} />

      <header className="flex flex-col gap-2">
        <div className="flex items-center justify-between gap-2 flex-wrap">
          <h1 className="text-h1 text-ink m-0">Ito ang nabasa namin</h1>
          {scanResult.readScore !== null && (
            <Chip tone="primary">{`${scanResult.readScore}%`}</Chip>
          )}
        </div>
        <p className="text-body text-ink-muted m-0">
          Pakisigurado kung tama bago magpatuloy.
        </p>
      </header>

      {hasLineCountMismatch && (
        <EmptyState
          tone="warn"
          icon={<AlertTriangle className="w-6 h-6" />}
          message="May hindi nabasa na gamot. Pakisigurado ang reseta."
        />
      )}

      <Card className="flex flex-col gap-2">
        <div className="grid grid-cols-3 gap-2 text-body">
          <div className="flex flex-col">
            <span className="text-ink-muted">Doktor</span>
            <span className={scanResult.doctorName ? "text-body-strong text-ink" : "text-ink-muted"}>
              {scanResult.doctorName ?? "Hindi nabasa"}
            </span>
          </div>
          <div className="flex flex-col">
            <span className="text-ink-muted">PRC</span>
            <span className={scanResult.prcNumber ? "text-body-strong text-ink" : "text-ink-muted"}>
              {scanResult.prcNumber ?? "Hindi nabasa"}
            </span>
          </div>
          <div className="flex flex-col">
            <span className="text-ink-muted">Petsa</span>
            <span className={scanResult.dateOnSlip ? "text-body-strong text-ink" : "text-ink-muted"}>
              {scanResult.dateOnSlip ?? "Hindi nabasa"}
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

          return (
            <Card key={item.id} className="flex flex-col gap-4">
              <div className="flex flex-col gap-1.5">
                <span className="text-body-strong text-ink">
                  Nakasulat sa reseta
                </span>
                <div className="rounded-[12px] bg-bg border border-border p-3.5 flex flex-col gap-1">
                  <p className="text-body-strong text-ink m-0">{item.writtenAs}</p>
                  <p className="text-body text-ink-muted m-0">{item.sigRaw}</p>
                </div>
              </div>

              <div className="flex flex-col gap-1">
                <span className="text-body-strong text-primary">
                  Ibig sabihin
                </span>

                {isGenericUncertain ? (
                  <UncertainField
                    label="Generic na pangalan"
                    value={item.genericName}
                    onChange={(val) =>
                      onUpdateItem(item.id, (prev) => ({
                        ...prev,
                        genericName: val,
                      }))
                    }
                  />
                ) : (
                  <FieldRow label="Generic na pangalan">
                    {item.genericName}
                  </FieldRow>
                )}

                {isStrengthUncertain ? (
                  <UncertainField
                    label="Lakas"
                    value={item.strength}
                    onChange={(val) =>
                      onUpdateItem(item.id, (prev) => ({
                        ...prev,
                        strength: val,
                      }))
                    }
                  />
                ) : (
                  <FieldRow label="Lakas">{item.strength}</FieldRow>
                )}

                {isQuantityUncertain ? (
                  <UncertainField
                    label="Dami"
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
                  <FieldRow label="Dami">
                    {`${item.quantity} ${item.form}`}
                  </FieldRow>
                )}

                {isSigUncertain ? (
                  <UncertainField
                    label="Paano inumin"
                    value={formatHowToTake(item)}
                    onChange={(val) =>
                      onUpdateItem(item.id, (prev) => ({
                        ...prev,
                        sigRaw: val,
                      }))
                    }
                  />
                ) : (
                  <FieldRow label="Paano inumin">
                    {formatHowToTake(item)}
                  </FieldRow>
                )}

                {item.noteTl && (
                  <div
                    className={`mt-2 rounded-[12px] p-3 text-caption border border-border ${
                      item.noteTl.includes("BABALA")
                        ? "bg-warn-bg text-warn-ink"
                        : "bg-primary-soft text-ink"
                    }`}
                  >
                    <span className="font-semibold block mb-0.5">
                      {item.noteTl.includes("BABALA") ? "Babala sa Kaligtasan:" : "Paalala sa Pag-inom:"}
                    </span>
                    <span>{item.noteTl}</span>
                  </div>
                )}
              </div>
            </Card>
          );
        })}
      </div>

      <div className="flex flex-col gap-3 pt-2">
        <PrimaryButton onClick={onConfirm}>
          Tama, ipakita ang presyo
        </PrimaryButton>
        <SecondaryButton variant="text" onClick={onRescan}>
          Mag-scan ulit
        </SecondaryButton>
      </div>
    </main>
  );
}
