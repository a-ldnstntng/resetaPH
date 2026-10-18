// src/components/Card.tsx
import React from "react";
import clsx from "clsx";

interface CardProps {
  children: React.ReactNode;
  className?: string;
  variant?: "surface" | "primary-soft" | "warn" | "danger";
}

export function Card({ children, className, variant = "surface" }: CardProps) {
  const variantClasses: Record<NonNullable<CardProps["variant"]>, string> = {
    surface: "bg-surface border-border text-ink",
    "primary-soft": "bg-primary-soft border-border text-ink",
    warn: "bg-warn-bg border-border text-warn-ink",
    danger: "bg-danger-bg border-border text-danger-ink",
  };

  return (
    <div
      className={clsx(
        "rounded-[20px] border p-5 card-shadow animate-card-entry",
        variantClasses[variant],
        className
      )}
    >
      {children}
    </div>
  );
}
