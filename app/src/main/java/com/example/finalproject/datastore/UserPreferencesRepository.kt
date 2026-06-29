package com.example.finalproject.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val DEFAULT_STATUS_KEY = stringPreferencesKey("default_reading_status")
        val LAST_SEARCH_KEY = stringPreferencesKey("last_search")
    }

    // 深色模式偏好
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }

    // 默认阅读状态
    val defaultReadingStatus: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_STATUS_KEY] ?: "want_to_read"
    }

    suspend fun setDefaultReadingStatus(status: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_STATUS_KEY] = status
        }
    }

    // 最近搜索
    val lastSearch: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LAST_SEARCH_KEY] ?: ""
    }

    suspend fun setLastSearch(query: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SEARCH_KEY] = query
        }
    }
}
