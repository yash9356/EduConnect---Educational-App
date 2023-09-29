package com.example.educonnect.repo

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharedPrefRepo(
    private val preferences: SharedPreferences,
    private val dataStore: DataStore<Preferences>
) {
    private val userNameKey = stringPreferencesKey("user_name")
    val userName: Flow<String> = dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[userNameKey] ?: ""
        }

    private val isNewUserKey = booleanPreferencesKey("is_new_user")
    val isNewUser: Flow<Boolean> = dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[isNewUserKey] == true
        }

    suspend fun updateNewUser(isNewUser: Boolean) {
        dataStore.edit { settings ->
            settings[isNewUserKey] = isNewUser
        }
    }


    suspend fun clearAll() {
        dataStore.edit {
            it.clear()
        }
        preferences.edit {
            clear()
        }
    }
}