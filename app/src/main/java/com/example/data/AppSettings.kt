/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppSettings(private val context: Context) {
    private val DARK_MODE = booleanPreferencesKey("dark_mode")
    private val API_KEY = stringPreferencesKey("api_key")
    private val CAMERA_QUALITY = stringPreferencesKey("camera_quality")
    private val FRAME_RATE = stringPreferencesKey("frame_rate")
    private val IMAGE_QUALITY = stringPreferencesKey("image_quality")

    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: true }
    val apiKeyFlow: Flow<String> = context.dataStore.data.map { it[API_KEY] ?: "" }
    val cameraQualityFlow: Flow<String> = context.dataStore.data.map { it[CAMERA_QUALITY] ?: "High" }
    val frameRateFlow: Flow<String> = context.dataStore.data.map { it[FRAME_RATE] ?: "60 Hz" }
    val imageQualityFlow: Flow<String> = context.dataStore.data.map { it[IMAGE_QUALITY] ?: "100%" }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = isDark }
    }

    suspend fun setApiKey(key: String) {
        context.dataStore.edit { it[API_KEY] = key }
    }

    suspend fun setCameraQuality(quality: String) {
        context.dataStore.edit { it[CAMERA_QUALITY] = quality }
    }

    suspend fun setFrameRate(rate: String) {
        context.dataStore.edit { it[FRAME_RATE] = rate }
    }

    suspend fun setImageQuality(quality: String) {
        context.dataStore.edit { it[IMAGE_QUALITY] = quality }
    }
}
