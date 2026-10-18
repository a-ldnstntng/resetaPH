import React, { useRef } from "react";
import { Camera, Upload, History, Trash2, ArrowRight } from "lucide-react";
import type { SamplePrescription } from "../lib/types";
import type { SavedScan } from "../lib/history";
import { SAMPLES } from "../lib/sample-data";
import { PrimaryButton } from "../components/PrimaryButton";
import { SecondaryButton } from "../components/SecondaryButton";
import { SampleCard } from "../components/SampleCard";

interface HomeScreenProps {
  onStartFileScan: (file: File) => void;
  onStartSampleScan: (sample: SamplePrescription) => void;
  history: SavedScan[];
  onSelectHistoryScan: (scan: SavedScan) => void;
  onClearHistory: () => void;
}

export function HomeScreen({
  onStartFileScan,
  onStartSampleScan,
  history,
  onSelectHistoryScan,
  onClearHistory,
}: HomeScreenProps) {
  const cameraInputRef = useRef<HTMLInputElement>(null);
  const uploadInputRef = useRef<HTMLInputElement>(null);

  const readySamples = SAMPLES.filter((s) => s.data !== null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      onStartFileScan(file);
    }
    e.target.value = "";
  };

  return (
    <main className="flex-1 flex flex-col justify-between gap-8 py-2">
      <div className="flex flex-col gap-6">
        <header>
          <span className="text-h2 font-extrabold text-primary tracking-tight">
            ResetaPH
          </span>
        </header>

        <section className="flex flex-col gap-3 pt-4">
          <h1 className="text-h1 text-ink m-0">
            I-scan ang reseta mo
          </h1>
          <p className="text-body text-ink-muted m-0">
            Alamin ang tamang gamot, tinatayang presyo, at paano inumin.
          </p>
        </section>

        <section className="flex flex-col gap-3 pt-2">
          <input
            ref={cameraInputRef}
            type="file"
            accept="image/*"
            capture="environment"
            onChange={handleFileChange}
            className="sr-only"
            tabIndex={-1}
            aria-hidden="true"
          />
          <input
            ref={uploadInputRef}
            type="file"
            accept="image/*"
            onChange={handleFileChange}
            className="sr-only"
            tabIndex={-1}
            aria-hidden="true"
          />

          <PrimaryButton
            icon={<Camera className="w-6 h-6" />}
            onClick={() => cameraInputRef.current?.click()}
          >
            I-scan ang Reseta
          </PrimaryButton>

          <SecondaryButton
            variant="outline"
            icon={<Upload className="w-6 h-6" />}
            onClick={() => uploadInputRef.current?.click()}
          >
            Mag-upload ng Larawan
          </SecondaryButton>
        </section>

        {history.length > 0 && (
          <section className="flex flex-col gap-3 pt-2">
            <div className="flex items-center justify-between">
              <h2 className="text-body-strong text-ink m-0 flex items-center gap-2">
                <History className="w-5 h-5 text-primary" />
                <span>Naka-save sa Device</span>
              </h2>
              <button
                type="button"
                onClick={onClearHistory}
                className="text-caption text-ink-muted hover:text-danger-ink flex items-center gap-1 cursor-pointer"
                title="Burahin ang lokal na history"
              >
                <Trash2 className="w-3.5 h-3.5" />
                <span>Burahin</span>
              </button>
            </div>

            <div className="flex flex-col gap-2.5">
              {history.map((item) => (
                <button
                  key={item.id}
                  type="button"
                  onClick={() => onSelectHistoryScan(item)}
                  className="w-full text-left p-3.5 rounded-[16px] border border-border bg-surface card-shadow hover:bg-bg transition-colors flex items-center justify-between gap-3 cursor-pointer"
                >
                  <div className="flex flex-col gap-0.5 min-w-0">
                    <span className="text-body-strong text-ink truncate">
                      {item.itemSummary}
                    </span>
                    <span className="text-caption text-ink-muted">
                      {item.doctorName ? `${item.doctorName} · ` : ""}
                      {item.itemCount} gamot
                    </span>
                  </div>
                  <ArrowRight className="w-4 h-4 text-ink-muted shrink-0" />
                </button>
              ))}
            </div>
          </section>
        )}

        {readySamples.length > 0 && (
          <section className="flex flex-col gap-3 pt-4">
            <h2 className="text-body-strong text-ink m-0">
              Subukan ang sample na reseta
            </h2>
            <div className="flex flex-col gap-3">
              {readySamples.map((sample) => (
                <SampleCard
                  key={sample.id}
                  sample={sample}
                  onSelect={onStartSampleScan}
                />
              ))}
            </div>
          </section>
        )}
      </div>

      <footer className="pt-6 border-t border-border">
        <p className="text-body text-ink-muted m-0">
          Gabay lang ito. Hindi kapalit ng payo ng doktor o parmasyutiko.
        </p>
      </footer>
    </main>
  );
}
