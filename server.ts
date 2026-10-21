// server.ts
import "dotenv/config";
import express, { type Request, type Response } from "express";
import path from "path";
import { fileURLToPath } from "url";
import { createServer as createViteServer } from "vite";
import { GoogleGenAI, ThinkingLevel, Type, type Schema } from "@google/genai";
import { lookupCatalogItem } from "./src/lib/catalog.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const EXTRACTION_PROMPT = `List every medication on this prescription slip.
PHILIPPINE MEDICAL PRESCRIPTION RULES & SHORTHAND:
1. Shorthand Frequencies & Administration:
   - OD = once daily (umaga), BID = twice daily (umaga, gabi), TID = 3x daily (umaga, tanghali, gabi), QID = 4x daily, PRN = as needed, HS = at bedtime (gabi).
   - PC = post cibum (pagkatapos kumain), AC = ante cibum (bago kumain), PO = per orem (oral).
   - Quantities: #14, #30, No. 30, x14 means total units to dispense.
   - Forms: tab (tablet), cap (capsule), syr/susp (syrup/suspension), neb (nebule).
2. Deciphering Doctor Cursive & Standard PH Formulations:
   - Philippine doctors frequently write Brand (Generic) or Generic alone.
   - If letters in cursive are ambiguous, cross-reference standard commercial strengths in the Philippines:
     * Co-Amoxiclav: 625mg, 1g
     * Amoxicillin: 250mg, 500mg
     * Cefuroxime: 500mg, Cefalexin: 500mg
     * Amlodipine: 5mg, 10mg, Losartan: 50mg, 100mg
     * Metformin: 500mg, 850mg, Glimepiride: 2mg
     * Paracetamol: 500mg, Mefenamic Acid: 500mg, Ibuprofen: 200mg/400mg
     * Atorvastatin: 20mg, 40mg, Simvastatin: 20mg
     * Cetirizine: 10mg, Omeprazole: 20mg, Salbutamol: 2mg
   - Do not stop after the first few lines; prescriptions can have 15 or more medications.
   - Never invent medicines not written. If any field (genericName, strength, quantity, sig) is difficult to read, include the item and list the field names in "uncertainFields".
   - Never drop a medication line; transcribe every single medication line found.`;

const extractionSchema: Schema = {
  type: Type.OBJECT,
  properties: {
    lines: {
      type: Type.ARRAY,
      description:
        "Every handwritten or printed line or numbered entry on the prescription slip in reading order.",
      items: {
        type: Type.OBJECT,
        properties: {
          rawText: {
            type: Type.STRING,
            description: "Exact text transcribed from the line on the slip.",
          },
          isMedication: {
            type: Type.BOOLEAN,
            description: "True if this line prescribes a medication.",
          },
        },
        required: ["rawText", "isMedication"],
      },
    },
    medicationLineCount: {
      type: Type.INTEGER,
      description: "Total number of lines where isMedication is true.",
    },
    doctorName: {
      type: Type.STRING,
      nullable: true,
    },
    prcNumber: {
      type: Type.STRING,
      nullable: true,
    },
    dateOnSlip: {
      type: Type.STRING,
      nullable: true,
    },
    readScore: {
      type: Type.INTEGER,
      nullable: true,
    },
    items: {
      type: Type.ARRAY,
      description:
        "One entry for every line where isMedication is true. No max limit (supports 15+ medications).",
      items: {
        type: Type.OBJECT,
        properties: {
          writtenAs: {
            type: Type.STRING,
          },
          sigRaw: {
            type: Type.STRING,
          },
          brandName: {
            type: Type.STRING,
            nullable: true,
          },
          genericName: {
            type: Type.STRING,
          },
          strength: {
            type: Type.STRING,
          },
          form: {
            type: Type.STRING,
          },
          quantity: {
            type: Type.INTEGER,
          },
          dosesPerDay: {
            type: Type.INTEGER,
          },
          durationDays: {
            type: Type.INTEGER,
            nullable: true,
          },
          slots: {
            type: Type.ARRAY,
            items: {
              type: Type.STRING,
            },
          },
          mealNote: {
            type: Type.STRING,
            nullable: true,
          },
          mealNoteEn: {
            type: Type.STRING,
            nullable: true,
          },
          noteTl: {
            type: Type.STRING,
            nullable: true,
          },
          noteEn: {
            type: Type.STRING,
            nullable: true,
          },
          uncertainFields: {
            type: Type.ARRAY,
            items: {
              type: Type.STRING,
            },
          },
        },
        required: [
          "writtenAs",
          "sigRaw",
          "brandName",
          "genericName",
          "strength",
          "form",
          "quantity",
          "dosesPerDay",
          "durationDays",
          "slots",
          "mealNote",
          "mealNoteEn",
          "noteTl",
          "noteEn",
          "uncertainFields",
        ],
      },
    },
  },
  required: [
    "lines",
    "medicationLineCount",
    "doctorName",
    "prcNumber",
    "dateOnSlip",
    "readScore",
    "items",
  ],
};

function stripJsonFences(text: string): string {
  const trimmed = text.trim();
  if (trimmed.startsWith("```")) {
    return trimmed
      .replace(/^```(?:json)?\s*/i, "")
      .replace(/\s*```$/, "")
      .trim();
  }
  return trimmed;
}

