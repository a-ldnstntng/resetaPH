// server.ts
import "dotenv/config";
import express from "express";
import path from "path";
import { fileURLToPath } from "url";
import { createServer as createViteServer } from "vite";
import { GoogleGenAI, ThinkingLevel, Type, type Schema } from "@google/genai";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const EXTRACTION_PROMPT =
  "List every medication on this prescription. Prescriptions often have 4 to 10 lines. Do not stop after the first few. Include lines that are hard to read and mark them uncertain. Do not invent medicines that are not written.";

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

async function startServer() {
  const app = express();
  app.use(express.json({ limit: "50mb" }));

  app.post("/api/scan", async (req, res) => {
    const startMs = Date.now();
    try {
      const { mimeType, base64Data } = req.body as {
        mimeType?: string;
        base64Data?: string;
      };

      if (!mimeType || !base64Data) {
        res.status(400).json({ error: "Missing image data" });
        return;
      }

      console.log(
        `[ResetaPH Scan] Starting Gemini extraction (${mimeType}, ${Math.round(
          base64Data.length / 1024
        )} KB base64)...`
      );

      const ai = new GoogleGenAI({
        apiKey: process.env.GEMINI_API_KEY,
        httpOptions: {
          headers: {
            "User-Agent": "aistudio-build",
          },
        },
      });

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
              text: `${EXTRACTION_PROMPT}\n\nFirst transcribe every numbered medication block or line on the slip in reading order into \`lines\` with \`rawText\` and \`isMedication\`. Set \`medicationLineCount\` to the count of lines where \`isMedication\` is true. Then populate \`items\` with one entry for every line where \`isMedication\` is true. Never drop a medication line silently—if any field (genericName, strength, quantity, sig) is hard to read or missing, still include the item and list the uncertain field names in \`uncertainFields\`. Use valid slots from ["umaga", "tanghali", "gabi"].`,
            },
          ],
        },
        config: {
          responseMimeType: "application/json",
          responseSchema: extractionSchema,
          maxOutputTokens: 16384,
          thinkingConfig: {
            thinkingLevel: ThinkingLevel.LOW,
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

  const port = 3000;
  app.listen(port, "0.0.0.0", () => {
    console.log(`Server listening on http://0.0.0.0:${port}`);
  });
}

startServer();
