package ph.reseta.pricing

import ph.reseta.model.PharmacyChain
import ph.reseta.model.PriceObservation

/**
 * Pure Kotlin CSV parser for price_observations (Rule 1 & Rule 2).
 *
 * Expected CSV columns (header row required):
 * drug,brand,chain,unit_price,observed_on,source,evidence_note,is_sample
 *
 * Example:
 * Amoxicillin,Amoxil,Mercury,8.50,2026-10-15,Physical Survey,Receipt #12345,false
 * Paracetamol,,TGP,2.00,2026-10-10,DOH-DPRI,Bulletin 2026-08,false
 */
object PriceObservationCsvParser {

    private const val HEADER_DRUG = "drug"
    private const val HEADER_BRAND = "brand"
    private const val HEADER_CHAIN = "chain"
    private const val HEADER_UNIT_PRICE = "unit_price"
    private const val HEADER_OBSERVED_ON = "observed_on"
    private const val HEADER_SOURCE = "source"
    private const val HEADER_EVIDENCE_NOTE = "evidence_note"
    private const val HEADER_IS_SAMPLE = "is_sample"

    fun parse(csvContent: String): List<PriceObservation> {
        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return emptyList()

        val headerTokens = parseCsvRow(lines.first()).map { it.lowercase().trim() }
        val drugIdx = headerTokens.indexOf(HEADER_DRUG)
        val brandIdx = headerTokens.indexOf(HEADER_BRAND)
        val chainIdx = headerTokens.indexOf(HEADER_CHAIN)
        val priceIdx = headerTokens.indexOf(HEADER_UNIT_PRICE)
        val dateIdx = headerTokens.indexOf(HEADER_OBSERVED_ON)
        val sourceIdx = headerTokens.indexOf(HEADER_SOURCE)
        val evidenceIdx = headerTokens.indexOf(HEADER_EVIDENCE_NOTE)
        val isSampleIdx = headerTokens.indexOf(HEADER_IS_SAMPLE)

        if (drugIdx == -1 || chainIdx == -1 || priceIdx == -1 || dateIdx == -1) {
            throw IllegalArgumentException(
                "Invalid CSV header. Must include at least: drug, chain, unit_price, observed_on"
            )
        }

        val results = mutableListOf<PriceObservation>()

        for (i in 1 until lines.size) {
            val line = lines[i]
            if (line.startsWith("#")) continue // Skip comment rows

            val tokens = parseCsvRow(line)
            if (tokens.isEmpty() || tokens.all { it.isBlank() }) continue

            val drug = tokens.getOrNull(drugIdx)?.trim().orEmpty()
            if (drug.isEmpty()) continue

            val brand = if (brandIdx != -1) tokens.getOrNull(brandIdx)?.trim()?.ifBlank { null } else null

            val chainRaw = tokens.getOrNull(chainIdx)?.trim().orEmpty()
            val chain = parseChain(chainRaw) ?: continue

            val priceRaw = tokens.getOrNull(priceIdx)?.trim().orEmpty()
            val unitPrice = priceRaw.toDoubleOrNull() ?: continue
            if (unitPrice <= 0.0) continue

            val observedOn = tokens.getOrNull(dateIdx)?.trim().orEmpty()
            if (observedOn.isEmpty()) continue

            val source = if (sourceIdx != -1) tokens.getOrNull(sourceIdx)?.trim().orEmpty() else "Unknown Source"
            val evidenceNote = if (evidenceIdx != -1) tokens.getOrNull(evidenceIdx)?.trim().orEmpty() else ""
            val isSample = if (isSampleIdx != -1) {
                tokens.getOrNull(isSampleIdx)?.trim()?.toBooleanStrictOrNull() ?: false
            } else {
                false
            }

            results.add(
                PriceObservation(
                    drug = drug,
                    brand = brand,
                    chain = chain,
                    unitPrice = unitPrice,
                    observedOn = observedOn,
                    source = source,
                    evidenceNote = evidenceNote,
                    isSample = isSample
                )
            )
        }

        return results
    }

    fun toCsv(observations: List<PriceObservation>): String {
        val sb = StringBuilder()
        sb.appendLine("drug,brand,chain,unit_price,observed_on,source,evidence_note,is_sample")
        for (obs in observations) {
            sb.appendLine(
                listOf(
                    escapeCsv(obs.drug),
                    escapeCsv(obs.brand ?: ""),
                    obs.chain.name,
                    "%.2f".format(obs.unitPrice),
                    escapeCsv(obs.observedOn),
                    escapeCsv(obs.source),
                    escapeCsv(obs.evidenceNote),
                    obs.isSample.toString()
                ).joinToString(",")
            )
        }
        return sb.toString()
    }

    private fun parseChain(raw: String): PharmacyChain? {
        val clean = raw.trim().uppercase()
        return PharmacyChain.values().firstOrNull {
            it.name == clean ||
            it.shortName.uppercase() == clean ||
            it.displayName.uppercase() == clean
        }
    }

    private fun parseCsvRow(row: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < row.length) {
            val c = row[i]
            when {
                c == '\"' -> {
                    if (inQuotes && i + 1 < row.length && row[i + 1] == '\"') {
                        sb.append('\"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == ',' && !inQuotes -> {
                    tokens.add(sb.toString().trim())
                    sb.clear()
                }
                else -> {
                    sb.append(c)
                }
            }
            i++
        }
        tokens.add(sb.toString().trim())
        return tokens
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
