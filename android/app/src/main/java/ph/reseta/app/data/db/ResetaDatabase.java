package ph.reseta.app.data.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * ResetaPH Local Room Database (Rule 5).
 * Disables automated backup and keeps only patient-controlled structured prescription data.
 */
@Database(
    entities = {PrescriptionScanEntity.class},
    version = 1,
    exportSchema = false
)
public abstract class ResetaDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "reseta_ph.db";
    private static volatile ResetaDatabase INSTANCE;

    public abstract PrescriptionScanDao prescriptionScanDao();

    public static ResetaDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ResetaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ResetaDatabase.class,
                            DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
