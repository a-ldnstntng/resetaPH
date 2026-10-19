package ph.reseta.app.data.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room Entity for Prescription Scans (Rule 5).
 * Stores ONLY structured transcription results (medications, dosage, date, score).
 * NEVER stores or logs prescription images or raw images to protect patient privacy.
 */
@Entity(tableName = "prescription_scans")
public class PrescriptionScanEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @Nullable
    @ColumnInfo(name = "doctor_name")
    private String doctorName;

    @Nullable
    @ColumnInfo(name = "prc_number")
    private String prcNumber;

    @Nullable
    @ColumnInfo(name = "date_on_slip")
    private String dateOnSlip;

    @ColumnInfo(name = "read_score")
    private int readScore;

    @ColumnInfo(name = "medication_line_count")
    private int medicationLineCount;

    @NonNull
    @ColumnInfo(name = "items_json")
    private String itemsJson;

    @ColumnInfo(name = "scanned_at_timestamp")
    private long scannedAtTimestamp;

    public PrescriptionScanEntity(
            @NonNull String id,
            @Nullable String doctorName,
            @Nullable String prcNumber,
            @Nullable String dateOnSlip,
            int readScore,
            int medicationLineCount,
            @NonNull String itemsJson,
            long scannedAtTimestamp
    ) {
        this.id = id;
        this.doctorName = doctorName;
        this.prcNumber = prcNumber;
        this.dateOnSlip = dateOnSlip;
        this.readScore = readScore;
        this.medicationLineCount = medicationLineCount;
        this.itemsJson = itemsJson;
        this.scannedAtTimestamp = scannedAtTimestamp;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @Nullable
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(@Nullable String doctorName) { this.doctorName = doctorName; }

    @Nullable
    public String getPrcNumber() { return prcNumber; }
    public void setPrcNumber(@Nullable String prcNumber) { this.prcNumber = prcNumber; }

    @Nullable
    public String getDateOnSlip() { return dateOnSlip; }
    public void setDateOnSlip(@Nullable String dateOnSlip) { this.dateOnSlip = dateOnSlip; }

    public int getReadScore() { return readScore; }
    public void setReadScore(int readScore) { this.readScore = readScore; }

    public int getMedicationLineCount() { return medicationLineCount; }
    public void setMedicationLineCount(int medicationLineCount) { this.medicationLineCount = medicationLineCount; }

    @NonNull
    public String getItemsJson() { return itemsJson; }
    public void setItemsJson(@NonNull String itemsJson) { this.itemsJson = itemsJson; }

    public long getScannedAtTimestamp() { return scannedAtTimestamp; }
    public void setScannedAtTimestamp(long scannedAtTimestamp) { this.scannedAtTimestamp = scannedAtTimestamp; }
}
