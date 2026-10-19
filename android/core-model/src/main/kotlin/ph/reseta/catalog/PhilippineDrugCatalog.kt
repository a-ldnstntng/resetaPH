package ph.reseta.catalog

import ph.reseta.model.DatedPrice
import ph.reseta.model.DrugCatalogEntry
import ph.reseta.model.PharmacyChain

/**
 * Pure Kotlin Drug Catalog for the Philippines (Rule 1 & Rule 2).
 * All prices are explicitly dated records from DOH DPRI and verified retail surveys.
 */
object PhilippineDrugCatalog {
    const val CURRENT_SURVEY_DATE = "2026-10-15"
    private const val DEFAULT_SOURCE = "DOH DPRI & Retail Market Survey"

    private fun dated(amount: Double, source: String = DEFAULT_SOURCE): DatedPrice =
        DatedPrice(amountPhp = amount, recordedDate = CURRENT_SURVEY_DATE, source = source)

    val entries: List<DrugCatalogEntry> = listOf(
        DrugCatalogEntry(
            genericName = "Co-Amoxiclav",
            brandNames = listOf("Augmentin", "Natravox", "Curam"),
            form = "tablet",
            standardStrength = "625 mg",
            maxDailyDoseMg = 2000.0,
            maxDaysDuration = 14,
            isAntibiotic = true,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(18.00),
                PharmacyChain.WATSONS to dated(24.00),
                PharmacyChain.MERCURY_DRUG to dated(26.00),
                PharmacyChain.SOUTH_STAR to dated(25.00),
                PharmacyChain.ROSE_PHARMACY to dated(25.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(82.00, "Mercury Drug Official Price"),
                PharmacyChain.WATSONS to dated(84.50, "Watsons Official Price")
            ),
            defaultMealNoteTl = "Pagkatapos kumain",
            defaultMealNoteEn = "Take after meals",
            defaultPatientNoteTl = "Tapusin ang buong 7 araw kahit magaling na ang pakiramdam para maiwasan ang antibiotic resistance.",
            defaultPatientNoteEn = "Finish the full 7-day course even if feeling better to prevent antibiotic resistance."
        ),
        DrugCatalogEntry(
            genericName = "Amoxicillin",
            brandNames = listOf("Amoxil", "Himox"),
            form = "capsule",
            standardStrength = "500 mg",
            maxDailyDoseMg = 3000.0,
            maxDaysDuration = 14,
            isAntibiotic = true,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(3.50),
                PharmacyChain.WATSONS to dated(5.00),
                PharmacyChain.MERCURY_DRUG to dated(5.50),
                PharmacyChain.SOUTH_STAR to dated(5.25),
                PharmacyChain.ROSE_PHARMACY to dated(5.25)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(14.50),
                PharmacyChain.WATSONS to dated(15.00)
            ),
            defaultMealNoteTl = "Pagkatapos kumain",
            defaultMealNoteEn = "Take after meals",
            defaultPatientNoteTl = "Antibiotic ito: inumin sa tamang oras at tapusin ang buong reseta.",
            defaultPatientNoteEn = "This is an antibiotic: take on schedule and finish the entire prescription."
        ),
        DrugCatalogEntry(
            genericName = "Cefuroxime",
            brandNames = listOf("Zinnat", "Ceftum"),
            form = "tablet",
            standardStrength = "500 mg",
            maxDailyDoseMg = 1000.0,
            maxDaysDuration = 14,
            isAntibiotic = true,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(22.00),
                PharmacyChain.WATSONS to dated(28.00),
                PharmacyChain.MERCURY_DRUG to dated(30.00),
                PharmacyChain.SOUTH_STAR to dated(29.00),
                PharmacyChain.ROSE_PHARMACY to dated(29.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(75.00),
                PharmacyChain.WATSONS to dated(77.00)
            ),
            defaultMealNoteTl = "Kasamang kumain para mas ma-absorb",
            defaultMealNoteEn = "Take with food for better absorption",
            defaultPatientNoteTl = "Tapusin ang iniresetang araw ng doktor para lubusang mapuksa ang impeksyon.",
            defaultPatientNoteEn = "Complete the full days prescribed by the doctor to completely clear the infection."
        ),
        DrugCatalogEntry(
            genericName = "Cefalexin",
            brandNames = listOf("Keflex", "Ceporex"),
            form = "capsule",
            standardStrength = "500 mg",
            maxDailyDoseMg = 4000.0,
            maxDaysDuration = 14,
            isAntibiotic = true,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(7.00),
                PharmacyChain.WATSONS to dated(9.50),
                PharmacyChain.MERCURY_DRUG to dated(10.00),
                PharmacyChain.SOUTH_STAR to dated(9.75),
                PharmacyChain.ROSE_PHARMACY to dated(9.75)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(28.00),
                PharmacyChain.WATSONS to dated(29.00)
            ),
            defaultMealNoteTl = "Bago o pagkatapos kumain",
            defaultMealNoteEn = "Before or after meals",
            defaultPatientNoteTl = "Tapusin ang buong reseta ng antibiotic.",
            defaultPatientNoteEn = "Complete the full antibiotic prescription."
        ),
        DrugCatalogEntry(
            genericName = "Azithromycin",
            brandNames = listOf("Zithromax"),
            form = "tablet",
            standardStrength = "500 mg",
            maxDailyDoseMg = 500.0,
            maxDaysDuration = 5,
            isAntibiotic = true,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(32.00),
                PharmacyChain.WATSONS to dated(45.00),
                PharmacyChain.MERCURY_DRUG to dated(48.00),
                PharmacyChain.SOUTH_STAR to dated(46.00),
                PharmacyChain.ROSE_PHARMACY to dated(47.00)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(165.00),
                PharmacyChain.WATSONS to dated(170.00)
            ),
            defaultMealNoteTl = "1 oras bago kumain o 2 oras pagkatapos kumain",
            defaultMealNoteEn = "1 hour before meals or 2 hours after meals",
            defaultPatientNoteTl = "Karaniwang 3 hanggang 5 araw lamang iniinom. Huwag laktawan.",
            defaultPatientNoteEn = "Usually taken for only 3 to 5 days. Do not skip doses."
        ),
        DrugCatalogEntry(
            genericName = "Amlodipine",
            brandNames = listOf("Norvasc"),
            form = "tablet",
            standardStrength = "5 mg",
            maxDailyDoseMg = 10.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(2.00),
                PharmacyChain.WATSONS to dated(4.50),
                PharmacyChain.MERCURY_DRUG to dated(5.00),
                PharmacyChain.SOUTH_STAR to dated(4.75),
                PharmacyChain.ROSE_PHARMACY to dated(4.75)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(24.00),
                PharmacyChain.WATSONS to dated(25.00)
            ),
            defaultMealNoteTl = "Kahit may laman o walang laman ang tiyan",
            defaultMealNoteEn = "With or without food",
            defaultPatientNoteTl = "Maintenance para sa presyon ng dugo. Huwag biglang ititigil nang walang abiso ng doktor.",
            defaultPatientNoteEn = "Blood pressure maintenance. Do not stop abruptly without doctor's advice."
        ),
        DrugCatalogEntry(
            genericName = "Amlodipine",
            brandNames = listOf("Norvasc"),
            form = "tablet",
            standardStrength = "10 mg",
            maxDailyDoseMg = 10.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(3.50),
                PharmacyChain.WATSONS to dated(7.00),
                PharmacyChain.MERCURY_DRUG to dated(8.00),
                PharmacyChain.SOUTH_STAR to dated(7.50),
                PharmacyChain.ROSE_PHARMACY to dated(7.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(44.00),
                PharmacyChain.WATSONS to dated(45.50)
            ),
            defaultMealNoteTl = "Kahit may laman o walang laman ang tiyan",
            defaultMealNoteEn = "With or without food",
            defaultPatientNoteTl = "Pang-high blood: inumin sa parehong oras araw-araw.",
            defaultPatientNoteEn = "For hypertension: take at the same time each day."
        ),
        DrugCatalogEntry(
            genericName = "Losartan",
            brandNames = listOf("Cozaar", "Lifezar"),
            form = "tablet",
            standardStrength = "50 mg",
            maxDailyDoseMg = 100.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(3.00),
                PharmacyChain.WATSONS to dated(6.00),
                PharmacyChain.MERCURY_DRUG to dated(6.50),
                PharmacyChain.SOUTH_STAR to dated(6.25),
                PharmacyChain.ROSE_PHARMACY to dated(6.25)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(26.00),
                PharmacyChain.WATSONS to dated(27.00)
            ),
            defaultMealNoteTl = "Kahit may laman o walang laman ang tiyan",
            defaultMealNoteEn = "With or without food",
            defaultPatientNoteTl = "Para sa altapresyon at proteksyon sa bato. Inumin araw-araw ayon sa bilin.",
            defaultPatientNoteEn = "For hypertension and kidney protection. Take daily as instructed."
        ),
        DrugCatalogEntry(
            genericName = "Losartan",
            brandNames = listOf("Cozaar"),
            form = "tablet",
            standardStrength = "100 mg",
            maxDailyDoseMg = 100.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(5.50),
                PharmacyChain.WATSONS to dated(10.00),
                PharmacyChain.MERCURY_DRUG to dated(11.00),
                PharmacyChain.SOUTH_STAR to dated(10.50),
                PharmacyChain.ROSE_PHARMACY to dated(10.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(45.00),
                PharmacyChain.WATSONS to dated(46.50)
            ),
            defaultMealNoteTl = "Kahit may laman o walang laman ang tiyan",
            defaultMealNoteEn = "With or without food",
            defaultPatientNoteTl = "Mataas na dosage para sa altapresyon. Bantayan ang blood pressure regular.",
            defaultPatientNoteEn = "High dosage for hypertension. Monitor blood pressure regularly."
        ),
        DrugCatalogEntry(
            genericName = "Metformin",
            brandNames = listOf("Glucophage"),
            form = "tablet",
            standardStrength = "500 mg",
            maxDailyDoseMg = 2550.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(2.00),
                PharmacyChain.WATSONS to dated(3.75),
                PharmacyChain.MERCURY_DRUG to dated(4.00),
                PharmacyChain.SOUTH_STAR to dated(3.85),
                PharmacyChain.ROSE_PHARMACY to dated(3.85)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(16.00),
                PharmacyChain.WATSONS to dated(17.00)
            ),
            defaultMealNoteTl = "Kasamang kumain para iwas sakit ng tiyan",
            defaultMealNoteEn = "Take with meals to prevent stomach irritation",
            defaultPatientNoteTl = "Para sa diabetes (blood sugar). Inumin kasabay ng pagkain.",
            defaultPatientNoteEn = "For diabetes (blood sugar). Take together with meals."
        ),
        DrugCatalogEntry(
            genericName = "Metformin",
            brandNames = listOf("Glucophage"),
            form = "tablet",
            standardStrength = "850 mg",
            maxDailyDoseMg = 2550.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(3.50),
                PharmacyChain.WATSONS to dated(5.50),
                PharmacyChain.MERCURY_DRUG to dated(6.00),
                PharmacyChain.SOUTH_STAR to dated(5.75),
                PharmacyChain.ROSE_PHARMACY to dated(5.75)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(22.00),
                PharmacyChain.WATSONS to dated(23.00)
            ),
            defaultMealNoteTl = "Kasamang kumain o pagkatapos kumain",
            defaultMealNoteEn = "Take with or right after meals",
            defaultPatientNoteTl = "Para sa diabetes. Panatilihing regular ang pagsusuri ng asukal sa dugo.",
            defaultPatientNoteEn = "For diabetes. Keep monitoring blood sugar regularly."
        ),
        DrugCatalogEntry(
            genericName = "Glimepiride",
            brandNames = listOf("Amaryl"),
            form = "tablet",
            standardStrength = "2 mg",
            maxDailyDoseMg = 8.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(4.00),
                PharmacyChain.WATSONS to dated(7.50),
                PharmacyChain.MERCURY_DRUG to dated(8.00),
                PharmacyChain.SOUTH_STAR to dated(7.75),
                PharmacyChain.ROSE_PHARMACY to dated(7.75)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(25.00),
                PharmacyChain.WATSONS to dated(26.00)
            ),
            defaultMealNoteTl = "Bago mag-almusal o kasabay ng unang pagkain",
            defaultMealNoteEn = "Before breakfast or with your first meal",
            defaultPatientNoteTl = "Pampababa ng blood sugar. Magbaon ng matamis sakaling mahilo o mag-hypoglycemia.",
            defaultPatientNoteEn = "Lowers blood sugar. Keep candy or sweets handy in case of dizziness or hypoglycemia."
        ),
        DrugCatalogEntry(
            genericName = "Atorvastatin",
            brandNames = listOf("Lipitor"),
            form = "tablet",
            standardStrength = "20 mg",
            maxDailyDoseMg = 80.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(7.50),
                PharmacyChain.WATSONS to dated(12.00),
                PharmacyChain.MERCURY_DRUG to dated(13.00),
                PharmacyChain.SOUTH_STAR to dated(12.50),
                PharmacyChain.ROSE_PHARMACY to dated(12.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(42.00),
                PharmacyChain.WATSONS to dated(44.00)
            ),
            defaultMealNoteTl = "Mas mabuting inumin sa gabi bago matulog",
            defaultMealNoteEn = "Best taken at night before bedtime",
            defaultPatientNoteTl = "Pampababa ng kolesterol sa dugo. Karaniwang iniinom sa gabi.",
            defaultPatientNoteEn = "Lowers blood cholesterol. Usually taken at bedtime."
        ),
        DrugCatalogEntry(
            genericName = "Atorvastatin",
            brandNames = listOf("Lipitor"),
            form = "tablet",
            standardStrength = "40 mg",
            maxDailyDoseMg = 80.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(12.00),
                PharmacyChain.WATSONS to dated(18.00),
                PharmacyChain.MERCURY_DRUG to dated(19.50),
                PharmacyChain.SOUTH_STAR to dated(19.00),
                PharmacyChain.ROSE_PHARMACY to dated(19.00)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(58.00),
                PharmacyChain.WATSONS to dated(60.00)
            ),
            defaultMealNoteTl = "Inumin sa gabi bago matulog",
            defaultMealNoteEn = "Take at night before bedtime",
            defaultPatientNoteTl = "Pampababa ng kolesterol. Ipaalam sa doktor kung magkaroon ng pananakit ng kalamnan.",
            defaultPatientNoteEn = "Lowers cholesterol. Notify your doctor if you experience muscle aches."
        ),
        DrugCatalogEntry(
            genericName = "Paracetamol",
            brandNames = listOf("Biogesic", "Tempra", "Calpol"),
            form = "tablet",
            standardStrength = "500 mg",
            maxDailyDoseMg = 4000.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(1.50),
                PharmacyChain.WATSONS to dated(2.50),
                PharmacyChain.MERCURY_DRUG to dated(2.75),
                PharmacyChain.SOUTH_STAR to dated(2.60),
                PharmacyChain.ROSE_PHARMACY to dated(2.60)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(5.00),
                PharmacyChain.WATSONS to dated(5.25)
            ),
            defaultMealNoteTl = "Kahit may laman o walang laman ang tiyan",
            defaultMealNoteEn = "With or without food",
            defaultPatientNoteTl = "Para sa lagnat at kirot. Huwag lumampas sa 8 tableta (4,000 mg) sa loob ng 24 oras para sa atay.",
            defaultPatientNoteEn = "For fever and pain. Do not exceed 8 tablets (4,000 mg) within 24 hours to protect your liver."
        ),
        DrugCatalogEntry(
            genericName = "Mefenamic Acid",
            brandNames = listOf("Ponstan", "Dolfenal"),
            form = "capsule",
            standardStrength = "500 mg",
            maxDailyDoseMg = 1500.0,
            maxDaysDuration = 7,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(3.50),
                PharmacyChain.WATSONS to dated(6.00),
                PharmacyChain.MERCURY_DRUG to dated(6.50),
                PharmacyChain.SOUTH_STAR to dated(6.25),
                PharmacyChain.ROSE_PHARMACY to dated(6.25)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(28.00),
                PharmacyChain.WATSONS to dated(29.50)
            ),
            defaultMealNoteTl = "Laging pagkatapos kumain para hindi humapdi ang tiyan",
            defaultMealNoteEn = "Always take after meals to prevent stomach irritation",
            defaultPatientNoteTl = "Para sa matinding kirot. Huwag inumin nang walang laman ang tiyan o lagpas 7 araw.",
            defaultPatientNoteEn = "For acute pain. Do not take on an empty stomach or for longer than 7 days."
        ),
        DrugCatalogEntry(
            genericName = "Ibuprofen",
            brandNames = listOf("Advil", "Medicol"),
            form = "softgel",
            standardStrength = "200 mg",
            maxDailyDoseMg = 1200.0,
            maxDaysDuration = 10,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(2.50),
                PharmacyChain.WATSONS to dated(4.50),
                PharmacyChain.MERCURY_DRUG to dated(5.00),
                PharmacyChain.SOUTH_STAR to dated(4.75),
                PharmacyChain.ROSE_PHARMACY to dated(4.75)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(12.00),
                PharmacyChain.WATSONS to dated(12.50)
            ),
            defaultMealNoteTl = "Pagkatapos kumain",
            defaultMealNoteEn = "Take after meals",
            defaultPatientNoteTl = "Para sa kirot at pamamaga. Inumin pagkatapos kumain.",
            defaultPatientNoteEn = "For pain and inflammation. Always take after meals."
        ),
        DrugCatalogEntry(
            genericName = "Omeprazole",
            brandNames = listOf("Losec", "Prilosec"),
            form = "capsule",
            standardStrength = "20 mg",
            maxDailyDoseMg = 40.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(6.00),
                PharmacyChain.WATSONS to dated(11.00),
                PharmacyChain.MERCURY_DRUG to dated(12.00),
                PharmacyChain.SOUTH_STAR to dated(11.50),
                PharmacyChain.ROSE_PHARMACY to dated(11.50)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(48.00),
                PharmacyChain.WATSONS to dated(50.00)
            ),
            defaultMealNoteTl = "30 minuto bago mag-almusal",
            defaultMealNoteEn = "30 minutes before breakfast",
            defaultPatientNoteTl = "Para sa hyperacidity at ulcer. Inumin nang buo sa umaga 30 minuto bago kumain.",
            defaultPatientNoteEn = "For hyperacidity and ulcers. Swallow whole in the morning 30 minutes before breakfast."
        ),
        DrugCatalogEntry(
            genericName = "Cetirizine",
            brandNames = listOf("Alnix", "Virlix", "Zyrtec"),
            form = "tablet",
            standardStrength = "10 mg",
            maxDailyDoseMg = 10.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(3.00),
                PharmacyChain.WATSONS to dated(6.50),
                PharmacyChain.MERCURY_DRUG to dated(7.00),
                PharmacyChain.SOUTH_STAR to dated(6.75),
                PharmacyChain.ROSE_PHARMACY to dated(6.75)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(28.00),
                PharmacyChain.WATSONS to dated(29.00)
            ),
            defaultMealNoteTl = "Bago matulog",
            defaultMealNoteEn = "Take before bedtime",
            defaultPatientNoteTl = "Para sa allergy at pangangati. Maaaring magdulot ng antok kaya mas maganda sa gabi.",
            defaultPatientNoteEn = "For allergy and itching. May cause drowsiness; best taken at bedtime."
        ),
        DrugCatalogEntry(
            genericName = "Salbutamol",
            brandNames = listOf("Ventolin"),
            form = "tablet",
            standardStrength = "2 mg",
            maxDailyDoseMg = 16.0,
            isAntibiotic = false,
            genericDatedPrices = mapOf(
                PharmacyChain.TGP to dated(1.50),
                PharmacyChain.WATSONS to dated(3.00),
                PharmacyChain.MERCURY_DRUG to dated(3.50),
                PharmacyChain.SOUTH_STAR to dated(3.25),
                PharmacyChain.ROSE_PHARMACY to dated(3.25)
            ),
            brandedDatedPrices = mapOf(
                PharmacyChain.MERCURY_DRUG to dated(8.50),
                PharmacyChain.WATSONS to dated(9.00)
            ),
            defaultMealNoteTl = "Pagkatapos kumain",
            defaultMealNoteEn = "Take after meals",
            defaultPatientNoteTl = "Pang-hika o bronchospasm. Maaaring makaramdam ng kaunting panginginig o mabilis na tibok ng puso.",
            defaultPatientNoteEn = "For asthma or bronchospasm. May cause slight tremors or rapid heart rate."
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
