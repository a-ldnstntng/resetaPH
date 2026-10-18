// src/components/ListenButton.tsx
import { useEffect, useState } from "react";
import { Volume2, Square } from "lucide-react";
import type { PrescriptionItem, Slot } from "../lib/types";
import { PrimaryButton } from "./PrimaryButton";
import { EmptyState } from "./EmptyState";

interface ListenButtonProps {
  items: PrescriptionItem[];
}

const SLOT_LABEL: Record<Slot, string> = {
  umaga: "Umaga",
  tanghali: "Tanghali",
  gabi: "Gabi",
};

export function buildSpeechText(items: PrescriptionItem[]): string {
  const parts: string[] = [];
  for (const item of items) {
    parts.push(`${item.genericName} ${item.strength}.`);
    const slotNames = item.slots.map((s) => SLOT_LABEL[s]).join(", ");
    if (item.mealNote !== null) {
      parts.push(`${slotNames}. ${item.mealNote}.`);
    } else {
      parts.push(`${slotNames}.`);
    }
    if (item.noteTl !== null) {
      parts.push(item.noteTl);
    }
  }
  return parts.join(" ");
}

export function ListenButton({ items }: ListenButtonProps) {
  const [supported, setSupported] = useState(true);
  const [speaking, setSpeaking] = useState(false);

  useEffect(() => {
    // VERIFY: window.speechSynthesis and SpeechSynthesisUtterance browser support check
    const isSupported =
      typeof window !== "undefined" &&
      "speechSynthesis" in window &&
      typeof window.SpeechSynthesisUtterance !== "undefined";
    setSupported(isSupported);

    return () => {
      if (isSupported) {
        window.speechSynthesis.cancel();
      }
    };
  }, []);

  if (!supported) {
    return (
      <EmptyState message="Hindi sinusuportahan ng device na ito ang pakikinig." />
    );
  }

  const handleToggleSpeech = () => {
    if (speaking) {
      window.speechSynthesis.cancel();
      setSpeaking(false);
      return;
    }

    window.speechSynthesis.cancel();
    const text = buildSpeechText(items);
    const utterance = new window.SpeechSynthesisUtterance(text);
    // VERIFY: speechSynthesis utterance lang set to fil-PH with fallback to default voice
    utterance.lang = "fil-PH";
    const voices = window.speechSynthesis.getVoices();
    const filVoice = voices.find((v) =>
      v.lang.toLowerCase().startsWith("fil") || v.lang.toLowerCase().startsWith("tl")
    );
    if (filVoice) {
      utterance.voice = filVoice;
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
      {speaking ? "Itigil" : "Pakinggan"}
    </PrimaryButton>
  );
}
