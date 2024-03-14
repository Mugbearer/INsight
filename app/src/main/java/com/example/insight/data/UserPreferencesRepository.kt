package com.example.insight.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val PREFERRED_APP = stringPreferencesKey("preferred_app")
        val GESTURE_ZERO = intPreferencesKey("gesture_zero")
        val GESTURE_ONE = intPreferencesKey("gesture_one")
        val GESTURE_TWO = intPreferencesKey("gesture_two")
        val GESTURE_THREE = intPreferencesKey("gesture_three")
        val GESTURE_FOUR = intPreferencesKey("gesture_four")
        val GESTURE_FIVE = intPreferencesKey("gesture_five")
        val GESTURE_SIX = intPreferencesKey("gesture_six")
        val GESTURE_SEVEN = intPreferencesKey("gesture_seven")
        val GESTURE_EIGHT = intPreferencesKey("gesture_eight")
        val GESTURE_NINE = intPreferencesKey("gesture_nine")
        const val TAG = "UserPreferencesRepo"
    }

    val preferredApp: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[PREFERRED_APP] ?: ""
        }

    suspend fun setPreferredApp(preferredApp: String) {
        dataStore.edit { preferences ->
            preferences[PREFERRED_APP] = preferredApp
        }
    }

    val gestureMap: Flow<MutableMap<Int, Int>> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            mutableMapOf(
                0 to (preferences[GESTURE_ZERO] ?: 0),
                1 to (preferences[GESTURE_ONE] ?: 1),
                2 to (preferences[GESTURE_TWO] ?: 2),
                3 to (preferences[GESTURE_THREE] ?: 3),
                4 to (preferences[GESTURE_FOUR] ?: 4),
                5 to (preferences[GESTURE_FIVE] ?: 5),
                6 to (preferences[GESTURE_SIX] ?: 6),
                7 to (preferences[GESTURE_SEVEN] ?: 7),
                8 to (preferences[GESTURE_EIGHT] ?: 8),
                9 to (preferences[GESTURE_NINE] ?: 9)
            )
        }

    suspend fun swapGestures(firstGesture: Int, secondGesture: Int) {
        val firstKey = GESTURE_KEY_MAP[firstGesture] ?: error("Gesture not found")
        val secondKey = GESTURE_KEY_MAP[secondGesture] ?: error("Gesture not found")

        dataStore.edit { preferences ->
            val temp = preferences[firstKey] ?: error("Gesture value not found")
            preferences[firstKey] = preferences[secondKey] ?: error("Gesture value not found")
            preferences[secondKey] = temp
        }
    }


    private val GESTURE_KEY_MAP = mapOf(
        0 to GESTURE_ZERO,
        1 to GESTURE_ONE,
        2 to GESTURE_TWO,
        3 to GESTURE_THREE,
        4 to GESTURE_FOUR,
        5 to GESTURE_FIVE,
        6 to GESTURE_SIX,
        7 to GESTURE_SEVEN,
        8 to GESTURE_EIGHT,
        9 to GESTURE_NINE
    )

//    suspend fun assignGesture(indexOfOldGesture: Int, indexOfNewGesture: Int){
//        dataStore.edit {  preferences ->
//            gestureMap.map {
//                it.map { (key, value) ->
//                    if (value == indexOfOldGesture) {
//
//                    }
//                }
//            }
//        }
//    }

    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}