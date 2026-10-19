package ph.reseta.catalog

import ph.reseta.model.DatedPrice
import ph.reseta.model.DrugCatalogEntry
import ph.reseta.model.PharmacyChain

/**
 * Pure Kotlin Sample Drug Catalog for the Philippines (Rule 1 & Rule 2).
 * NOTE: All prices and values in this catalog are purely synthetic sample data
 * created for design mockups and local preview. No real-world field survey was conducted.
 */
object SamplePhilippineDrugCatalog {
    const val SAMPLE_SOURCE = "Sample Mock Data (Unsurveyed)"
    const val SAMPLE_DATE_LABEL = "Sample"

    private fun samplePrice(amount: Double): DatedPrice =
        DatedPrice(
            amountPhp = amount,
            recordedDate = SAMPLE_DATE_LABEL,
            source = SAMPLE_SOURCE,
            isSample = true,
            isEstimate = true
        )

    val entries: List<DrugCatalogEntry> = listOf(
        DrugCatalogEntry(
            genericName = "Co-Amoxiclav",
            brandNames = listOf("Augmentin", "Natravox", "Curam"),
            form = "tablet",
            standardStrength = "625 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(18.00),
                PharmacyChain.WATSONS to samplePrice(24.00),
                PharmacyChain.MERCURY_DRUG to samplePrice(26.00),
                PharmacyChain.SOUTH_STAR to samplePrice(25.00),
                PharmacyChain.ROSE_PHARMACY to samplePrice(25.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(82.00),
                PharmacyChain.WATSONS to samplePrice(84.50)
            ),
            defaultMealNoteTl = "Pagkatapos kumain",
            defaultMealNoteEn = "Take after meals",
            defaultPatientNoteTl = "Inumin ayon sa iniresetang iskedyul ng doktor.",
            defaultPatientNoteEn = "Take strictly as prescribed by your doctor."
        ),
        DrugCatalogEntry(
            genericName = "Amoxicillin",
            brandNames = listOf("Amoxil", "Himox"),
            form = "capsule",
            standardStrength = "500 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(3.50),
                PharmacyChain.WATSONS to samplePrice(5.00),
                PharmacyChain.MERCURY_DRUG to samplePrice(5.50),
                PharmacyChain.SOUTH_STAR to samplePrice(5.25),
                PharmacyChain.ROSE_PHARMACY to samplePrice(5.25)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(14.50),
                PharmacyChain.WATSONS to samplePrice(15.00)
            ),
            defaultMealNoteTl = "Pagkatapos kumain",
            defaultMealNoteEn = "Take after meals",
            defaultPatientNoteTl = "Sundin ang buong iskedyul na inireseta.",
            defaultPatientNoteEn = "Follow the full schedule prescribed."
        ),
        DrugCatalogEntry(
            genericName = "Cefuroxime",
            brandNames = listOf("Zinnat", "Ceftum"),
            form = "tablet",
            standardStrength = "500 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(22.00),
                PharmacyChain.WATSONS to samplePrice(28.00),
                PharmacyChain.MERCURY_DRUG to samplePrice(30.00),
                PharmacyChain.SOUTH_STAR to samplePrice(29.00),
                PharmacyChain.ROSE_PHARMACY to samplePrice(29.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(75.00),
                PharmacyChain.WATSONS to samplePrice(77.00)
            ),
            defaultMealNoteTl = "Kasamang kumain para mas ma-absorb",
            defaultMealNoteEn = "Take with food for better absorption",
            defaultPatientNoteTl = "Tapusin ang buong bilang ng araw na inireseta.",
            defaultPatientNoteEn = "Finish the full prescribed days."
        ),
        DrugCatalogEntry(
            genericName = "Cefalexin",
            brandNames = listOf("Keflex", "Ceporex"),
            form = "capsule",
            standardStrength = "500 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(7.00),
                PharmacyChain.WATSONS to samplePrice(9.50),
                PharmacyChain.MERCURY_DRUG to samplePrice(10.00),
                PharmacyChain.SOUTH_STAR to samplePrice(9.75),
                PharmacyChain.ROSE_PHARMACY to samplePrice(9.75)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(34.00),
                PharmacyChain.WATSONS to samplePrice(35.50)
            ),
            defaultMealNoteTl = "Bago o pagkatapos kumain",
            defaultMealNoteEn = "Before or after meals",
            defaultPatientNoteTl = "Sundin ang regular na pagitan ng oras sa pag-inom.",
            defaultPatientNoteEn = "Maintain regular spacing between doses."
        ),
        DrugCatalogEntry(
            genericName = "Azithromycin",
            brandNames = listOf("Zithromax", "Azyth"),
            form = "tablet",
            standardStrength = "500 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(55.00),
                PharmacyChain.WATSONS to samplePrice(75.00),
                PharmacyChain.MERCURY_DRUG to samplePrice(80.00),
                PharmacyChain.SOUTH_STAR to samplePrice(78.00),
                PharmacyChain.ROSE_PHARMACY to samplePrice(79.00)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(195.00),
                PharmacyChain.WATSONS to samplePrice(200.00)
            ),
            defaultMealNoteTl = "1 oras bago kumain o 2 oras pagkatapos kumain",
            defaultMealNoteEn = "1 hour before or 2 hours after meals",
            defaultPatientNoteTl = "Karaniwang isang beses lamang bawat araw sa parehong oras.",
            defaultPatientNoteEn = "Usually taken once daily at the same time."
        ),
        DrugCatalogEntry(
            genericName = "Paracetamol",
            brandNames = listOf("Biogesic", "Tempra", "Calpol"),
            form = "tablet",
            standardStrength = "500 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(1.50),
                PharmacyChain.WATSONS to samplePrice(2.75),
                PharmacyChain.MERCURY_DRUG to samplePrice(3.00),
                PharmacyChain.SOUTH_STAR to samplePrice(2.50),
                PharmacyChain.ROSE_PHARMACY to samplePrice(2.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(4.75),
                PharmacyChain.WATSONS to samplePrice(5.00)
            ),
            defaultMealNoteTl = "Kahit may laman o walang laman ang tiyan",
            defaultMealNoteEn = "With or without food",
            defaultPatientNoteTl = "Para sa lagnat o pananakit. Maghintay ng hindi bababa sa 4 oras bago ang susunod na inom.",
            defaultPatientNoteEn = "For fever or pain. Allow at least 4 hours between doses."
        ),
        DrugCatalogEntry(
            genericName = "Ibuprofen",
            brandNames = listOf("Advil", "Medicol"),
            form = "softgel capsule",
            standardStrength = "200 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(2.00),
                PharmacyChain.WATSONS to samplePrice(4.50),
                PharmacyChain.MERCURY_DRUG to samplePrice(5.00),
                PharmacyChain.SOUTH_STAR to samplePrice(4.75),
                PharmacyChain.ROSE_PHARMACY to samplePrice(4.75)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(8.50),
                PharmacyChain.WATSONS to samplePrice(9.00)
            ),
            defaultMealNoteTl = "Pagkatapos kumain o kasabay ng gatas",
            defaultMealNoteEn = "After meals or with milk",
            defaultPatientNoteTl = "Para sa pamamaga at pananakit. Mas maiging inumin na may laman ang tiyan.",
            defaultPatientNoteEn = "For pain and inflammation. Take after food to protect stomach lining."
        ),
        DrugCatalogEntry(
            genericName = "Mefenamic Acid",
            brandNames = listOf("Ponstan", "Dolfenal"),
            form = "capsule",
            standardStrength = "500 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(2.50),
                PharmacyChain.WATSONS to samplePrice(5.00),
                PharmacyChain.MERCURY_DRUG to samplePrice(5.50),
                PharmacyChain.SOUTH_STAR to samplePrice(5.00),
                PharmacyChain.ROSE_PHARMACY to samplePrice(5.00)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(24.50),
                PharmacyChain.WATSONS to samplePrice(25.00)
            ),
            defaultMealNoteTl = "Pagkatapos kumain",
            defaultMealNoteEn = "Take after meals",
            defaultPatientNoteTl = "Pampawala ng matinding pananakit. Huwag inumin nang walang laman ang tiyan.",
            defaultPatientNoteEn = "For acute pain. Do not take on an empty stomach."
        ),
        DrugCatalogEntry(
            genericName = "Amlodipine",
            brandNames = listOf("Norvasc", "Amlife"),
            form = "tablet",
            standardStrength = "10 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(2.50),
                PharmacyChain.WATSONS to samplePrice(4.50),
                PharmacyChain.MERCURY_DRUG to samplePrice(5.00),
                PharmacyChain.SOUTH_STAR to samplePrice(4.50),
                PharmacyChain.ROSE_PHARMACY to samplePrice(4.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(22.00),
                PharmacyChain.WATSONS to samplePrice(23.50)
            ),
            defaultMealNoteTl = "Kahit may laman o walang laman ang tiyan",
            defaultMealNoteEn = "With or without food",
            defaultPatientNoteTl = "Maintenance sa alta-presyon. Inumin sa parehong oras araw-araw.",
            defaultPatientNoteEn = "Blood pressure maintenance. Take at the same time each day."
        ),
        DrugCatalogEntry(
            genericName = "Losartan",
            brandNames = listOf("Cozaar", "Lifezar"),
            form = "tablet",
            standardStrength = "50 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(3.50),
                PharmacyChain.WATSONS to samplePrice(6.00),
                PharmacyChain.MERCURY_DRUG to samplePrice(6.50),
                PharmacyChain.SOUTH_STAR to samplePrice(6.25),
                PharmacyChain.ROSE_PHARMACY to samplePrice(6.25)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(28.00),
                PharmacyChain.WATSONS to samplePrice(29.50)
            ),
            defaultMealNoteTl = "Kahit may laman o walang laman ang tiyan",
            defaultMealNoteEn = "With or without food",
            defaultPatientNoteTl = "Pang-kontrol sa presyon ng dugo at pangalaga sa bato.",
            defaultPatientNoteEn = "For blood pressure control and kidney protection."
        ),
        DrugCatalogEntry(
            genericName = "Metformin",
            brandNames = listOf("Glucophage", "Humamet"),
            form = "tablet",
            standardStrength = "500 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(1.75),
                PharmacyChain.WATSONS to samplePrice(3.50),
                PharmacyChain.MERCURY_DRUG to samplePrice(3.75),
                PharmacyChain.SOUTH_STAR to samplePrice(3.50),
                PharmacyChain.ROSE_PHARMACY to samplePrice(3.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(11.50),
                PharmacyChain.WATSONS to samplePrice(12.00)
            ),
            defaultMealNoteTl = "Kasabay o pagkatapos kumain",
            defaultMealNoteEn = "With or immediately after meals",
            defaultPatientNoteTl = "Para sa blood sugar control. Inumin kasabay ng pagkain upang hindi sumakit ang tiyan.",
            defaultPatientNoteEn = "For blood sugar management. Take with food to reduce stomach upset."
        ),
        DrugCatalogEntry(
            genericName = "Atorvastatin",
            brandNames = listOf("Lipitor", "Vidastat"),
            form = "tablet",
            standardStrength = "20 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(5.50),
                PharmacyChain.WATSONS to samplePrice(9.50),
                PharmacyChain.MERCURY_DRUG to samplePrice(10.50),
                PharmacyChain.SOUTH_STAR to samplePrice(10.00),
                PharmacyChain.ROSE_PHARMACY to samplePrice(10.00)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(38.00),
                PharmacyChain.WATSONS to samplePrice(39.50)
            ),
            defaultMealNoteTl = "Gabi bago matulog",
            defaultMealNoteEn = "At bedtime",
            defaultPatientNoteTl = "Pababa ng cholesterol. Mas mainam inumin sa gabi.",
            defaultPatientNoteEn = "Lowers cholesterol. Best taken in the evening."
        ),
        DrugCatalogEntry(
            genericName = "Omeprazole",
            brandNames = listOf("Losec", "Omepron"),
            form = "capsule",
            standardStrength = "20 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(3.50),
                PharmacyChain.WATSONS to samplePrice(6.00),
                PharmacyChain.MERCURY_DRUG to samplePrice(6.50),
                PharmacyChain.SOUTH_STAR to samplePrice(6.00),
                PharmacyChain.ROSE_PHARMACY to samplePrice(6.00)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(42.00),
                PharmacyChain.WATSONS to samplePrice(43.50)
            ),
            defaultMealNoteTl = "30 minuto bago kumain sa umaga",
            defaultMealNoteEn = "30 minutes before breakfast",
            defaultPatientNoteTl = "Para sa hyperacidity o GERD. Lunukin nang buo kasabay ng tubig.",
            defaultPatientNoteEn = "For GERD or acidity. Swallow whole with water."
        ),
        DrugCatalogEntry(
            genericName = "Cetirizine",
            brandNames = listOf("Zyrtec", "Alnix"),
            form = "tablet",
            standardStrength = "10 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(2.50),
                PharmacyChain.WATSONS to samplePrice(5.50),
                PharmacyChain.MERCURY_DRUG to samplePrice(6.00),
                PharmacyChain.SOUTH_STAR to samplePrice(5.75),
                PharmacyChain.ROSE_PHARMACY to samplePrice(5.75)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(24.50),
                PharmacyChain.WATSONS to samplePrice(25.00)
            ),
            defaultMealNoteTl = "Bago matulog",
            defaultMealNoteEn = "Take at bedtime",
            defaultPatientNoteTl = "Para sa allergy at pangangati. Maaaring magdulot ng antok.",
            defaultPatientNoteEn = "For allergy and itchiness. May cause mild drowsiness."
        ),
        DrugCatalogEntry(
            genericName = "Salbutamol",
            brandNames = listOf("Ventolin"),
            form = "tablet",
            standardStrength = "2 mg",
            isSample = true,
            source = SAMPLE_SOURCE,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to samplePrice(1.50),
                PharmacyChain.WATSONS to samplePrice(3.00),
                PharmacyChain.MERCURY_DRUG to samplePrice(3.50),
                PharmacyChain.SOUTH_STAR to samplePrice(3.25),
                PharmacyChain.ROSE_PHARMACY to samplePrice(3.25)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to samplePrice(8.50),
                PharmacyChain.WATSONS to samplePrice(9.00)
            ),
            defaultMealNoteTl = "Pagkatapos kumain",
            defaultMealNoteEn = "Take after meals",
            defaultPatientNoteTl = "Pang-hika o paninikip ng dibdib ayon sa tagubilin ng doktor.",
            defaultPatientNoteEn = "For asthma or chest tightness as directed by doctor."
        )
    )

    fun findEntry(name: String, strength: String = ""): DrugCatalogEntry? {
        val cleanName = name.lowercase().replace(Regex("[^a-z0-9]"), "")
        val cleanStrength = strength.lowercase().replace(Regex("[^a-z0-9]"), "")

        return entries.firstOrNull { entry ->
            val genericMatch = entry.genericName.lowercase().replace(Regex("[^a-z0-9]"), "") == cleanName
            val brandMatch = entry.brandNames.any { it.lowercase().replace(Regex("[^a-z0-9]"), "") == cleanName }
            val strengthMatch = cleanStrength.isEmpty() || entry.standardStrength.lowercase().replace(Regex("[^a-z0-9]"), "").contains(cleanStrength)
            (genericMatch || brandMatch) && strengthMatch
        } ?: entries.firstOrNull { entry ->
            entry.genericName.lowercase().replace(Regex("[^a-z0-9]"), "").contains(cleanName) ||
                entry.brandNames.any { it.lowercase().replace(Regex("[^a-z0-9]"), "").contains(cleanName) }
        }
    }
}
