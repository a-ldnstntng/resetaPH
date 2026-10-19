package ph.reseta.app.data.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room Entity for price_observations (Stage 4 / Rule 2).
 * Fields: (drug, brand, chain, unit price, observed_on, source, evidence note)
 */
@Entity(tableName = "price_observations")
public class PriceObservationEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "drug")
    private String drug;

    @ColumnInfo(name = "brand")
    private String brand;

    @ColumnInfo(name = "chain")
    private String chain;

    @ColumnInfo(name = "unit_price")
    private double unitPrice;

    @ColumnInfo(name = "observed_on")
    private String observedOn;

    @ColumnInfo(name = "source")
    private String source;

    @ColumnInfo(name = "evidence_note")
    private String evidenceNote;

    @ColumnInfo(name = "is_sample")
    private boolean isSample;

    public PriceObservationEntity(
            long id,
            String drug,
            String brand,
            String chain,
            double unitPrice,
            String observedOn,
            String source,
            String evidenceNote,
            boolean isSample
    ) {
        this.id = id;
        this.drug = drug;
        this.brand = brand;
        this.chain = chain;
        this.unitPrice = unitPrice;
        this.observedOn = observedOn;
        this.source = source;
        this.evidenceNote = evidenceNote;
        this.isSample = isSample;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDrug() { return drug; }
    public void setDrug(String drug) { this.drug = drug; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getChain() { return chain; }
    public void setChain(String chain) { this.chain = chain; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public String getObservedOn() { return observedOn; }
    public void setObservedOn(String observedOn) { this.observedOn = observedOn; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getEvidenceNote() { return evidenceNote; }
    public void setEvidenceNote(String evidenceNote) { this.evidenceNote = evidenceNote; }

    public boolean isSample() { return isSample; }
    public void setSample(boolean sample) { isSample = sample; }
}
