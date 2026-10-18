// src/components/StepIndicator.tsx
import clsx from "clsx";
import { Check } from "lucide-react";
import { useLanguage } from "../context/LanguageContext";

interface StepIndicatorProps {
  currentStep: 1 | 2 | 3;
}

export function StepIndicator({ currentStep }: StepIndicatorProps) {
  const { t } = useLanguage();

  const steps: Array<{ step: 1 | 2 | 3; label: string }> = [
    { step: 1, label: t.stepScan },
    { step: 2, label: t.stepReview },
    { step: 3, label: t.stepResults },
  ];

  return (
    <nav aria-label="Progress" className="w-full mb-5">
      <ol className="grid grid-cols-3 gap-2">
        {steps.map(({ step, label }) => {
          const isCompleted = step < currentStep;
          const isCurrent = step === currentStep;
          return (
            <li
              key={step}
              aria-current={isCurrent ? "step" : undefined}
              className="flex flex-col gap-1.5"
            >
              <div
                className={clsx(
                  "h-1.5 w-full rounded-[999px] transition-colors duration-150 ease-out",
                  isCompleted || isCurrent ? "bg-primary" : "bg-border"
                )}
              />
              <div className="flex items-center gap-1.5">
                {isCompleted ? (
                  <Check
                    className="w-4 h-4 text-primary shrink-0"
                    aria-hidden="true"
                  />
                ) : (
                  <span
                    className={clsx(
                      "text-body-strong",
                      isCurrent ? "text-primary" : "text-ink-muted"
                    )}
                  >
                    {step}.
                  </span>
                )}
                <span
                  className={clsx(
                    isCurrent ? "text-body-strong text-ink" : "text-body text-ink-muted"
                  )}
                >
                  {label}
                </span>
              </div>
            </li>
          );
        })}
      </ol>
    </nav>
  );
}
