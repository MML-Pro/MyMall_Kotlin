package com.blogspot.mido_mymall.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("user_preferences")
private const val TAG = "DataStoreRepository"

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val datastore: DataStore<Preferences> = context.dataStore

    private object PreferencesKeys {

        val FORCE_DARK_THEME = intPreferencesKey("force_dark_theme")

        val BACK_ONLINE = booleanPreferencesKey("backOnline")
    }

    val readBackOnline: Flow<Boolean> = datastore.data.catch { ex ->
        if (ex is IOException) {
            ex.message?.let { Log.e(TAG, it) }
            emit(emptyPreferences())
        } else {
            throw ex
        }
    }.map { preferences ->
        val backOnline = preferences[PreferencesKeys.BACK_ONLINE] ?: false
        backOnline
    }


    val getSelectedDayNightMode: Flow<Int> = context.dataStore.data
        .catch { ex ->
            if (ex is IOException) {
                ex.message?.let { Log.e(TAG, it) }
                emit(emptyPreferences())
            } else {
                throw ex
            }
        }
        .map { preferences ->
            val whichModeSelected = preferences[PreferencesKeys.FORCE_DARK_THEME] ?: -1
            whichModeSelected
        }

    suspend fun saveSelectedDayNightMode(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FORCE_DARK_THEME] = value
        }
    }

    suspend fun saveBackOnline(backOnline: Boolean) {
        datastore.edit { preferences ->
            preferences[PreferencesKeys.BACK_ONLINE] = backOnline
        }
    }

}