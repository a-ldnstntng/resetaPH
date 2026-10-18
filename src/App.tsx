// src/App.tsx
import { useEffect, useState } from "react";
import type { PrescriptionItem, SamplePrescription, ScanResult } from "./lib/types";
import { SAMPLE_SCAN_INFECTION } from "./lib/sample-data";
import { analyzePrescription, cloneScanResult } from "./lib/scan";
import { AppShell } from "./components/AppShell";
import { ScanOverlay } from "./components/ScanOverlay";
import { CounterMode } from "./components/CounterMode";
import { EmptyState } from "./components/EmptyState";
import { SecondaryButton } from "./components/SecondaryButton";
import { HomeScreen } from "./screens/HomeScreen";
import { ReviewScreen } from "./screens/ReviewScreen";
import { ResultsScreen } from "./screens/ResultsScreen";

export type ScreenState = "home" | "scanning" | "review" | "results";

export default function App() {
  const [screen, setScreen] = useState<ScreenState>("home");
  const [counterOpen, setCounterOpen] = useState<boolean>(false);
  const [scanResult, setScanResult] = useState<ScanResult>(() =>
    cloneScanResult(SAMPLE_SCAN_INFECTION)
  );
  const [hasLineCountMismatch, setHasLineCountMismatch] = useState<boolean>(false);
  const [scanImageUrl, setScanImageUrl] = useState<string | null>(null);
  const [scanSampleLabel, setScanSampleLabel] = useState<string | null>(null);
  const [objectUrlToRevoke, setObjectUrlToRevoke] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [screen]);

  useEffect(() => {
    return () => {
      if (objectUrlToRevoke) {
        URL.revokeObjectURL(objectUrlToRevoke);
      }
    };
  }, [objectUrlToRevoke]);

  const handleStartFileScan = (file: File) => {
    if (objectUrlToRevoke) {
      URL.revokeObjectURL(objectUrlToRevoke);
    }
    const url = URL.createObjectURL(file);
    setObjectUrlToRevoke(url);
    setScanImageUrl(url);
    setScanSampleLabel(null);
    setErrorMessage(null);
    setHasLineCountMismatch(false);
    setScreen("scanning");

    analyzePrescription(file)
      .then((outcome) => {
        setScanResult(outcome.scanResult);
        setHasLineCountMismatch(outcome.hasLineCountMismatch);
        setScreen("review");
      })
      .catch(() => {
        setErrorMessage("May nangyaring problema. Subukan ulit.");
        setScreen("home");
      });
  };

  const handleStartSampleScan = (sample: SamplePrescription) => {
    if (!sample.data) return;
    if (objectUrlToRevoke) {
      URL.revokeObjectURL(objectUrlToRevoke);
      setObjectUrlToRevoke(null);
    }
    setScanImageUrl(sample.imageSrc);
    setScanSampleLabel(sample.label);
    setErrorMessage(null);
    setHasLineCountMismatch(false);
    setScreen("scanning");

    analyzePrescription(sample.data)
      .then((outcome) => {
        setScanResult(outcome.scanResult);
        setHasLineCountMismatch(outcome.hasLineCountMismatch);
        setScreen("review");
      })
      .catch(() => {
        setErrorMessage("May nangyaring problema. Subukan ulit.");
        setScreen("home");
      });
  };

  const handleUpdateItem = (
    itemId: string,
    updater: (item: PrescriptionItem) => PrescriptionItem
  ) => {
    setScanResult((prev) => ({
      ...prev,
      items: prev.items.map((item) => (item.id === itemId ? updater(item) : item)),
    }));
  };

  const handleRescan = () => {
    setCounterOpen(false);
    setHasLineCountMismatch(false);
    setScreen("home");
  };

  return (
    <AppShell>
      {errorMessage && (
        <div className="mb-4 flex flex-col gap-2">
          <EmptyState tone="danger" message={errorMessage} />
          <SecondaryButton variant="outline" onClick={() => setErrorMessage(null)}>
            Mag-scan ulit
          </SecondaryButton>
        </div>
      )}

      {screen === "home" && (
        <HomeScreen
          onStartFileScan={handleStartFileScan}
          onStartSampleScan={handleStartSampleScan}
        />
      )}

      {screen === "scanning" && (
        <ScanOverlay imageUrl={scanImageUrl} sampleLabel={scanSampleLabel} />
      )}

      {screen === "review" && (
        <ReviewScreen
          scanResult={scanResult}
          hasLineCountMismatch={hasLineCountMismatch}
          onUpdateItem={handleUpdateItem}
          onConfirm={() => setScreen("results")}
          onRescan={handleRescan}
        />
      )}

      {screen === "results" && (
        <ResultsScreen
          scanResult={scanResult}
          onOpenCounterMode={() => setCounterOpen(true)}
          onRescan={handleRescan}
        />
      )}

      {counterOpen && (
        <CounterMode
          items={scanResult.items}
          onClose={() => setCounterOpen(false)}
        />
      )}
    </AppShell>
  );
}
