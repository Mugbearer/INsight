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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
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
        gestureMap
            .take(1)
            .map { gestureMap ->
                val temp = gestureMap[firstGesture] ?: error("Gesture value not found for gesture: $firstGesture")
                val secondValue = gestureMap[secondGesture] ?: error("Gesture value not found for gesture: $secondGesture")

                gestureMap[firstGesture] = secondValue
                gestureMap[secondGesture] = temp

                gestureMap
            }
            .collect { updatedGestureMap ->
                dataStore.edit { preferences ->
                    preferences[GESTURE_ZERO] = updatedGestureMap[0] ?: 0
                    preferences[GESTURE_ONE] = updatedGestureMap[1] ?: 1
                    preferences[GESTURE_TWO] = updatedGestureMap[2] ?: 2
                    preferences[GESTURE_THREE] = updatedGestureMap[3] ?: 3
                    preferences[GESTURE_FOUR] = updatedGestureMap[4] ?: 4
                    preferences[GESTURE_FIVE] = updatedGestureMap[5] ?: 5
                    preferences[GESTURE_SIX] = updatedGestureMap[6] ?: 6
                    preferences[GESTURE_SEVEN] = updatedGestureMap[7] ?: 7
                    preferences[GESTURE_EIGHT] = updatedGestureMap[8] ?: 8
                    preferences[GESTURE_NINE] = updatedGestureMap[9] ?: 9
                }
            }
    }


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