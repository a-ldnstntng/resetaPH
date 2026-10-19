// src/lib/catalog.ts
import type { PharmacyId, PriceEntry, Slot } from "./types";
import type { Language } from "./i18n";

export interface DrugCatalogEntry {
  genericName: string;
  aliases: string[];
  brandName: string;
  brandUnitPrice: number;
  strength: string;
  form: string;
  defaultSlots: Slot[];
  mealNote: string;
  mealNoteEn?: string;
  noteTl: string;
  noteEn?: string;
  maxDailyMg?: number;
  isAntibiotic?: boolean;
  prices: Record<PharmacyId, number>;
}

// Grounded price database compiled from Philippine DOH DPRI benchmarks & retail pharmacies (TGP, Watsons, Mercury)
export const PH_DRUG_CATALOG: DrugCatalogEntry[] = [
  {
    genericName: "Co-Amoxiclav",
    aliases: ["amoxicillin + clavulanic acid", "amoxicillin clavulanate", "amoxiclav", "augmentin"],
    brandName: "Augmentin",
    brandUnitPrice: 82.0,
    strength: "625 mg",
    form: "tablet",
    defaultSlots: ["umaga", "gabi"],
    mealNote: "Pagkatapos kumain",
    mealNoteEn: "Take after meals",
    noteTl: "Tapusin ang buong 7 araw kahit magaling na ang pakiramdam para maiwasan ang antibiotic resistance.",
    noteEn: "Finish the full 7-day course even if feeling better to prevent antibiotic resistance.",
    maxDailyMg: 2000,
    isAntibiotic: true,
    prices: { tgp: 18.0, watsons: 24.0, mercury: 26.0 },
  },
  {
    genericName: "Amoxicillin",
    aliases: ["amoxil", "amoxicilina"],
    brandName: "Amoxil",
    brandUnitPrice: 14.5,
    strength: "500 mg",
    form: "capsule",
    defaultSlots: ["umaga", "tanghali", "gabi"],
    mealNote: "Pagkatapos kumain",
    mealNoteEn: "Take after meals",
    noteTl: "Antibiotic ito: inumin sa tamang oras at tapusin ang buong reseta.",
    noteEn: "This is an antibiotic: take on schedule and finish the entire prescription.",
    maxDailyMg: 3000,
    isAntibiotic: true,
    prices: { tgp: 3.5, watsons: 5.0, mercury: 5.5 },
  },
  {
    genericName: "Cefuroxime",
    aliases: ["cefuroxime axetil", "zinnat"],
    brandName: "Zinnat",
    brandUnitPrice: 75.0,
    strength: "500 mg",
    form: "tablet",
    defaultSlots: ["umaga", "gabi"],
    mealNote: "Kasamang kumain para mas ma-absorb",
    mealNoteEn: "Take with food for better absorption",
    noteTl: "Tapusin ang iniresetang araw ng doktor para lubusang mapuksa ang impeksyon.",
    noteEn: "Complete the full days prescribed by the doctor to completely clear the infection.",
    maxDailyMg: 1000,
    isAntibiotic: true,
    prices: { tgp: 22.0, watsons: 28.0, mercury: 30.0 },
  },
  {
    genericName: "Cefalexin",
    aliases: ["cephalexin", "keflex"],
    brandName: "Keflex",
    brandUnitPrice: 28.0,
    strength: "500 mg",
    form: "capsule",
    defaultSlots: ["umaga", "tanghali", "gabi"],
    mealNote: "Bago o pagkatapos kumain",
    mealNoteEn: "Before or after meals",
    noteTl: "Tapusin ang buong reseta ng antibiotic.",
    noteEn: "Complete the full antibiotic prescription.",
    maxDailyMg: 4000,
    isAntibiotic: true,
    prices: { tgp: 7.0, watsons: 9.5, mercury: 10.0 },
  },
  {
    genericName: "Azithromycin",
    aliases: ["zithromax"],
    brandName: "Zithromax",
    brandUnitPrice: 165.0,
    strength: "500 mg",
    form: "tablet",
    defaultSlots: ["umaga"],
    mealNote: "1 oras bago kumain o 2 oras pagkatapos kumain",
    mealNoteEn: "1 hour before meals or 2 hours after meals",
    noteTl: "Karaniwang 3 hanggang 5 araw lamang iniinom. Huwag laktawan.",
    noteEn: "Usually taken for only 3 to 5 days. Do not skip doses.",
    maxDailyMg: 500,
    isAntibiotic: true,
    prices: { tgp: 32.0, watsons: 45.0, mercury: 48.0 },
  },
  {
    genericName: "Amlodipine",
    aliases: ["norvasc"],
    brandName: "Norvasc",
    brandUnitPrice: 24.0,
    strength: "5 mg",
    form: "tablet",
    defaultSlots: ["umaga"],
    mealNote: "Kahit may laman o walang laman ang tiyan",
    mealNoteEn: "With or without food",
    noteTl: "Maintenance para sa presyon ng dugo. Huwag biglang ititigil nang walang abiso ng doktor.",
    noteEn: "Blood pressure maintenance. Do not stop abruptly without doctor's advice.",
    maxDailyMg: 10,
    prices: { tgp: 2.0, watsons: 4.5, mercury: 5.0 },
  },
  {
    genericName: "Amlodipine",
    aliases: ["norvasc"],
    brandName: "Norvasc",
    brandUnitPrice: 44.0,
    strength: "10 mg",
    form: "tablet",
    defaultSlots: ["umaga"],
    mealNote: "Kahit may laman o walang laman ang tiyan",
    mealNoteEn: "With or without food",
    noteTl: "Pang-high blood: inumin sa parehong oras araw-araw.",
    noteEn: "For hypertension: take at the same time each day.",
    maxDailyMg: 10,
    prices: { tgp: 3.5, watsons: 7.0, mercury: 8.0 },
  },
  {
    genericName: "Losartan",
    aliases: ["cozaar"],
    brandName: "Cozaar",
    brandUnitPrice: 26.0,
    strength: "50 mg",
    form: "tablet",
    defaultSlots: ["umaga"],
    mealNote: "Kahit may laman o walang laman ang tiyan",
    mealNoteEn: "With or without food",
    noteTl: "Para sa altapresyon at proteksyon sa bato. Inumin araw-araw ayon sa bilin.",
    noteEn: "For hypertension and kidney protection. Take daily as instructed.",
    maxDailyMg: 100,
    prices: { tgp: 3.0, watsons: 6.0, mercury: 6.5 },
  },
  {
    genericName: "Losartan",
    aliases: ["cozaar"],
    brandName: "Cozaar",
    brandUnitPrice: 45.0,
    strength: "100 mg",
    form: "tablet",
    defaultSlots: ["umaga"],
    mealNote: "Kahit may laman o walang laman ang tiyan",
    mealNoteEn: "With or without food",
    noteTl: "Mataas na dosage para sa altapresyon. Bantayan ang blood pressure regular.",
    noteEn: "High dosage for hypertension. Monitor blood pressure regularly.",
    maxDailyMg: 100,
    prices: { tgp: 5.5, watsons: 10.0, mercury: 11.0 },
  },
  {
    genericName: "Metformin",
    aliases: ["glucophage"],
    brandName: "Glucophage",
    brandUnitPrice: 16.0,
    strength: "500 mg",
    form: "tablet",
    defaultSlots: ["umaga", "gabi"],
    mealNote: "Kasamang kumain para iwas sakit ng tiyan",
    mealNoteEn: "Take with meals to prevent stomach irritation",
    noteTl: "Para sa diabetes (blood sugar). Inumin kasabay ng pagkain.",
    noteEn: "For diabetes (blood sugar). Take together with meals.",
    maxDailyMg: 2550,
    prices: { tgp: 2.0, watsons: 3.75, mercury: 4.0 },
  },
  {
    genericName: "Metformin",
    aliases: ["glucophage"],
    brandName: "Glucophage",
    brandUnitPrice: 22.0,
    strength: "850 mg",
    form: "tablet",
    defaultSlots: ["umaga", "gabi"],
    mealNote: "Kasamang kumain o pagkatapos kumain",
    mealNoteEn: "Take with or right after meals",
    noteTl: "Para sa diabetes. Panatilihing regular ang pagsusuri ng asukal sa dugo.",
    noteEn: "For diabetes. Keep monitoring blood sugar regularly.",
    maxDailyMg: 2550,
    prices: { tgp: 3.5, watsons: 5.5, mercury: 6.0 },
  },
  {
    genericName: "Glimepiride",
    aliases: ["amaryl"],
    brandName: "Amaryl",
    brandUnitPrice: 25.0,
    strength: "2 mg",
    form: "tablet",
    defaultSlots: ["umaga"],
    mealNote: "Bago mag-almusal o kasabay ng unang pagkain",
    mealNoteEn: "Before breakfast or with your first meal",
    noteTl: "Pampababa ng blood sugar. Magbaon ng matamis sakaling mahilo o mag-hypoglycemia.",
    noteEn: "Lowers blood sugar. Keep candy or sweets handy in case of dizziness or hypoglycemia.",
    maxDailyMg: 8,
    prices: { tgp: 4.0, watsons: 7.5, mercury: 8.0 },
  },
  {
    genericName: "Atorvastatin",
    aliases: ["lipitor"],
    brandName: "Lipitor",
    brandUnitPrice: 42.0,
    strength: "20 mg",
    form: "tablet",
    defaultSlots: ["gabi"],
    mealNote: "Mas mabuting inumin sa gabi bago matulog",
    mealNoteEn: "Best taken at night before bedtime",
    noteTl: "Pampababa ng kolesterol sa dugo. Karaniwang iniinom sa gabi.",
    noteEn: "Lowers blood cholesterol. Usually taken at bedtime.",
    maxDailyMg: 80,
    prices: { tgp: 7.5, watsons: 12.0, mercury: 13.0 },
  },
  {
    genericName: "Atorvastatin",
    aliases: ["lipitor"],
    brandName: "Lipitor",
    brandUnitPrice: 58.0,
    strength: "40 mg",
    form: "tablet",
    defaultSlots: ["gabi"],
    mealNote: "Inumin sa gabi bago matulog",
    mealNoteEn: "Take at night before bedtime",
    noteTl: "Pampababa ng kolesterol. Ipaalam sa doktor kung magkaroon ng pananakit ng kalamnan.",
    noteEn: "Lowers cholesterol. Notify your doctor if you experience muscle aches.",
    maxDailyMg: 80,
    prices: { tgp: 12.0, watsons: 18.0, mercury: 19.5 },
  },
  {
    genericName: "Simvastatin",
    aliases: ["zocor"],
    brandName: "Zocor",
    brandUnitPrice: 35.0,
    strength: "20 mg",
    form: "tablet",
    defaultSlots: ["gabi"],
    mealNote: "Inumin sa gabi",
    mealNoteEn: "Take in the evening",
    noteTl: "Para sa kolesterol. Mas epektibo kapag gabi ininom.",
    noteEn: "For cholesterol. Most effective when taken at bedtime.",
    maxDailyMg: 40,
    prices: { tgp: 4.5, watsons: 7.5, mercury: 8.0 },
  },
  {
    genericName: "Paracetamol",
    aliases: ["biogesic", "tempra", "calpol", "acetaminophen"],
    brandName: "Biogesic",
    brandUnitPrice: 5.0,
    strength: "500 mg",
    form: "tablet",
    defaultSlots: ["umaga", "tanghali", "gabi"],
    mealNote: "Kahit may laman o walang laman ang tiyan",
    mealNoteEn: "With or without food",
    noteTl: "Para sa lagnat at kirot. Huwag lumampas sa 8 tableta (4,000 mg) sa loob ng 24 oras para sa atay.",
    noteEn: "For fever and pain. Do not exceed 8 tablets (4,000 mg) within 24 hours to protect your liver.",
    maxDailyMg: 4000,
    prices: { tgp: 1.5, watsons: 2.5, mercury: 2.75 },
  },
  {
    genericName: "Mefenamic Acid",
    aliases: ["ponstan", "dolfenal"],
    brandName: "Ponstan",
    brandUnitPrice: 28.0,
    strength: "500 mg",
    form: "capsule",
    defaultSlots: ["umaga", "tanghali", "gabi"],
    mealNote: "Laging pagkatapos kumain para hindi humapdi ang tiyan",
    mealNoteEn: "Always take after meals to prevent stomach irritation",
    noteTl: "Para sa matinding kirot. Huwag inumin nang walang laman ang tiyan o lagpas 7 araw.",
    noteEn: "For acute pain. Do not take on an empty stomach or for longer than 7 days.",
    maxDailyMg: 1500,
    prices: { tgp: 3.5, watsons: 6.0, mercury: 6.5 },
  },
  {
    genericName: "Ibuprofen",
    aliases: ["medicol", "advil"],
    brandName: "Advil",
    brandUnitPrice: 12.0,
    strength: "200 mg",
    form: "softgel",
    defaultSlots: ["umaga", "tanghali", "gabi"],
    mealNote: "Pagkatapos kumain",
    mealNoteEn: "Take after meals",
    noteTl: "Para sa kirot at pamamaga. Inumin pagkatapos kumain.",
    noteEn: "For pain and inflammation. Always take after meals.",
    maxDailyMg: 1200,
    prices: { tgp: 2.5, watsons: 4.5, mercury: 5.0 },
  },
  {
    genericName: "Omeprazole",
    aliases: ["losec", "prilosec"],
    brandName: "Losec",
    brandUnitPrice: 48.0,
    strength: "20 mg",
    form: "capsule",
    defaultSlots: ["umaga"],
    mealNote: "30 minuto bago mag-almusal",
    mealNoteEn: "30 minutes before breakfast",
    noteTl: "Para sa hyperacidity at ulcer. Inumin nang buo sa umaga 30 minuto bago kumain.",
    noteEn: "For hyperacidity and ulcers. Swallow whole in the morning 30 minutes before breakfast.",
    maxDailyMg: 40,
    prices: { tgp: 6.0, watsons: 11.0, mercury: 12.0 },
  },
  {
    genericName: "Cetirizine",
    aliases: ["alnix", "virlix", "zyrtec"],
    brandName: "Alnix",
    brandUnitPrice: 28.0,
    strength: "10 mg",
    form: "tablet",
    defaultSlots: ["gabi"],
    mealNote: "Bago matulog",
    mealNoteEn: "Take before bedtime",
    noteTl: "Para sa allergy at pangangati. Maaaring magdulot ng antok kaya mas maganda sa gabi.",
    noteEn: "For allergy and itching. May cause drowsiness; best taken at bedtime.",
    maxDailyMg: 10,
    prices: { tgp: 3.0, watsons: 6.5, mercury: 7.0 },
  },
  {
    genericName: "Salbutamol",
    aliases: ["ventolin"],
    brandName: "Ventolin",
    brandUnitPrice: 8.5,
    strength: "2 mg",
    form: "tablet",
    defaultSlots: ["umaga", "tanghali", "gabi"],
    mealNote: "Pagkatapos kumain",
    mealNoteEn: "Take after meals",
    noteTl: "Pang-hika o bronchospasm. Maaaring makaramdam ng kaunting panginginig o mabilis na tibok ng puso.",
    noteEn: "For asthma or bronchospasm. May cause slight tremors or rapid heart rate.",
    maxDailyMg: 16,
    prices: { tgp: 1.5, watsons: 3.0, mercury: 3.5 },
  },
];

