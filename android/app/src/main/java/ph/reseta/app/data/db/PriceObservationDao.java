package ph.reseta.app.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

/**
 * Room DAO for querying and inserting price_observations.
 */
@Dao
public interface PriceObservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PriceObservationEntity> observations);

    @Query("SELECT * FROM price_observations WHERE LOWER(drug) = LOWER(:drugName)")
    List<PriceObservationEntity> getObservationsForDrug(String drugName);

    @Query("SELECT * FROM price_observations")
    List<PriceObservationEntity> getAllObservations();

    @Query("SELECT COUNT(*) FROM price_observations")
    int getObservationCount();

    @Query("DELETE FROM price_observations")
    void deleteAll();
}
