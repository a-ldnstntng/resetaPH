// src/App.tsx
import { useEffect, useState } from "react";
import type { PrescriptionItem, SamplePrescription, ScanResult } from "./lib/types";
import { SAMPLE_SCAN_INFECTION } from "./lib/sample-data";
import { analyzePrescription, cloneScanResult } from "./lib/scan";
import {
  loadScanHistory,
  saveScanToHistory,
  clearAllScanHistory,
  type SavedScan,
} from "./lib/history";
import { LanguageProvider, useLanguage } from "./context/LanguageContext";
import { AppShell } from "./components/AppShell";
import { ScanOverlay } from "./components/ScanOverlay";
import { CounterMode } from "./components/CounterMode";
import { EmptyState } from "./components/EmptyState";
import { SecondaryButton } from "./components/SecondaryButton";
import { HomeScreen } from "./screens/HomeScreen";
import { ReviewScreen } from "./screens/ReviewScreen";
import { ResultsScreen } from "./screens/ResultsScreen";

export type ScreenState = "home" | "scanning" | "review" | "results";

function AppContent() {
  const { language, t } = useLanguage();
  const [screen, setScreen] = useState<ScreenState>("home");
  const [counterOpen, setCounterOpen] = useState<boolean>(false);
  const [history, setHistory] = useState<SavedScan[]>(() => loadScanHistory());
  const [scanResult, setScanResult] = useState<ScanResult>(() =>
    cloneScanResult(SAMPLE_SCAN_INFECTION)
  );
  const [currentFile, setCurrentFile] = useState<File | null>(null);
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
    setCurrentFile(file);
    setErrorMessage(null);
    setHasLineCountMismatch(false);
    setScreen("scanning");

    analyzePrescription(file, false, language)
      .then((outcome) => {
        setScanResult(outcome.scanResult);
        setHasLineCountMismatch(outcome.hasLineCountMismatch);
        setScreen("review");
      })
      .catch((err) => {
        const msg =
          err instanceof Error && err.message
            ? err.message
            : t.errorGeneric;
        setErrorMessage(msg);
        setScreen("home");
      });
  };

  const handleDeepScan = () => {
    if (!currentFile) return;
    setErrorMessage(null);
    setScreen("scanning");

    analyzePrescription(currentFile, true, language)
      .then((outcome) => {
        setScanResult(outcome.scanResult);
        setHasLineCountMismatch(outcome.hasLineCountMismatch);
        setScreen("review");
      })
      .catch((err) => {
        const msg =
          err instanceof Error && err.message
            ? err.message
            : t.errorDeepScan;
        setErrorMessage(msg);
        setScreen("review");
      });
  };

  const handleStartSampleScan = (sample: SamplePrescription) => {
    if (!sample.data) return;
    if (objectUrlToRevoke) {
      URL.revokeObjectURL(objectUrlToRevoke);
      setObjectUrlToRevoke(null);
    }
    setCurrentFile(null);
    setScanImageUrl(sample.imageSrc);
    setScanSampleLabel(sample.label);
    setErrorMessage(null);
    setHasLineCountMismatch(false);
    setScreen("scanning");

    analyzePrescription(sample.data, false, language)
      .then((outcome) => {
        setScanResult(outcome.scanResult);
        setHasLineCountMismatch(outcome.hasLineCountMismatch);
        setScreen("review");
      })
      .catch(() => {
        setErrorMessage(t.errorGeneric);
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
    setCurrentFile(null);
    setCounterOpen(false);
    setHasLineCountMismatch(false);
    setScreen("home");
  };

  const handleSelectHistoryScan = (saved: SavedScan) => {
    setScanResult(saved.scanResult);
    setHasLineCountMismatch(false);
    setScanImageUrl(null);
    setScanSampleLabel(null);
    setScreen("results");
  };

  const handleClearHistory = () => {
    clearAllScanHistory();
    setHistory([]);
  };

  const handleConfirmReview = () => {
    const updated = saveScanToHistory(scanResult);
    setHistory(updated);
    setScreen("results");
  };

  return (
    <AppShell onBrandClick={handleRescan}>
      {errorMessage && (
        <div className="mb-4 flex flex-col gap-2">
          <EmptyState tone="danger" message={errorMessage} />
          <SecondaryButton variant="outline" onClick={() => setErrorMessage(null)}>
            {t.scanAgain}
          </SecondaryButton>
        </div>
      )}

      {screen === "home" && (
        <HomeScreen
          onStartFileScan={handleStartFileScan}
          onStartSampleScan={handleStartSampleScan}
          history={history}
          onSelectHistoryScan={handleSelectHistoryScan}
          onClearHistory={handleClearHistory}
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
          onConfirm={handleConfirmReview}
          onRescan={handleRescan}
          onDeepScan={currentFile ? handleDeepScan : undefined}
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

export default function App() {
  return (
    <LanguageProvider>
      <AppContent />
    </LanguageProvider>
  );
}
