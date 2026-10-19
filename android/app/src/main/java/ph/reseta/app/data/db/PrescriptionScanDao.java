package ph.reseta.app.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import kotlinx.coroutines.flow.Flow;

/**
 * Room DAO for Prescription Scans (Rule 5: stores structured results, supports history deletion).
 */
@Dao
public interface PrescriptionScanDao {

    @Query("SELECT * FROM prescription_scans ORDER BY scanned_at_timestamp DESC")
    Flow<List<PrescriptionScanEntity>> getAllScansFlow();

    @Query("SELECT * FROM prescription_scans ORDER BY scanned_at_timestamp DESC")
    List<PrescriptionScanEntity> getAllScansList();

    @Query("SELECT * FROM prescription_scans WHERE id = :id LIMIT 1")
    PrescriptionScanEntity getScanById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScan(PrescriptionScanEntity scan);

    @Query("DELETE FROM prescription_scans WHERE id = :id")
    void deleteScanById(String id);

    @Query("DELETE FROM prescription_scans")
    void deleteAllScans();
}
