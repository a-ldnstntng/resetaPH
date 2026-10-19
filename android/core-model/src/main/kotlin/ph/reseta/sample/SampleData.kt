package ph.reseta.sample

import ph.reseta.model.PharmacyBranch
import ph.reseta.model.PharmacyChain
import ph.reseta.model.PrescriptionItem
import ph.reseta.model.PrescriptionScan

/**
 * Rich clinical sample data for ResetaPH (Rule 1 & Rule 2).
 */
object SampleData {

    val sampleItem1 = PrescriptionItem(
        id = "item-1",
        writtenAs = "Augmentin 625mg tab #14",
        brandName = "Augmentin",
        genericName = "Co-Amoxiclav",
        strength = "625 mg",
        form = "tablet",
        quantity = 14,
        dosesPerDay = 2,
        durationDays = 7,
        slots = listOf("umaga", "gabi"),
        mealNoteTl = "Pagkatapos kumain",
        mealNoteEn = "Take after meals",
        noteTl = "Tapusin ang buong 7 araw kahit magaling na ang pakiramdam para maiwasan ang antibiotic resistance.",
        noteEn = "Finish the full 7-day course even if feeling better to prevent antibiotic resistance.",
        uncertainFields = emptyList(),
        safetyWarnings = emptyList()
    )

    val sampleItem2 = PrescriptionItem(
        id = "item-2",
        writtenAs = "Norvasc 10mg tab #30",
        brandName = "Norvasc",
        genericName = "Amlodipine",
        strength = "10 mg",
        form = "tablet",
        quantity = 30,
        dosesPerDay = 1,
        durationDays = 30,
        slots = listOf("umaga"),
        mealNoteTl = "Kahit may laman o walang laman ang tiyan",
        mealNoteEn = "With or without food",
        noteTl = "Maintenance para sa presyon ng dugo. Inumin sa parehong oras araw-araw.",
        noteEn = "Blood pressure maintenance. Take at the same time each day.",
        uncertainFields = emptyList(),
        safetyWarnings = emptyList()
    )

    val sampleItem3 = PrescriptionItem(
        id = "item-3",
        writtenAs = "Glucophage 500mg tab #60",
        brandName = "Glucophage",
        genericName = "Metformin",
        strength = "500 mg",
        form = "tablet",
        quantity = 60,
        dosesPerDay = 2,
        durationDays = 30,
        slots = listOf("umaga", "gabi"),
        mealNoteTl = "Kasamang kumain para iwas sakit ng tiyan",
        mealNoteEn = "Take with meals to prevent stomach upset",
        noteTl = "Pang-kontrol sa asukal sa dugo (diabetes). Huwag lumiban sa pag-inom.",
        noteEn = "Controls blood sugar (diabetes). Do not skip scheduled doses.",
        uncertainFields = listOf("quantity"),
        safetyWarnings = emptyList()
    )

    val sampleScanResult = PrescriptionScan(
        id = "scan-mock-001",
        items = listOf(sampleItem1, sampleItem2, sampleItem3),
        doctorName = "Dr. Maria Santos, MD",
        prcNumber = "PRC 0123456",
        dateOnSlip = "10/18/2026",
        readScore = 92,
        medicationLineCount = 3,
        scannedAtTimestamp = System.currentTimeMillis() - 3600000L
    )

    /**
     * Requirement 2: Branches come from an external data source to be provided.
     * Until then, label every branch "Sample" and invent no addresses, phone numbers, hours, or stock claims.
     */
    val sampleBranches = listOf(
        PharmacyBranch(
            id = "sample-tgp-1",
            name = "The Generics Pharmacy [Sample]",
            chain = PharmacyChain.TGP,
            address = "Sample Location",
            latitude = 14.5685,
            longitude = 120.9934,
            distanceKm = 0.35,
            phoneNumber = null,
            openingHours = null,
            stockClaim = null,
            isSample = true
        ),
        PharmacyBranch(
            id = "sample-mercury-1",
            name = "Mercury Drug [Sample]",
            chain = PharmacyChain.MERCURY_DRUG,
            address = "Sample Location",
            latitude = 14.5638,
            longitude = 120.9947,
            distanceKm = 0.52,
            phoneNumber = null,
            openingHours = null,
            stockClaim = null,
            isSample = true
        ),
        PharmacyBranch(
            id = "sample-watsons-1",
            name = "Watsons [Sample]",
            chain = PharmacyChain.WATSONS,
            address = "Sample Location",
            latitude = 14.5612,
            longitude = 120.9890,
            distanceKm = 0.88,
            phoneNumber = null,
            openingHours = null,
            stockClaim = null,
            isSample = true
        ),
        PharmacyBranch(
            id = "sample-southstar-1",
            name = "South Star Drug [Sample]",
            chain = PharmacyChain.SOUTH_STAR,
            address = "Sample Location",
            latitude = 14.5710,
            longitude = 120.9912,
            distanceKm = 1.15,
            phoneNumber = null,
            openingHours = null,
            stockClaim = null,
            isSample = true
        )
    )
}
