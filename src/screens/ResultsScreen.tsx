// src/screens/ResultsScreen.tsx
import { useEffect, useState } from "react";
import { IdCard, WifiOff, MapPin } from "lucide-react";
import type { ScanResult } from "../lib/types";
import { DEFAULT_CENTER, SAMPLE_BRANCHES } from "../lib/sample-data";
import { computeBasketSummary } from "../lib/pricing";
import { haversineMeters, type LatLng } from "../lib/geo";
import { StepIndicator } from "../components/StepIndicator";
import { TabBar, type ResultsTab } from "../components/TabBar";
import { SavingsHero } from "../components/SavingsHero";
import { ItemPriceCard } from "../components/ItemPriceCard";
import { ScheduleTimeline } from "../components/ScheduleTimeline";
import { ListenButton } from "../components/ListenButton";
import { PharmacyMap } from "../components/PharmacyMap";
import { BranchCard } from "../components/BranchCard";
import { Chip } from "../components/Chip";
import { PrimaryButton } from "../components/PrimaryButton";
import { SecondaryButton } from "../components/SecondaryButton";
import { EmptyState } from "../components/EmptyState";

interface ResultsScreenProps {
  scanResult: ScanResult;
  onOpenCounterMode: () => void;
  onRescan: () => void;
}

export function ResultsScreen({
  scanResult,
  onOpenCounterMode,
  onRescan,
}: ResultsScreenProps) {
  const [tab, setTab] = useState<ResultsTab>("presyo");
  const [scaleScheduleText, setScaleScheduleText] = useState(false);
  const [filter24h, setFilter24h] = useState(false);
  const [userCenter, setUserCenter] = useState<LatLng>(DEFAULT_CENTER);
  const [locationDenied, setLocationDenied] = useState(false);
  const [isOnline, setIsOnline] = useState<boolean>(() =>
    typeof navigator !== "undefined" ? navigator.onLine : true
  );

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [tab]);

  useEffect(() => {
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);
    window.addEventListener("online", handleOnline);
    window.addEventListener("offline", handleOffline);
    return () => {
      window.removeEventListener("online", handleOnline);
      window.removeEventListener("offline", handleOffline);
    };
  }, []);

  useEffect(() => {
    if (tab !== "botika") return;

    // VERIFY: navigator.geolocation.getCurrentPosition standard browser call
    if (typeof navigator !== "undefined" && "geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          setUserCenter({
            lat: pos.coords.latitude,
            lng: pos.coords.longitude,
          });
          setLocationDenied(false);
        },
        () => {
          setUserCenter(DEFAULT_CENTER);
          setLocationDenied(true);
        },
        { timeout: 5000 }
      );
    } else {
      setUserCenter(DEFAULT_CENTER);
      setLocationDenied(true);
    }
  }, [tab]);

  const basketSummary = computeBasketSummary(scanResult.items);

  const branchesWithDistance = SAMPLE_BRANCHES.filter((b) =>
    filter24h ? b.open24h === true : true
  )
    .map((branch) => ({
      branch,
      distanceMeters: haversineMeters(userCenter, {
        lat: branch.lat,
        lng: branch.lng,
      }),
    }))
    .sort((a, b) => a.distanceMeters - b.distanceMeters);

  return (
    <main className="flex-1 flex flex-col pb-28">
      <StepIndicator currentStep={3} />

      <TabBar activeTab={tab} onSelectTab={setTab} />

      {tab === "presyo" && (
        <section
          id="tabpanel-presyo"
          role="tabpanel"
          aria-labelledby="tab-presyo"
          className="flex flex-col gap-4 pt-2"
        >
          <SavingsHero summary={basketSummary} />

          <div className="flex flex-col gap-4">
            {scanResult.items.map((item) => (
              <ItemPriceCard key={item.id} item={item} />
            ))}
          </div>

          <div className="pt-2">
            <SecondaryButton variant="text" onClick={onRescan}>
              Mag-scan ulit
            </SecondaryButton>
          </div>
        </section>
      )}

      {tab === "inumin" && (
        <section
          id="tabpanel-inumin"
          role="tabpanel"
          aria-labelledby="tab-inumin"
          className="flex flex-col gap-4 pt-2"
        >
          <div className="flex items-center justify-between gap-2">
            <h1 className="text-h1 text-ink m-0">Paano inumin</h1>
            <button
              type="button"
              onClick={() => setScaleScheduleText((prev) => !prev)}
              aria-pressed={scaleScheduleText}
              aria-label="A A+"
              className="min-h-[48px] min-w-[64px] px-3 py-2 rounded-[12px] border border-border bg-surface text-body-strong text-ink flex items-center justify-center gap-1.5 cursor-pointer"
            >
              <span>A</span>
              <span className="text-primary">A+</span>
            </button>
          </div>

          <ListenButton items={scanResult.items} />

          <ScheduleTimeline
            items={scanResult.items}
            scaled={scaleScheduleText}
          />
        </section>
      )}

      {tab === "botika" && (
        <section
          id="tabpanel-botika"
          role="tabpanel"
          aria-labelledby="tab-botika"
          className="flex flex-col gap-4 pt-2"
        >
          <h1 className="text-h1 text-ink m-0">Mga botika sa malapit</h1>

          {!isOnline ? (
            <EmptyState
              tone="warn"
              icon={<WifiOff className="w-6 h-6" />}
              message="Walang internet. Hindi makikita ang mapa."
            />
          ) : (
            <PharmacyMap
              center={userCenter}
              branches={branchesWithDistance.map((b) => b.branch)}
            />
          )}

          {locationDenied && (
            <EmptyState
              tone="neutral"
              icon={<MapPin className="w-6 h-6" />}
              message="Hindi namin makuha ang lokasyon mo. Ipinapakita ang paligid ng DLSU Manila."
            />
          )}

          <div>
            <Chip
              onClick={() => setFilter24h((prev) => !prev)}
              active={filter24h}
            >
              Bukas 24 oras
            </Chip>
          </div>

          <div className="flex flex-col gap-4">
            {branchesWithDistance.map(({ branch, distanceMeters }) => (
              <BranchCard
                key={branch.id}
                branch={branch}
                distanceMeters={distanceMeters}
                firstItem={scanResult.items[0]}
              />
            ))}
          </div>
        </section>
      )}

      <div className="fixed bottom-0 left-0 right-0 z-30 flex justify-center pointer-events-none px-5 pb-5 pt-2">
        <div className="w-full max-w-[440px] pointer-events-auto">
          <PrimaryButton
            onClick={onOpenCounterMode}
            icon={<IdCard className="w-6 h-6" />}
            className="card-shadow"
          >
            Ipakita sa Botika
          </PrimaryButton>
        </div>
      </div>
    </main>
  );
}
