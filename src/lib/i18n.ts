// src/lib/i18n.ts

export type Language = "en" | "fil";

export const DEFAULT_LANGUAGE: Language = "en";
export const LANGUAGE_STORAGE_KEY = "resetaph_language";

export interface Translations {
  appName: string;
  appTagline: string;
  settings: string;
  language: string;
  english: string;
  filipino: string;
  scanAgain: string;
  errorGeneric: string;
  errorDeepScan: string;
  notDetected: string;

  // Onboarding modal
  onboardingTitle: string;
  onboardingSubtitle: string;
  onboardingSettingsHint: string;
  onboardingContinue: string;
  prescriptionFallback: string;
  homeBrandTitle: string;

  // Settings modal
  settingsTitle: string;
  settingsLanguageHeading: string;
  settingsLanguageDescription: string;
  settingsClose: string;

  // Steps
  stepScan: string;
  stepReview: string;
  stepResults: string;

  // Home Screen
  homeHeading: string;
  homeSubheading: string;
  btnScanCamera: string;
  btnUploadPhoto: string;
  historyTitle: string;
  historyClear: string;
  historyClearTitle: string;
  historyMedsCount: (count: number) => string;
  samplesHeading: string;
  medicalDisclaimer: string;

  // Scanning Overlay
  readingPrescription: string;

  // Review Screen
  reviewHeading: string;
  reviewSubheading: string;
  lineCountMismatch: string;
  deepScanTitle: string;
  deepScanDescription: string;
  btnDeepScan: string;
  doctorLabel: string;
  prcLabel: string;
  dateLabel: string;
  writtenOnRx: string;
  meaningHeading: string;
  fieldGenericName: string;
  fieldStrength: string;
  fieldQuantity: string;
  fieldHowToTake: string;
  uncertainBadge: string;
  uncertainHelpText: string;
  safetyWarningTitle: string;
  medReminderTitle: string;
  btnConfirmReview: string;
  howToTakeSummary: (dosesPerDay: number, durationDays: number | null) => string;

  // Results Screen & Tabs
  tabPrice: string;
  tabHowToTake: string;
  tabPharmacy: string;
  howToTakeHeading: string;
  fontSizeToggleLabel: string;
  nearbyPharmaciesHeading: string;
  offlineMessage: string;
  locationDeniedMessage: string;
  filter24h: string;
  btnShowAtPharmacy: string;

  // Savings Hero
  estimatedSavings: string;
  cheaperGenericBadge: (percent: number) => string;
  ifBuyingGeneric: string;
  brandedLabel: string;
  genericCheapestLabel: string;
  notEnoughPrices: string;
  priceEstimateDisclaimer: string;
  priceDateAsOf: (date: string) => string;
  priceDateMissing: string;
  excludedItemNotice: (genericName: string, strength: string) => string;

  // Pharmacy Price Row
  cheapestBadge: string;
  perPiece: (price: string) => string;
  noPriceYet: string;

  // Schedule Timeline
  slotMorning: string;
  slotNoon: string;
  slotEvening: string;
  unitDose: (quantity: number, form: string) => string;

  // Listen Button
  listenNotSupported: string;
  btnListen: string;
  btnStop: string;

  // Branch Card
  distanceNote: string;
  usuallyInStock: string;
  callToConfirm: string;
  btnCall: string;
  noPhoneYet: string;
  btnCopyInquiry: string;
  btnCopied: string;

  // Counter Mode
  counterGreeting: string;
  counterPrompt: string;
  counterPharmacistNote: string;
  counterClose: string;
}

