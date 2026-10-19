package ph.reseta.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "reseta_user_preferences")

enum class AppLanguage {
    TAGLISH,
    ENGLISH
}

/**
 * Repository interface for managing user preferences (language, onboarding, installId for Rule 6 rate-limiting).
 */
interface UserPreferencesRepository {
    val appLanguage: Flow<AppLanguage>
    val isOnboardingComplete: Flow<Boolean>
    val installId: Flow<String>

    suspend fun setLanguage(language: AppLanguage)
    suspend fun setOnboardingComplete(complete: Boolean)
    suspend fun getOrCreateInstallId(): String
}

class DataStoreUserPreferencesRepository(
    private val context: Context
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val KEY_INSTALL_ID = stringPreferencesKey("app_install_id")
    }

    override val appLanguage: Flow<AppLanguage> = context.dataStore.data.map { preferences ->
        when (preferences[PreferencesKeys.KEY_LANGUAGE]) {
            "ENGLISH" -> AppLanguage.ENGLISH
            else -> AppLanguage.TAGLISH // Taglish is default
        }
    }

    override val isOnboardingComplete: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_ONBOARDING_COMPLETE] ?: false
    }

    override val installId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_INSTALL_ID] ?: ""
    }

    override suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_LANGUAGE] = language.name
        }
    }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_ONBOARDING_COMPLETE] = complete
        }
    }

    override suspend fun getOrCreateInstallId(): String {
        val current = context.dataStore.data.map { it[PreferencesKeys.KEY_INSTALL_ID] }.first()
        if (!current.isNullOrBlank()) {
            return current
        }
        val newId = UUID.randomUUID().toString()
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_INSTALL_ID] = newId
        }
        return newId
    }
}
