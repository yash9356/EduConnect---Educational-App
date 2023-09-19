package com.example.educonnect

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

class EduConnectApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

    companion object {
        lateinit var appContext: Context
        private const val SHARED_PREFERENCES_NAME = "app_shared_preferences"
        val sharedPreferences: SharedPreferences by lazy {
            appContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        }

        val dataStore: DataStore<Preferences> by lazy {
            PreferenceDataStoreFactory.create {
                appContext.preferencesDataStoreFile("app_pref")
            }
        }
    }
}