// src/components/ScheduleTimeline.tsx
import React from "react";
import { Sunrise, Sun, Moon, Info } from "lucide-react";
import type { PrescriptionItem, Slot } from "../lib/types";
import { useLanguage } from "../context/LanguageContext";
import { Card } from "./Card";

interface ScheduleTimelineProps {
  items: PrescriptionItem[];
  scaled?: boolean;
}

export function ScheduleTimeline({ items, scaled = false }: ScheduleTimelineProps) {
  const { t, getMealNote, getNote } = useLanguage();

  const slotMeta: Array<{
    slot: Slot;
    label: string;
    Icon: React.ComponentType<{ className?: string; "aria-hidden"?: boolean | "true" | "false" }>;
  }> = [
    { slot: "umaga", label: t.slotMorning, Icon: Sunrise },
    { slot: "tanghali", label: t.slotNoon, Icon: Sun },
    { slot: "gabi", label: t.slotEvening, Icon: Moon },
  ];

  const activeSlots = slotMeta.filter(({ slot }) =>
    items.some((item) => item.slots.includes(slot))
  );

  const itemsWithNotes = items.filter((item) => getNote(item) !== null);

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
                {slotItems.map((item) => {
                  const meal = getMealNote(item);
                  return (
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
                        {t.unitDose(1, item.form)}
                      </span>
                      {meal !== null && (
                        <span
                          className="text-body text-ink-muted"
                          style={bodyStyle}
                        >
                          {meal}
                        </span>
                      )}
                    </div>
                  );
                })}
              </div>
            </Card>
          );
        })}
      </div>

      {itemsWithNotes.length > 0 && (
        <div className="flex flex-col gap-3">
          {itemsWithNotes.map((item) => {
            const note = getNote(item);
            return (
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
                    {note}
                  </p>
                </div>
              </Card>
            );
          })}
        </div>
      )}
    </div>
  );
}
