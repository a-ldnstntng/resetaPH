// src/components/ScheduleTimeline.tsx
import React from "react";
import { Sunrise, Sun, Moon, Info } from "lucide-react";
import type { PrescriptionItem, Slot } from "../lib/types";
import { Card } from "./Card";

interface ScheduleTimelineProps {
  items: PrescriptionItem[];
  scaled?: boolean;
}

const SLOT_META: Array<{
  slot: Slot;
  label: string;
  Icon: React.ComponentType<{ className?: string; "aria-hidden"?: boolean | "true" | "false" }>;
}> = [
  { slot: "umaga", label: "Umaga", Icon: Sunrise },
  { slot: "tanghali", label: "Tanghali", Icon: Sun },
  { slot: "gabi", label: "Gabi", Icon: Moon },
];

export function ScheduleTimeline({ items, scaled = false }: ScheduleTimelineProps) {
  const activeSlots = SLOT_META.filter(({ slot }) =>
    items.some((item) => item.slots.includes(slot))
  );

  const itemsWithNotes = items.filter((item) => item.noteTl !== null);

  const titleStyle = scaled
    ? { fontSize: "27.5px", lineHeight: "35px", fontWeight: 700 }
    : undefined;
  const bodyStyle = scaled
    ? { fontSize: "22.5px", lineHeight: "35px" }
    : undefined;
  const bodyStrongStyle = scaled
    ? { fontSize: "22.5px", lineHeight: "35px", fontWeight: 600 }
    : undefined;

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-col gap-4">
        {activeSlots.map(({ slot, label, Icon }) => {
          const slotItems = items.filter((item) => item.slots.includes(slot));
          return (
            <Card key={slot} className="flex flex-col gap-3">
              <div className="flex items-center gap-2.5 border-b border-border pb-2.5">
                <Icon className="w-6 h-6 text-primary shrink-0" aria-hidden="true" />
                <h3 className="text-h2 text-ink m-0" style={titleStyle}>
                  {label}
                </h3>
              </div>

              <div className="flex flex-col gap-3">
                {slotItems.map((item) => (
                  <div
                    key={`${slot}-${item.id}`}
                    className="flex flex-col gap-0.5 border-b border-border last:border-b-0 pb-2.5 last:pb-0"
                  >
                    <span
                      className="text-body-strong text-ink"
                      style={bodyStrongStyle}
                    >
                      {`${item.genericName} ${item.strength}`}
                    </span>
                    <span className="text-body text-ink" style={bodyStyle}>
                      {`1 ${item.form}`}
                    </span>
                    {item.mealNote !== null && (
                      <span
                        className="text-body text-ink-muted"
                        style={bodyStyle}
                      >
                        {item.mealNote}
                      </span>
                    )}
                  </div>
                ))}
              </div>
            </Card>
          );
        })}
      </div>

      {itemsWithNotes.length > 0 && (
        <div className="flex flex-col gap-3">
          {itemsWithNotes.map((item) => (
            <Card
              key={`note-${item.id}`}
              variant="primary-soft"
              className="flex items-start gap-3"
            >
              <Info
                className="w-6 h-6 text-primary shrink-0 mt-1"
                aria-hidden="true"
              />
              <div className="flex flex-col gap-1">
                <span
                  className="text-body-strong text-ink"
                  style={bodyStrongStyle}
                >
                  {`${item.genericName} ${item.strength}`}
                </span>
                <p className="text-body text-ink m-0" style={bodyStyle}>
                  {item.noteTl}
                </p>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