function normalize(s?: string | null): string {
  if (!s) return "";
  return s.trim().toLowerCase().replace(/[^a-z0-9]/g, "");
}

function parseMg(strength?: string | null): number | null {
  if (!strength) return null;
  const match = strength.match(/(\d+(?:\.\d+)?)\s*mg/i);
  return match ? parseFloat(match[1]) : null;
}

export function lookupCatalogItem(
  genericOrBrand: string,
  strength: string = ""
): DrugCatalogEntry | undefined {
  const cleanInput = normalize(genericOrBrand);
  const cleanStrength = normalize(strength);

  // Exact match with strength
  const exact = PH_DRUG_CATALOG.find((entry) => {
    const matchesName =
      normalize(entry.genericName) === cleanInput ||
      normalize(entry.brandName) === cleanInput ||
      entry.aliases.some((alias) => normalize(alias) === cleanInput);
    if (!matchesName) return false;
    return cleanStrength ? normalize(entry.strength) === cleanStrength : true;
  });

  if (exact) return exact;

  // Name-only fallback if strength not matched
  return PH_DRUG_CATALOG.find((entry) => {
    return (
      normalize(entry.genericName) === cleanInput ||
      normalize(entry.brandName) === cleanInput ||
      entry.aliases.some((alias) => normalize(alias) === cleanInput)
    );
  });
}

