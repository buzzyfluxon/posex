/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.camera.VolumeShutterController
import com.example.data.AppSettings
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
      val handler = VolumeShutterController.onVolumeKeyPressed
      if (handler != null) {
        handler()
        return true
      }
    }
    return super.onKeyDown(keyCode, event)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val appSettings = AppSettings(applicationContext)
    setContent {
      val isDarkMode by appSettings.darkModeFlow.collectAsStateWithLifecycle(initialValue = true)
      MyApplicationTheme(darkTheme = isDarkMode, dynamicColor = false) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          AppNavigation(appSettings = appSettings)
        }
      }
    }
  }
}