// Rate limiter: Keyed by both Install ID and IP (Rule 6 & Rule 8)
const rateLimitMap = new Map<string, { count: number; resetAt: number }>();

function checkRateLimit(key: string, maxRequests = 15, windowMs = 60000): boolean {
  const now = Date.now();
  const entry = rateLimitMap.get(key);
  if (!entry || now > entry.resetAt) {
    rateLimitMap.set(key, { count: 1, resetAt: now + windowMs });
    return true;
  }
  if (entry.count >= maxRequests) {
    return false;
  }
  entry.count += 1;
  return true;
}

setInterval(() => {
  const now = Date.now();
  for (const [key, entry] of rateLimitMap.entries()) {
    if (now > entry.resetAt) {
      rateLimitMap.delete(key);
    }
  }
}, 300000);

async function startServer() {
  const app = express();
  app.set("trust proxy", 1);
  // Support full quality scan images up to 20MB
  app.use(express.json({ limit: "25mb" }));
  app.use(express.text({ type: ["text/csv", "text/plain"], limit: "5mb" }));

  // CORS support for LAN dev & mobile client access
  app.use((req, res, next) => {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, x-install-id");
    if (req.method === "OPTIONS") {
      res.sendStatus(200);
      return;
    }
    next();
  });

  app.get("/healthz", (_req: Request, res: Response) => {
    res.json({
      status: "ok",
      uptimeSeconds: Math.floor(process.uptime()),
      timestamp: new Date().toISOString(),
    });
  });

  // POST /api/scan
  app.post("/api/scan", async (req: Request, res: Response) => {
    const clientIp =
      (req.headers["x-forwarded-for"] as string)?.split(",")[0]?.trim() ||
      req.ip ||
      "unknown";

    const installId = (req.headers["x-install-id"] as string)?.trim() || "no-install-id";
    const rateLimitKey = `${installId}:${clientIp}`;

    const { mimeType, base64Data, deepScan, language } = req.body as {
      mimeType?: string;
      base64Data?: string;
      deepScan?: boolean;
      language?: "en" | "fil";
    };

    const lang = language === "fil" ? "fil" : "en";

    // 1. Rate Limiting Check (Rule 6 & 8)
    if (!checkRateLimit(rateLimitKey)) {
      res.status(429).json({
        error:
          lang === "fil"
            ? "Masyadong maraming request. Maghintay ng 1 minuto bago mag-scan ulit."
            : "Too many requests. Please wait 1 minute before scanning again.",
      });
      return;
    }

    // 2. Content Type Validation (Rule 8)
    const validMimes = ["image/jpeg", "image/png", "image/webp"];
    if (!mimeType || !validMimes.includes(mimeType.toLowerCase())) {
      res.status(400).json({
        error:
          lang === "fil"
            ? "Hindi suportadong format ng larawan. Gamitin ang JPEG o PNG."
            : "Unsupported image format. Please send a JPEG or PNG image.",
      });
      return;
    }

    // 3. Image Size Validation (Rule 8)
    if (!base64Data || base64Data.length < 100) {
      res.status(400).json({
        error: lang === "fil" ? "Nawawala ang larawan ng reseta" : "Missing prescription image data",
      });
      return;
    }

    if (base64Data.length > 25 * 1024 * 1024) {
      res.status(413).json({
        error:
          lang === "fil"
            ? "Masyadong malaki ang larawan. Hanggang 20MB lamang ang pinapayagan."
            : "Image payload exceeds the maximum 20MB limit.",
      });
      return;
    }

    if (!process.env.GEMINI_API_KEY) {
      res.status(500).json({
        error:
          lang === "fil"
            ? "Nawawala ang GEMINI_API_KEY sa .env file. Mangyaring i-set up ang iyong Gemini API key para magamit ang AI scan."
            : "Missing GEMINI_API_KEY in .env file. Please set up your Gemini API key to use AI scan.",
      });
      return;
    }

    const startMs = Date.now();
    const isDeep = Boolean(deepScan);

    // Rule 4: DO NOT log request images or extracted text!
    console.log(
      `[ResetaPH Scan] Processing scan request from IP=${clientIp}, InstallID=${installId.slice(0, 8)}... (${mimeType}, deepScan=${isDeep})`
    );

    try {
      const ai = new GoogleGenAI({
        apiKey: process.env.GEMINI_API_KEY,
        httpOptions: {
          headers: {
            "User-Agent": "aistudio-build",
          },
        },
      });

      const langInstruction =
        "Bilingual notes: Provide 'mealNote' (Filipino meal timing, e.g. 'Pagkatapos kumain') and 'mealNoteEn' (English meal timing, e.g. 'Take after meals'). Provide 'noteTl' (Filipino patient instructions or reminders, e.g. 'Tapusin ang buong reseta') and 'noteEn' (English patient instructions or reminders, e.g. 'Complete the full course of antibiotics').";

      const promptText = isDeep
        ? `${EXTRACTION_PROMPT}\n\nDEEP DECIPHER MODE: Examine ambiguous cursive handwriting strokes meticulously. Cross-reference doctor abbreviations and letter patterns with standard Philippine FDA drug formulations.\n\n${langInstruction}\n\nFirst transcribe every numbered medication block or line on the slip in reading order into \`lines\` with \`rawText\` and \`isMedication\`. Set \`medicationLineCount\` to the count of lines where \`isMedication\` is true. Then populate \`items\` with one entry for every line where \`isMedication\` is true. Never drop a medication line silently—if any field (genericName, strength, quantity, sig) is hard to read or missing, still include the item and list the uncertain field names in \`uncertainFields\`. Use valid slots from ["umaga", "tanghali", "gabi"].`
        : `${EXTRACTION_PROMPT}\n\n${langInstruction}\n\nFirst transcribe every numbered medication block or line on the slip in reading order into \`lines\` with \`rawText\` and \`isMedication\`. Set \`medicationLineCount\` to the count of lines where \`isMedication\` is true. Then populate \`items\` with one entry for every line where \`isMedication\` is true. Never drop a medication line silently—if any field (genericName, strength, quantity, sig) is hard to read or missing, still include the item and list the uncertain field names in \`uncertainFields\`. Use valid slots from ["umaga", "tanghali", "gabi"].`;

      // 45-second request timeout (Rule 8)
      const timeoutPromise = new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error("REQUEST_TIMEOUT")), 45000)
      );

      const geminiPromise = ai.models.generateContent({
        model: "gemini-2.5-flash",
        contents: {
          parts: [
            {
              inlineData: {
                mimeType,
                data: base64Data,
              },
            },
            {
              text: promptText,
            },
          ],
        },
        config: {
          responseMimeType: "application/json",
          responseSchema: extractionSchema,
          maxOutputTokens: 16384, // Rule 6: High token limit for 15+ medications
          thinkingConfig: {
            thinkingLevel: isDeep ? ThinkingLevel.HIGH : ThinkingLevel.LOW,
          },
        },
      });

      const response = await Promise.race([geminiPromise, timeoutPromise]);

      // Rule 6: Never parse truncated JSON
      const candidate = response.candidates?.[0];
      if (candidate?.finishReason === "MAX_TOKENS") {
        console.warn(`[ResetaPH Scan] Scan output was truncated by token limit.`);
        res.status(422).json({
          error:
            lang === "fil"
              ? "Naputol ang resulta ng AI scan dahil sa sobrang haba. Pakisubukan muling i-scan ang reseta nang mas malapit."
              : "Scan response was truncated by the model. Please scan the prescription closer or in parts.",
        });
        return;
      }

      const rawText = stripJsonFences(response.text ?? "{}");
      let parsed: {
        lines?: Array<{ rawText: string; isMedication: boolean }>;
        medicationLineCount?: number;
        doctorName?: string | null;
        prcNumber?: string | null;
        dateOnSlip?: string | null;
        readScore?: number | null;
        items?: Array<{
          writtenAs: string;
          sigRaw?: string;
          brandName?: string | null;
          genericName: string;
          strength: string;
          form: string;
          quantity: number;
          dosesPerDay: number;
          durationDays?: number | null;
          slots: string[];
          mealNote?: string | null;
          mealNoteEn?: string | null;
          noteTl?: string | null;
          noteEn?: string | null;
          uncertainFields?: string[];
        }>;
      };

      try {
        parsed = JSON.parse(rawText);
      } catch (_jsonErr) {
        console.error(`[ResetaPH Scan] Failed to parse model JSON.`);
        res.status(422).json({
          error:
            lang === "fil"
              ? "Hindi mabasa ang buong structure ng reseta. Pakisigurado na malinaw ang kuha ng camera."
              : "Could not parse prescription structure. Please ensure the prescription photo is clear and well-lit.",
        });
        return;
      }

      const rawItems = Array.isArray(parsed.items) ? parsed.items : [];
      const medicationLineCount = parsed.medicationLineCount ?? rawItems.length;

      // Rule 5: Line count mismatch detection
      const lineCountMismatch = medicationLineCount !== rawItems.length;
      const mismatchNotice = lineCountMismatch
        ? lang === "fil"
          ? "May hindi nabasa na gamot. Pakisigurado ang reseta."
          : "Some medication lines were not fully recognized. Please verify the prescription carefully."
        : null;

      // Rule 7: Validate each drug name against the drug table. Return matched catalog ID or null.
      // Unknown names are flagged "Pakisigurado", never auto-corrected, never dropped.
      // Return NO prices from this endpoint!
      const validatedItems = rawItems.map((item, index) => {
        const matched =
          lookupCatalogItem(item.genericName, item.strength) ||
          (item.brandName ? lookupCatalogItem(item.brandName, item.strength) : undefined) ||
          lookupCatalogItem(item.writtenAs, item.strength);

        const uncertain = Array.isArray(item.uncertainFields) ? [...item.uncertainFields] : [];
        const safetyWarnings: Array<{
          code: string;
          severity: "INFO" | "CAUTION" | "WARNING";
          messageTl: string;
          messageEn: string;
        }> = [];

        let catalogId: string | null = null;
        if (matched) {
          catalogId = matched.genericName;
        } else {
          catalogId = null;
          if (!uncertain.includes("genericName")) {
            uncertain.push("genericName");
          }
          safetyWarnings.push({
            code: "UNMATCHED_DRUG",
            severity: "CAUTION",
            messageTl: `Pakisigurado sa doktor o botika: Hindi nakatugma sa official drug table ang "${item.genericName || item.writtenAs}".`,
            messageEn: `Pakisigurado: "${item.genericName || item.writtenAs}" was not found in our drug catalog. Confirm with your doctor or pharmacist.`,
          });
        }

        return {
          id: `item-${index + 1}-${Date.now()}`,
          writtenAs: item.writtenAs,
          brandName: item.brandName ?? null,
          genericName: item.genericName, // Keep original, never auto-correct
          strength: item.strength,
          form: item.form,
          quantity: item.quantity > 0 ? item.quantity : 1,
          dosesPerDay: item.dosesPerDay > 0 ? item.dosesPerDay : 1,
          durationDays: item.durationDays ?? null,
          slots: item.slots && item.slots.length > 0 ? item.slots : ["umaga"],
          mealNoteTl: item.mealNote ?? null,
          mealNoteEn: item.mealNoteEn ?? null,
          noteTl: item.noteTl ?? null,
          noteEn: item.noteEn ?? null,
          uncertainFields: uncertain,
          catalogId, // Matched catalog ID or null (Rule 7)
          isMatched: Boolean(matched),
          safetyWarnings, // Flagged "Pakisigurado" if unknown
          // ZERO PRICES RETURNED HERE (Rule 2 & Rule 7)
        };
      });

      console.log(
        `[ResetaPH Scan] Completed in ${Date.now() - startMs}ms: ${validatedItems.length} items extracted, lineCountMismatch=${lineCountMismatch}`
      );

      res.json({
        id: `scan-${Date.now()}`,
        lines: parsed.lines || [],
        medicationLineCount,
        lineCountMismatch,
        mismatchNotice,
        doctorName: parsed.doctorName ?? null,
        prcNumber: parsed.prcNumber ?? null,
        dateOnSlip: parsed.dateOnSlip ?? null,
        readScore: parsed.readScore ?? 85,
        items: validatedItems,
        scannedAtTimestamp: Date.now(),
      });
    } catch (error) {
      const err = error as Error;
      const isTimeout = err.message === "REQUEST_TIMEOUT";
      const statusCode = isTimeout ? 504 : 500;
      const message = isTimeout
        ? lang === "fil"
          ? "Nag-timeout ang request sa AI server. Pakisubukan muli."
          : "Request to AI server timed out. Please try again."
        : err.message || "Failed to analyze prescription";

      console.error(`[ResetaPH Scan] Error after ${Date.now() - startMs}ms (status ${statusCode})`);
      res.status(statusCode).json({ error: message });
    }
  });

  // ==========================================
  // Price Observations Store (Stage 4 / Rule 2)
  // Structure: (drug, brand, chain, unit price, observed_on, source, evidence note)
  // ==========================================
  interface ServerPriceObservation {
    drug: string;
    brand?: string;
    chain: string;
    unitPrice: number;
    observedOn: string;
    source: string;
    evidenceNote: string;
    isSample: boolean;
  }

  const observedPricesStore: ServerPriceObservation[] = [];

  // GET /api/prices
  app.get("/api/prices", (req: Request, res: Response) => {
    const drugQuery = (req.query.drug as string)?.trim().toLowerCase();
    const results = drugQuery
      ? observedPricesStore.filter(
          (o) =>
            o.drug.toLowerCase() === drugQuery ||
            (o.brand && o.brand.toLowerCase() === drugQuery)
        )
      : observedPricesStore;

    res.json({
      count: results.length,
      isSample: results.length === 0 || results.every((o) => o.isSample),
      observations: results,
    });
  });

  // POST /api/prices/import-csv
  app.post("/api/prices/import-csv", (req: Request, res: Response) => {
    const csv = typeof req.body === "string" ? req.body : "";
    if (!csv.trim()) {
      return res.status(400).json({ error: "Empty CSV content" });
    }

    const lines = csv.split(/\r?\n/).map((l) => l.trim()).filter((l) => l.length > 0);
    if (lines.length < 2) {
      return res.status(400).json({ error: "CSV must contain header and at least one data row" });
    }

    const header = lines[0].toLowerCase().split(",").map((c) => c.trim().replace(/^"|"$/g, ""));
    const drugIdx = header.indexOf("drug");
    const brandIdx = header.indexOf("brand");
    const chainIdx = header.indexOf("chain");
    const priceIdx = header.indexOf("unit_price");
    const dateIdx = header.indexOf("observed_on");
    const sourceIdx = header.indexOf("source");
    const evidenceIdx = header.indexOf("evidence_note");
    const isSampleIdx = header.indexOf("is_sample");

    if (drugIdx === -1 || chainIdx === -1 || priceIdx === -1 || dateIdx === -1) {
      return res.status(400).json({
        error: "Missing required CSV columns: drug, chain, unit_price, observed_on",
      });
    }

    let importedCount = 0;
    const warnings: string[] = [];

    for (let i = 1; i < lines.length; i++) {
      const line = lines[i];
      if (line.startsWith("#")) continue;

      const tokens = line.split(",").map((t) => t.trim().replace(/^"|"$/g, ""));
      const drug = tokens[drugIdx];
      const chain = tokens[chainIdx];
      const unitPrice = parseFloat(tokens[priceIdx]);
      const observedOn = tokens[dateIdx];
      const brand = brandIdx !== -1 ? tokens[brandIdx] || undefined : undefined;
      const source = sourceIdx !== -1 ? tokens[sourceIdx] || "CSV Import" : "CSV Import";
      const evidenceNote = evidenceIdx !== -1 ? tokens[evidenceIdx] || "" : "";
      const isSample = isSampleIdx !== -1 ? tokens[isSampleIdx]?.toLowerCase() === "true" : false;

      if (!drug || !chain || isNaN(unitPrice) || unitPrice <= 0 || !observedOn) {
        continue;
      }

      // Validate drug against drug catalog
      const matched = lookupCatalogItem(drug) || (brand ? lookupCatalogItem(brand) : undefined);
      if (!matched) {
        warnings.push(`Row ${i}: "${drug}" is not in the recognized drug catalog.`);
      }

      observedPricesStore.push({
        drug: matched ? matched.genericName : drug,
        brand,
        chain,
        unitPrice,
        observedOn,
        source,
        evidenceNote,
        isSample,
      });
      importedCount++;
    }

    res.json({
      success: true,
      importedCount,
      totalStored: observedPricesStore.length,
      warnings,
    });
  });

  if (process.env.NODE_ENV === "production") {
    const distPath = path.resolve(__dirname, "dist");
    app.use(express.static(distPath));
    app.get("*", (_req, res) => {
      res.sendFile(path.join(distPath, "index.html"));
    });
  } else {
    const vite = await createViteServer({
      server: {
        middlewareMode: true,
        hmr: false,
        watch: null,
      },
      appType: "spa",
    });
    app.use(vite.middlewares);
  }

  const port = process.env.PORT ? parseInt(process.env.PORT, 10) : 3000;
  const host = process.env.HOST || "0.0.0.0";
  const server = app.listen(port, host, () => {
    console.log(`\n==================================================`);
    console.log(`  ResetaPH Backend Server Running`);
    console.log(`  Local:   http://localhost:${port}`);
    console.log(`  Network: http://192.168.0.28:${port}`);
    console.log(`  Binding: ${host}:${port}`);
    console.log(`==================================================\n`);
  });

  const shutdown = () => {
    console.log("Shutting down server gracefully...");
    server.close(() => process.exit(0));
  };
  process.on("SIGTERM", shutdown);
  process.on("SIGINT", shutdown);
}

startServer();