export function lookupCatalogPrices(
  genericName: string,
  strength: string
): { brandUnitPrice: number | null; genericPrices: PriceEntry[] } {
  const catalogItem = lookupCatalogItem(genericName, strength);

  if (!catalogItem) {
    return {
      brandUnitPrice: null,
      genericPrices: [
        { pharmacyId: "tgp", unitPrice: null, asOf: null },
        { pharmacyId: "watsons", unitPrice: null, asOf: null },
        { pharmacyId: "mercury", unitPrice: null, asOf: null },
      ],
    };
  }

  const asOf = "2026-03";
  return {
    brandUnitPrice: catalogItem.brandUnitPrice,
    genericPrices: [
      { pharmacyId: "tgp", unitPrice: catalogItem.prices.tgp, asOf },
      { pharmacyId: "watsons", unitPrice: catalogItem.prices.watsons, asOf },
      { pharmacyId: "mercury", unitPrice: catalogItem.prices.mercury, asOf },
    ],
  };
}

export interface MedicalValidationResult {
  genericName: string;
  brandName: string | null;
  strength: string;
  form: string;
  slots: Slot[];
  mealNote: string | null;
  mealNoteEn?: string | null;
  noteTl: string | null;
  noteEn?: string | null;
  uncertainFields: Array<"genericName" | "strength" | "quantity" | "sig">;
}

