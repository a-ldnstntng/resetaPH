// src/components/ListenButton.tsx
import { useEffect, useState } from "react";
import { Volume2, Square } from "lucide-react";
import type { PrescriptionItem, Slot } from "../lib/types";
import { getItemMealNote, getItemNote } from "../lib/catalog";
import { useLanguage } from "../context/LanguageContext";
import type { Language } from "../lib/i18n";
import { PrimaryButton } from "./PrimaryButton";
import { EmptyState } from "./EmptyState";

interface ListenButtonProps {
  items: PrescriptionItem[];
}

const SLOT_LABEL_FIL: Record<Slot, string> = {
  umaga: "Umaga",
  tanghali: "Tanghali",
  gabi: "Gabi",
};

const SLOT_LABEL_EN: Record<Slot, string> = {
  umaga: "Morning",
  tanghali: "Noon",
  gabi: "Evening",
};

export function buildSpeechText(items: PrescriptionItem[], language: Language = "en"): string {
  const parts: string[] = [];
  const slotLabels = language === "en" ? SLOT_LABEL_EN : SLOT_LABEL_FIL;

  for (const item of items) {
    parts.push(`${item.genericName} ${item.strength}.`);
    const slotNames = item.slots.map((s) => slotLabels[s] || s).join(", ");
    const meal = getItemMealNote(item, language);
    if (meal !== null) {
      parts.push(`${slotNames}. ${meal}.`);
    } else {
      parts.push(`${slotNames}.`);
    }
    const note = getItemNote(item, language);
    if (note !== null) {
      parts.push(note);
    }
  }
  return parts.join(" ");
}

export function ListenButton({ items }: ListenButtonProps) {
  const { language, t } = useLanguage();
  const [supported, setSupported] = useState(true);
  const [speaking, setSpeaking] = useState(false);

  useEffect(() => {
    const isSupported =
      typeof window !== "undefined" &&
      "speechSynthesis" in window &&
      typeof window.SpeechSynthesisUtterance !== "undefined";
    setSupported(isSupported);

    if (isSupported) {
      window.speechSynthesis.getVoices();
      const handleVoicesChanged = () => {
        window.speechSynthesis.getVoices();
      };
      window.speechSynthesis.addEventListener("voiceschanged", handleVoicesChanged);

      return () => {
        window.speechSynthesis.removeEventListener("voiceschanged", handleVoicesChanged);
        window.speechSynthesis.cancel();
      };
    }
  }, []);

  if (!supported) {
    return (
      <EmptyState message={t.listenNotSupported} />
    );
  }

  const handleToggleSpeech = () => {
    if (speaking) {
      window.speechSynthesis.cancel();
      setSpeaking(false);
      return;
    }

    window.speechSynthesis.cancel();
    const text = buildSpeechText(items, language);
    const utterance = new window.SpeechSynthesisUtterance(text);

    const voices = window.speechSynthesis.getVoices();
    if (language === "fil") {
      utterance.lang = "fil-PH";
      const filVoice = voices.find((v) =>
        v.lang.toLowerCase().startsWith("fil") || v.lang.toLowerCase().startsWith("tl")
      );
      if (filVoice) {
        utterance.voice = filVoice;
      }
    } else {
      utterance.lang = "en-US";
      const enVoice = voices.find((v) => v.lang.toLowerCase().startsWith("en"));
      if (enVoice) {
        utterance.voice = enVoice;
      }
    }

    utterance.onend = () => {
      setSpeaking(false);
    };
    utterance.onerror = () => {
      setSpeaking(false);
    };

    setSpeaking(true);
    window.speechSynthesis.speak(utterance);
  };

  return (
    <PrimaryButton
      onClick={handleToggleSpeech}
      icon={
        speaking ? (
          <Square className="w-5 h-5" />
        ) : (
          <Volume2 className="w-6 h-6" />
        )
      }
    >
      {speaking ? t.btnStop : t.btnListen}
    </PrimaryButton>
  );
}
