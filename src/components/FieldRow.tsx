// src/components/FieldRow.tsx
import React from "react";

interface FieldRowProps {
  label: string;
  children: React.ReactNode;
}

export function FieldRow({ label, children }: FieldRowProps) {
  return (
    <div className="flex flex-col gap-1 py-2 border-b border-border last:border-b-0">
      <span className="text-body text-ink-muted">{label}</span>
      <div className="text-body-strong text-ink">{children}</div>
    </div>
  );
}