/**
 * Validates extracted prescription data against Philippine clinical benchmarks:
 * - Generics Act compliance (resolves brand aliases to official generic name)
 * - Safe adult therapeutic dosage ceilings
 * - Antibiotic duration checks
 */
export function validateMedicationSafety(
  rawGeneric: string,
  rawStrength: string,
  rawForm: string,
  dosesPerDay: number,
  durationDays: number | null,
  currentUncertain: Array<"genericName" | "strength" | "quantity" | "sig">,
  currentMealNoteTl: string | null = null,
  currentNoteTl: string | null = null,
  currentMealNoteEn: string | null = null,
  currentNoteEn: string | null = null,
  language: Language = "en"
): MedicalValidationResult {
  const matched = lookupCatalogItem(rawGeneric, rawStrength);
  const uncertain = [...currentUncertain];

  const genericName = matched ? matched.genericName : rawGeneric;
  const brandName = matched ? matched.brandName : null;
  const strength = rawStrength || (matched ? matched.strength : "");
  const form = rawForm || (matched ? matched.form : "tablet");
  const slots: Slot[] = matched ? matched.defaultSlots : ["umaga"];

  let mealNote: string | null;
  let mealNoteEn: string | null;
  let noteTl: string | null;
  let noteEn: string | null;

  if (matched) {
    mealNote = matched.mealNote ?? currentMealNoteTl ?? null;
    mealNoteEn = matched.mealNoteEn ?? currentMealNoteEn ?? (language === "en" ? currentMealNoteTl : null);
    noteTl = matched.noteTl ?? currentNoteTl ?? null;
    noteEn = matched.noteEn ?? currentNoteEn ?? (language === "en" ? currentNoteTl : null);
  } else {
    if (language === "en") {
      mealNoteEn = currentMealNoteEn ?? currentMealNoteTl ?? null;
      mealNote = currentMealNoteTl ?? currentMealNoteEn ?? null;
      noteEn = currentNoteEn ?? currentNoteTl ?? null;
      noteTl = currentNoteTl ?? currentNoteEn ?? null;
    } else {
      mealNote = currentMealNoteTl ?? currentMealNoteEn ?? null;
      mealNoteEn = currentMealNoteEn ?? currentMealNoteTl ?? null;
      noteTl = currentNoteTl ?? currentNoteEn ?? null;
      noteEn = currentNoteEn ?? currentNoteTl ?? null;
    }
  }

  // Check dosage ceiling if strength is known in mg
  const mgPerDose = parseMg(strength);
  if (matched && mgPerDose !== null && matched.maxDailyMg) {
    const totalDailyMg = mgPerDose * Math.max(1, dosesPerDay);
    if (totalDailyMg > matched.maxDailyMg) {
      if (!uncertain.includes("strength")) uncertain.push("strength");
      if (!uncertain.includes("sig")) uncertain.push("sig");
      const warningTl = `BABALA: Lumalagpas sa karaniwang ligtas na dosis (${matched.maxDailyMg} mg/araw).`;
      const warningEn = `WARNING: Exceeds standard safe dosage (${matched.maxDailyMg} mg/day).`;
      noteTl = noteTl ? `${warningTl} ${noteTl}` : warningTl;
      noteEn = noteEn ? `${warningEn} ${noteEn}` : warningEn;
    }
  }

  // Antibiotic duration safety check
  if (matched?.isAntibiotic && durationDays !== null && durationDays < 5) {
    if (!uncertain.includes("sig")) uncertain.push("sig");
    const warningTl = "PAALALA: Karaniwang 5-7 araw ang buong reseta ng antibiotic.";
    const warningEn = "REMINDER: A standard antibiotic prescription usually lasts 5-7 days.";
    noteTl = noteTl ? `${warningTl} ${noteTl}` : warningTl;
    noteEn = noteEn ? `${warningEn} ${noteEn}` : warningEn;
  }

  return {
    genericName,
    brandName,
    strength,
    form,
    slots,
    mealNote,
    mealNoteEn,
    noteTl,
    noteEn,
    uncertainFields: uncertain,
  };
}

export function getItemMealNote(
  item: { mealNote: string | null; mealNoteEn?: string | null },
  lang: Language
): string | null {
  if (lang === "en") {
    return item.mealNoteEn || item.mealNote || null;
  }
  return item.mealNote || item.mealNoteEn || null;
}

export function getItemNote(
  item: { noteTl: string | null; noteEn?: string | null },
  lang: Language
): string | null {
  if (lang === "en") {
    return item.noteEn || item.noteTl || null;
  }
  return item.noteTl || item.noteEn || null;
}
