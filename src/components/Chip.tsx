// src/components/Chip.tsx
import React from "react";
import clsx from "clsx";

interface ChipProps {
  children: React.ReactNode;
  icon?: React.ReactNode;
  tone?: "neutral" | "primary" | "warn" | "danger";
  onClick?: () => void;
  active?: boolean;
  className?: string;
}

export function Chip({
  children,
  icon,
  tone = "neutral",
  onClick,
  active = false,
  className,
}: ChipProps) {
  const toneClasses: Record<NonNullable<ChipProps["tone"]>, string> = {
    neutral: active
      ? "bg-primary text-surface border-primary"
      : "bg-surface text-ink border-border",
    primary: "bg-primary-soft text-primary border-border",
    warn: "bg-warn-bg text-warn-ink border-border",
    danger: "bg-danger-bg text-danger-ink border-border",
  };

  if (onClick) {
    return (
      <button
        type="button"
        onClick={onClick}
        aria-pressed={active}
        className={clsx(
          "min-h-[48px] px-4 py-2 rounded-[999px] border text-body-strong inline-flex items-center gap-2 transition-colors duration-150 ease-out cursor-pointer",
          toneClasses[tone],
          className
        )}
      >
        {icon && <span aria-hidden="true" className="shrink-0 flex items-center">{icon}</span>}
        <span>{children}</span>
      </button>
    );
  }

  return (
    <span
      className={clsx(
        "px-3 py-1 rounded-[999px] border text-body-strong inline-flex items-center gap-1.5",
        toneClasses[tone],
        className
      )}
    >
      {icon && <span aria-hidden="true" className="shrink-0 flex items-center">{icon}</span>}
      <span>{children}</span>
    </span>
  );
}