export const translations: Record<Language, Translations> = {
  en: {
    appName: "ResetaPH",
    appTagline: "Find the right medication, estimated prices, and dosage instructions.",
    settings: "Settings",
    language: "Language",
    english: "English",
    filipino: "Filipino",
    scanAgain: "Scan again",
    errorGeneric: "Something went wrong. Please try again.",
    errorDeepScan: "Something went wrong with Deep Scan. Please try again.",
    notDetected: "Not detected",

    onboardingTitle: "Choose your language",
    onboardingSubtitle:
      "Welcome to ResetaPH! Please select your preferred language. You can also change this anytime in Settings.",
    onboardingSettingsHint: "You can change this anytime in Settings.",
    onboardingContinue: "Continue",
    prescriptionFallback: "Prescription",
    homeBrandTitle: "ResetaPH Home",

    settingsTitle: "Settings",
    settingsLanguageHeading: "Language / Wika",
    settingsLanguageDescription:
      "Choose the language for prescription analysis, reminders, and prices.",
    settingsClose: "Close",

    stepScan: "Scan",
    stepReview: "Review",
    stepResults: "Results",

    homeHeading: "Scan your prescription",
    homeSubheading:
      "Find the right medication, estimated prices, and dosage instructions.",
    btnScanCamera: "Scan Prescription",
    btnUploadPhoto: "Upload Photo",
    historyTitle: "Saved on Device",
    historyClear: "Clear",
    historyClearTitle: "Clear local history",
    historyMedsCount: (count: number) =>
      count === 1 ? "1 medicine" : `${count} medicines`,
    samplesHeading: "Try a sample prescription",
    medicalDisclaimer:
      "This is a guide only. Not a substitute for professional medical or pharmacist advice.",

    readingPrescription: "Reading prescription...",

    reviewHeading: "Here is what we read",
    reviewSubheading: "Please verify everything before proceeding.",
    lineCountMismatch:
      "Some medication lines could not be read clearly. Please check the slip.",
    deepScanTitle: "Uncertain handwriting?",
    deepScanDescription:
      "You can use deeper AI reasoning to decipher unclear cursive or doctor's shorthand.",
    btnDeepScan: "Try Deep AI Decipher",
    doctorLabel: "Doctor",
    prcLabel: "PRC",
    dateLabel: "Date",
    writtenOnRx: "Written on prescription",
    meaningHeading: "What it means",
    fieldGenericName: "Generic name",
    fieldStrength: "Strength",
    fieldQuantity: "Quantity",
    fieldHowToTake: "How to take",
    uncertainBadge: "Please verify",
    uncertainHelpText: "We are not certain about this. Please ask your pharmacist.",
    safetyWarningTitle: "Safety Warning:",
    medReminderTitle: "Medication Reminder:",
    btnConfirmReview: "Looks good, show prices",
    howToTakeSummary: (dosesPerDay: number, durationDays: number | null) => {
      const parts = [`${dosesPerDay}x daily`];
      if (durationDays !== null) {
        parts.push(durationDays === 1 ? "1 day" : `${durationDays} days`);
      }
      return parts.join(" · ");
    },

    tabPrice: "Price",
    tabHowToTake: "How to Take",
    tabPharmacy: "Pharmacy",
    howToTakeHeading: "How to take",
    fontSizeToggleLabel: "Toggle text size",
    nearbyPharmaciesHeading: "Nearby pharmacies",
    offlineMessage: "No internet connection. Map cannot be displayed.",
    locationDeniedMessage:
      "Could not detect your location. Showing the area around DLSU Manila.",
    filter24h: "Open 24 hours",
    btnShowAtPharmacy: "Show at Pharmacy",

    estimatedSavings: "Estimated savings",
    cheaperGenericBadge: (percent: number) => `${percent}% cheaper`,
    ifBuyingGeneric: "if purchasing generic equivalents",
    brandedLabel: "Branded",
    genericCheapestLabel: "Generic (cheapest)",
    notEnoughPrices: "Not enough price data available to compare.",
    priceEstimateDisclaimer: "Estimated prices only. May vary at the pharmacy.",
    priceDateAsOf: (date: string) => `Prices as of ${date}`,
    priceDateMissing: "Price date: not yet specified",
    excludedItemNotice: (genericName: string, strength: string) =>
      `Excluded: ${genericName} ${strength} (no price data)`,

    cheapestBadge: "Cheapest",
    perPiece: (price: string) => `${price} / piece`,
    noPriceYet: "No price yet",

    slotMorning: "Morning",
    slotNoon: "Noon",
    slotEvening: "Evening",
    unitDose: (quantity: number, form: string) => `${quantity} ${form}`,

    listenNotSupported: "This device does not support audio playback.",
    btnListen: "Listen",
    btnStop: "Stop",

    distanceNote: "approximate, straight line distance",
    usuallyInStock: "Usually in stock",
    callToConfirm: "Call to confirm",
    btnCall: "Call",
    noPhoneYet: "No phone number yet",
    btnCopyInquiry: "Copy inquiry",
    btnCopied: "Copied",

    counterGreeting: "Good day!",
    counterPrompt:
      "I would like to purchase generic equivalents for this prescription:",
    counterPharmacistNote: "Please confirm with the pharmacist.",
    counterClose: "Close",
  },
  fil: {
    appName: "ResetaPH",
    appTagline: "Alamin ang tamang gamot, tinatayang presyo, at paano inumin.",
    settings: "Mga Setting",
    language: "Wika",
    english: "Ingles (English)",
    filipino: "Filipino",
    scanAgain: "Mag-scan ulit",
    errorGeneric: "May nangyaring problema. Subukan ulit.",
    errorDeepScan: "May nangyaring problema sa Deep Scan. Subukan ulit.",
    notDetected: "Hindi nabasa",

    onboardingTitle: "Piliin ang iyong wika",
    onboardingSubtitle:
      "Maligayang pagdating sa ResetaPH! Piliin ang iyong gustong wika. Maaari mo itong palitan anumang oras sa Settings.",
    onboardingSettingsHint: "Maaari mo itong palitan anumang oras sa Settings.",
    onboardingContinue: "Magpatuloy",
    prescriptionFallback: "Reseta",
    homeBrandTitle: "ResetaPH Tahanan",

    settingsTitle: "Mga Setting",
    settingsLanguageHeading: "Wika / Language",
    settingsLanguageDescription:
      "Piliin ang wikang gagamitin para sa mga bilin, etiketa, at detalye ng reseta.",
    settingsClose: "Isara",

    stepScan: "Scan",
    stepReview: "Suriin",
    stepResults: "Resulta",

    homeHeading: "I-scan ang reseta mo",
    homeSubheading:
      "Alamin ang tamang gamot, tinatayang presyo, at paano inumin.",
    btnScanCamera: "I-scan ang Reseta",
    btnUploadPhoto: "Mag-upload ng Larawan",
    historyTitle: "Naka-save sa Device",
    historyClear: "Burahin",
    historyClearTitle: "Burahin ang lokal na history",
    historyMedsCount: (count: number) => `${count} gamot`,
    samplesHeading: "Subukan ang sample na reseta",
    medicalDisclaimer:
      "Gabay lang ito. Hindi kapalit ng payo ng doktor o parmasyutiko.",

    readingPrescription: "Binabasa ang reseta...",

    reviewHeading: "Ito ang nabasa namin",
    reviewSubheading: "Pakisigurado kung tama bago magpatuloy.",
    lineCountMismatch:
      "May hindi nabasa na gamot. Pakisigurado ang reseta.",
    deepScanTitle: "Hindi tiyak ang sulat-kamay?",
    deepScanDescription:
      "Maaaring gumamit ng mas malalim na AI reasoning upang basahin ang malalabong cursive o shorthand ng doktor.",
    btnDeepScan: "Subukan ang Deep AI Decipher",
    doctorLabel: "Doktor",
    prcLabel: "PRC",
    dateLabel: "Petsa",
    writtenOnRx: "Nakasulat sa reseta",
    meaningHeading: "Ibig sabihin",
    fieldGenericName: "Generic na pangalan",
    fieldStrength: "Lakas",
    fieldQuantity: "Dami",
    fieldHowToTake: "Paano inumin",
    uncertainBadge: "Pakisigurado",
    uncertainHelpText: "Hindi kami sigurado dito. Itanong sa parmasyutiko.",
    safetyWarningTitle: "Babala sa Kaligtasan:",
    medReminderTitle: "Paalala sa Pag-inom:",
    btnConfirmReview: "Tama, ipakita ang presyo",
    howToTakeSummary: (dosesPerDay: number, durationDays: number | null) => {
      const parts = [`${dosesPerDay}x isang araw`];
      if (durationDays !== null) {
        parts.push(`${durationDays} araw`);
      }
      return parts.join(" · ");
    },

    tabPrice: "Presyo",
    tabHowToTake: "Paano Inumin",
    tabPharmacy: "Botika",
    howToTakeHeading: "Paano inumin",
    fontSizeToggleLabel: "Palitan ang laki ng letra",
    nearbyPharmaciesHeading: "Mga botika sa malapit",
    offlineMessage: "Walang internet. Hindi makikita ang mapa.",
    locationDeniedMessage:
      "Hindi namin makuha ang lokasyon mo. Ipinapakita ang paligid ng DLSU Manila.",
    filter24h: "Bukas 24 oras",
    btnShowAtPharmacy: "Ipakita sa Botika",

    estimatedSavings: "Tinatayang matitipid",
    cheaperGenericBadge: (percent: number) => `${percent}% mas mura`,
    ifBuyingGeneric: "kung generic ang bibilhin",
    brandedLabel: "Branded",
    genericCheapestLabel: "Generic (pinakamura)",
    notEnoughPrices: "Wala pang sapat na presyo para makumpara.",
    priceEstimateDisclaimer: "Tinatayang presyo lamang. Maaaring iba sa botika.",
    priceDateAsOf: (date: string) => `Presyo as of ${date}`,
    priceDateMissing: "Petsa ng presyo: hindi pa nakasaad",
    excludedItemNotice: (genericName: string, strength: string) =>
      `Hindi kasama: ${genericName} ${strength} (walang presyo)`,

    cheapestBadge: "Pinakamura",
    perPiece: (price: string) => `${price} / piraso`,
    noPriceYet: "Walang presyo pa",

    slotMorning: "Umaga",
    slotNoon: "Tanghali",
    slotEvening: "Gabi",
    unitDose: (quantity: number, form: string) => `${quantity} ${form}`,

    listenNotSupported: "Hindi sinusuportahan ng device na ito ang pakikinig.",
    btnListen: "Pakinggan",
    btnStop: "Itigil",

    distanceNote: "humigit-kumulang, tuwid na linya",
    usuallyInStock: "Karaniwang may stock",
    callToConfirm: "Tumawag para makumpirma",
    btnCall: "Tawagan",
    noPhoneYet: "Walang numero pa",
    btnCopyInquiry: "Kopyahin ang tanong",
    btnCopied: "Nakopya",

    counterGreeting: "Magandang araw po!",
    counterPrompt: "Pabili po ng generic na katumbas ng nasa reseta:",
    counterPharmacistNote: "Pakikumpirma po sa parmasyutiko.",
    counterClose: "Isara",
  },
};
