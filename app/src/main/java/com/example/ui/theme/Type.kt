/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.R

private val fontProvider =
  GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
  )

private val bodoniModa = GoogleFont("Bodoni Moda")

val BodoniModaFamily =
  FontFamily(
    Font(googleFont = bodoniModa, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = bodoniModa, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = bodoniModa, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = bodoniModa, fontProvider = fontProvider, weight = FontWeight.Bold),
  )

val Typography =
  Typography(
    displayLarge =
      TextStyle(
        fontFamily = BodoniModaFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
      ),
    headlineLarge =
      TextStyle(
        fontFamily = BodoniModaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = BodoniModaFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = BodoniModaFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = BodoniModaFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = BodoniModaFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
      )
  )
