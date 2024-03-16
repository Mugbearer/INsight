package com.example.insight.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val PREFERRED_APP_NAME_ONE = stringPreferencesKey("preferred_app_name_one")
        val PREFERRED_APP_PACKAGE_NAME_ONE = stringPreferencesKey("preferred_package_app_name_one")
        val PREFERRED_APP_NAME_TWO = stringPreferencesKey("preferred_app_name_two")
        val PREFERRED_APP_PACKAGE_NAME_TWO = stringPreferencesKey("preferred_package_app_name_two")
        val PREFERRED_APP_NAME_THREE = stringPreferencesKey("preferred_app_name_three")
        val PREFERRED_APP_PACKAGE_NAME_THREE = stringPreferencesKey("preferred_package_app_name_three")
        const val TAG = "UserPreferencesRepo"
    }

    val preferredApps: Flow<List<App>> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            listOf(
                App(
                    appName = preferences[PREFERRED_APP_NAME_ONE] ?: "",
                    packageName = preferences[PREFERRED_APP_PACKAGE_NAME_ONE] ?: ""
                ),
                App(
                    appName = preferences[PREFERRED_APP_NAME_TWO] ?: "",
                    packageName = preferences[PREFERRED_APP_PACKAGE_NAME_TWO] ?: ""
                ),
                App(
                    appName = preferences[PREFERRED_APP_NAME_THREE] ?: "",
                    packageName = preferences[PREFERRED_APP_PACKAGE_NAME_THREE] ?: ""
                )
            )
        }

    suspend fun setPreferredApp(indexOfGesture: Int, preferredApp: App) {
        val preferredAppKeys: List<Preferences.Key<String>> = when (indexOfGesture) {
            4 -> listOf(
                PREFERRED_APP_NAME_ONE,
                PREFERRED_APP_PACKAGE_NAME_ONE
            )
            7 -> listOf(
                PREFERRED_APP_NAME_TWO,
                PREFERRED_APP_PACKAGE_NAME_TWO
            )
            8 -> listOf(
                PREFERRED_APP_NAME_THREE,
                PREFERRED_APP_PACKAGE_NAME_THREE
            )
            else -> throw IllegalArgumentException("Invalid gesture: $indexOfGesture")
        }

        dataStore.edit { preferences ->
            preferences[preferredAppKeys[0]] = preferredApp.appName
            preferences[preferredAppKeys[1]] = preferredApp.packageName
        }
    }

    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}