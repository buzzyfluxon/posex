/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppSettings(private val context: Context) {
    private val DARK_MODE = booleanPreferencesKey("dark_mode")
    private val CAMERA_QUALITY = stringPreferencesKey("camera_quality")
    private val FRAME_RATE = stringPreferencesKey("frame_rate")
    private val IMAGE_QUALITY = stringPreferencesKey("image_quality")
    private val OVERLAY_OPACITY = floatPreferencesKey("overlay_opacity")
    private val TIMER_SECONDS = intPreferencesKey("timer_seconds")
    private val SHOW_GRID = booleanPreferencesKey("show_grid")

    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: true }
    val cameraQualityFlow: Flow<String> = context.dataStore.data.map { it[CAMERA_QUALITY] ?: "High" }
    val frameRateFlow: Flow<String> = context.dataStore.data.map { it[FRAME_RATE] ?: "60 Hz" }
    val imageQualityFlow: Flow<String> = context.dataStore.data.map { it[IMAGE_QUALITY] ?: "100%" }
    val overlayOpacityFlow: Flow<Float> = context.dataStore.data.map { it[OVERLAY_OPACITY] ?: 0.6f }
    val timerSecondsFlow: Flow<Int> = context.dataStore.data.map { it[TIMER_SECONDS] ?: 0 }
    val showGridFlow: Flow<Boolean> = context.dataStore.data.map { it[SHOW_GRID] ?: false }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = isDark }
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

    suspend fun setOverlayOpacity(value: Float) {
        context.dataStore.edit { it[OVERLAY_OPACITY] = value }
    }

    suspend fun setTimerSeconds(value: Int) {
        context.dataStore.edit { it[TIMER_SECONDS] = value }
    }

    suspend fun setShowGrid(value: Boolean) {
        context.dataStore.edit { it[SHOW_GRID] = value }
    }
}
