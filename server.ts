// server.ts
import "dotenv/config";
import express from "express";
import path from "path";
import { fileURLToPath } from "url";
import { createServer as createViteServer } from "vite";
import { GoogleGenAI, ThinkingLevel, Type, type Schema } from "@google/genai";

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
   - Do not stop after the first few lines; prescriptions typically have 2 to 6 medications.
   - Do not invent medicines not written. If any field (genericName, strength, quantity, sig) is difficult to read, include the item and list the field names in "uncertainFields".`;

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
        "One entry for every line where isMedication is true. No maxItems limit.",
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
          noteTl: {
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
          "noteTl",
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

// Rate limiter: Max 15 scan requests per minute per IP address
const scanRateLimitMap = new Map<string, { count: number; resetAt: number }>();

function checkRateLimit(ip: string, maxRequests = 15, windowMs = 60000): boolean {
  const now = Date.now();
  const entry = scanRateLimitMap.get(ip);
  if (!entry || now > entry.resetAt) {
    scanRateLimitMap.set(ip, { count: 1, resetAt: now + windowMs });
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
  for (const [ip, entry] of scanRateLimitMap.entries()) {
    if (now > entry.resetAt) {
      scanRateLimitMap.delete(ip);
    }
  }
}, 300000);

async function startServer() {
  const app = express();
  app.set("trust proxy", 1);
  app.use(express.json({ limit: "10mb" }));

  app.get("/healthz", (_req, res) => {
    res.json({
      status: "ok",
      uptimeSeconds: Math.floor(process.uptime()),
      timestamp: new Date().toISOString(),
    });
  });

  app.post("/api/scan", async (req, res) => {
    const clientIp =
      (req.headers["x-forwarded-for"] as string)?.split(",")[0]?.trim() ||
      req.ip ||
      "unknown";

    if (!checkRateLimit(clientIp)) {
      res.status(429).json({
        error: "Masyadong maraming request. Maghintay ng 1 minuto bago mag-scan ulit.",
      });
      return;
    }

    const startMs = Date.now();
    try {
      const { mimeType, base64Data, deepScan } = req.body as {
        mimeType?: string;
        base64Data?: string;
        deepScan?: boolean;
      };

      if (!mimeType || !base64Data) {
        res.status(400).json({ error: "Missing image data" });
        return;
      }

      const isDeep = Boolean(deepScan);
      console.log(
        `[ResetaPH Scan] Starting Gemini extraction (${mimeType}, ${Math.round(
          base64Data.length / 1024
        )} KB base64, deepScan=${isDeep})...`
      );

      const ai = new GoogleGenAI({
        apiKey: process.env.GEMINI_API_KEY,
        httpOptions: {
          headers: {
            "User-Agent": "aistudio-build",
          },
        },
      });

      const promptText = isDeep
        ? `${EXTRACTION_PROMPT}\n\nDEEP DECIPHER MODE: Examine ambiguous cursive handwriting strokes meticulously. Cross-reference doctor abbreviations and letter patterns with standard Philippine FDA drug formulations.\n\nFirst transcribe every numbered medication block or line on the slip in reading order into \`lines\` with \`rawText\` and \`isMedication\`. Set \`medicationLineCount\` to the count of lines where \`isMedication\` is true. Then populate \`items\` with one entry for every line where \`isMedication\` is true. Never drop a medication line silently—if any field (genericName, strength, quantity, sig) is hard to read or missing, still include the item and list the uncertain field names in \`uncertainFields\`. Use valid slots from ["umaga", "tanghali", "gabi"].`
        : `${EXTRACTION_PROMPT}\n\nFirst transcribe every numbered medication block or line on the slip in reading order into \`lines\` with \`rawText\` and \`isMedication\`. Set \`medicationLineCount\` to the count of lines where \`isMedication\` is true. Then populate \`items\` with one entry for every line where \`isMedication\` is true. Never drop a medication line silently—if any field (genericName, strength, quantity, sig) is hard to read or missing, still include the item and list the uncertain field names in \`uncertainFields\`. Use valid slots from ["umaga", "tanghali", "gabi"].`;

      const response = await ai.models.generateContent({
        model: "gemini-3.8-flash",
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
          maxOutputTokens: 16384,
          thinkingConfig: {
            thinkingLevel: isDeep ? ThinkingLevel.HIGH : ThinkingLevel.LOW,
          },
        },
      });

      const rawText = stripJsonFences(response.text ?? "{}");
      const parsed = JSON.parse(rawText) as {
        items?: unknown[];
        medicationLineCount?: number;
      };

      console.log(
        `[ResetaPH Scan] Completed in ${Date.now() - startMs}ms. Raw model response:`,
        rawText
      );
      console.log(
        "[ResetaPH Scan] Parsed item count:",
        Array.isArray(parsed.items) ? parsed.items.length : 0,
        "Medication line count:",
        parsed.medicationLineCount
      );

      res.json({ rawText, extraction: parsed });
    } catch (error) {
      const message =
        error instanceof Error ? error.message : "Failed to analyze prescription";
      console.error(`[ResetaPH Scan] Error after ${Date.now() - startMs}ms:`, message);
      res.status(500).json({ error: message });
    }
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
  const server = app.listen(port, "0.0.0.0", () => {
    console.log(`Server listening on http://0.0.0.0:${port}`);
  });

  const shutdown = () => {
    console.log("Shutting down server gracefully...");
    server.close(() => process.exit(0));
  };
  process.on("SIGTERM", shutdown);
  process.on("SIGINT", shutdown);
}

startServer();
