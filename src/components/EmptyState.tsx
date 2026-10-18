// src/components/EmptyState.tsx
import React from "react";
import { Info } from "lucide-react";

interface EmptyStateProps {
  message: string;
  icon?: React.ReactNode;
  tone?: "neutral" | "warn" | "danger";
}

export function EmptyState({ message, icon, tone = "neutral" }: EmptyStateProps) {
  const toneClasses = {
    neutral: "bg-surface border-border text-ink-muted",
    warn: "bg-warn-bg border-border text-warn-ink",
    danger: "bg-danger-bg border-border text-danger-ink",
  };

  return (
    <div
      className={`rounded-[20px] border p-5 flex items-start gap-3 ${toneClasses[tone]}`}
    >
      <span aria-hidden="true" className="shrink-0 mt-0.5">
        {icon ?? <Info className="w-6 h-6" />}
      </span>
      <p className="text-body m-0">{message}</p>
    </div>
  );
}
