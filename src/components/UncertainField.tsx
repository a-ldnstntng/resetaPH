// src/components/UncertainField.tsx
import { useState, useId } from "react";
import { AlertTriangle, Check } from "lucide-react";
import { Chip } from "./Chip";

interface UncertainFieldProps {
  label: string;
  value: string;
  displaySuffix?: string;
  inputType?: "text" | "number";
  onChange: (newValue: string) => void;
}

export function UncertainField({
  label,
  value,
  displaySuffix,
  inputType = "text",
  onChange,
}: UncertainFieldProps) {
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState(value);
  const inputId = useId();

  const handleOpenEdit = () => {
    setDraft(value);
    setEditing(true);
  };

  const handleSave = () => {
    const trimmed = draft.trim();
    if (trimmed.length > 0) {
      onChange(trimmed);
    }
    setEditing(false);
  };

  return (
    <div className="rounded-[16px] bg-warn-bg text-warn-ink p-4 my-2 border border-border flex flex-col gap-2">
      <div className="flex items-center justify-between gap-2 flex-wrap">
        <span className="text-body-strong text-warn-ink">{label}</span>
        <Chip
          tone="warn"
          icon={<AlertTriangle className="w-4 h-4 text-warn-ink" />}
        >
          Pakisigurado
        </Chip>
      </div>

      {editing ? (
        <div className="flex items-center gap-2 mt-1">
          <label htmlFor={inputId} className="sr-only">
            {label}
          </label>
          <input
            id={inputId}
            type={inputType}
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                handleSave();
              }
            }}
            autoFocus
            className="flex-1 min-h-[48px] px-3 py-2 rounded-[12px] bg-surface text-ink border border-border text-body-strong"
          />
          <button
            type="button"
            onClick={handleSave}
            aria-label={label}
            className="min-h-[48px] min-w-[48px] px-4 rounded-[12px] bg-primary text-surface text-body-strong flex items-center justify-center cursor-pointer"
          >
            <Check className="w-5 h-5" aria-hidden="true" />
          </button>
        </div>
      ) : (
        <button
          type="button"
          onClick={handleOpenEdit}
          className="w-full min-h-[48px] px-3 py-2 rounded-[12px] bg-surface/80 border border-border text-left text-body-strong text-warn-ink flex items-center justify-between gap-2 cursor-pointer"
        >
          <span>
            {value}
            {displaySuffix ? ` ${displaySuffix}` : ""}
          </span>
          <AlertTriangle className="w-5 h-5 text-warn-ink shrink-0" aria-hidden="true" />
        </button>
      )}

      <p className="text-body text-warn-ink m-0">
        Hindi kami sigurado dito. Itanong sa parmasyutiko.
      </p>
    </div>
  );
}
