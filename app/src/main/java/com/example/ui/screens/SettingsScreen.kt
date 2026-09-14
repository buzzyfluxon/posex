/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example.ui.screens

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoSizeSelectLarge
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppSettings
import com.example.ui.theme.IconCircle
import com.example.ui.theme.Pink80
import com.example.ui.theme.RowSurface
import com.example.ui.theme.iosPressAnimation
import com.example.ui.theme.iosPressAnimationSubtle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appSettings: AppSettings,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isDarkMode by appSettings.darkModeFlow.collectAsStateWithLifecycle(initialValue = true)
    val cameraQuality by appSettings.cameraQualityFlow.collectAsStateWithLifecycle(initialValue = "High")
    val frameRate by appSettings.frameRateFlow.collectAsStateWithLifecycle(initialValue = "60 Hz")
    val imageQuality by appSettings.imageQualityFlow.collectAsStateWithLifecycle(initialValue = "100%")

    var showAboutDialog by remember { mutableStateOf(false) }
    var showCameraQualityDialog by remember { mutableStateOf(false) }
    var showFrameRateDialog by remember { mutableStateOf(false) }
    var showImageQualityDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .drawBehind {

                val path1 = Path().apply {
                    moveTo(size.width * 0.7f, 0f)
                    quadraticBezierTo(
                        size.width * 0.8f, size.height * 0.15f,
                        size.width, size.height * 0.2f
                    )
                }
                drawPath(
                    path = path1,
                    color = Pink80.copy(alpha = 0.5f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { },
                navigationIcon = {
                    val backInteractionSource = remember { MutableInteractionSource() }
                    IconButton(
                        onClick = onNavigateBack,
                        interactionSource = backInteractionSource,
                        modifier = Modifier.iosPressAnimation(backInteractionSource)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = "Back",
                            tint = Pink80,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        brush = Brush.linearGradient(listOf(Color.White, Pink80))
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Make it yours.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                SettingsRowToggle(
                    icon = Icons.Rounded.Nightlight,
                    title = "Dark Mode",
                    subtitle = "Better on your eyes",
                    checked = isDarkMode,
                    onCheckedChange = {
                        coroutineScope.launch { appSettings.setDarkMode(it) }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "CAMERA SETTINGS",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, fontSize = 11.sp),
                    color = Pink80,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                SettingsRowClickable(
                    icon = Icons.Rounded.PhotoCamera,
                    title = "Camera Quality",
                    subtitle = "Choose the output quality",
                    value = cameraQuality,
                    onClick = { showCameraQualityDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsRowClickable(
                    icon = Icons.Rounded.Speed,
                    title = "Frame Rate (Hertz)",
                    subtitle = "Smoothness of the preview",
                    value = frameRate,
                    onClick = { showFrameRateDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsRowClickable(
                    icon = Icons.Rounded.PhotoSizeSelectLarge,
                    title = "Image Quality",
                    subtitle = "Higher quality, larger size",
                    value = imageQuality,
                    onClick = { showImageQualityDialog = true }
                )

                Spacer(modifier = Modifier.height(24.dp))

                SettingsRowClickable(
                    icon = Icons.Rounded.Info,
                    title = "About",
                    subtitle = "App version & more",
                    value = "",
                    onClick = { showAboutDialog = true }
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(1.dp)
                            .background(Pink80.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Made with love by",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "@buzzyfluxon",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, fontSize = 13.sp),
                        color = Pink80
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Pink80,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Pose X") },
            text = { Text("Pose X\nVersion 1.0\nPose-reference camera application.") },
            confirmButton = {
                val closeInteractionSource = remember { MutableInteractionSource() }
                TextButton(
                    onClick = { showAboutDialog = false },
                    interactionSource = closeInteractionSource,
                    modifier = Modifier.iosPressAnimation(closeInteractionSource)
                ) {
                    Text("Close")
                }
            },
            containerColor = Color(0xFF1A1A1A),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    if (showCameraQualityDialog) {
        SettingsChoiceDialog(
            title = "Camera Quality",
            options = listOf("Low", "Medium", "High"),
            selected = cameraQuality,
            onSelect = { coroutineScope.launch { appSettings.setCameraQuality(it) } },
            onDismiss = { showCameraQualityDialog = false }
        )
    }

    if (showFrameRateDialog) {
        SettingsChoiceDialog(
            title = "Frame Rate",
            options = listOf("30 Hz", "60 Hz", "120 Hz"),
            selected = frameRate,
            onSelect = { coroutineScope.launch { appSettings.setFrameRate(it) } },
            onDismiss = { showFrameRateDialog = false }
        )
    }

    if (showImageQualityDialog) {
        SettingsChoiceDialog(
            title = "Image Quality",
            options = listOf("50%", "75%", "100%"),
            selected = imageQuality,
            onSelect = { coroutineScope.launch { appSettings.setImageQuality(it) } },
            onDismiss = { showImageQualityDialog = false }
        )
    }
}

@Composable
fun SettingsChoiceDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { option ->
                    val isSelected = option == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .selectable(
                                selected = isSelected,
                                onClick = {
                                    onSelect(option)
                                    onDismiss()
                                }
                            )
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                onSelect(option)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Pink80,
                                unselectedColor = Color.White.copy(alpha = 0.5f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option, color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            val closeInteractionSource = remember { MutableInteractionSource() }
            TextButton(
                onClick = onDismiss,
                interactionSource = closeInteractionSource,
                modifier = Modifier.iosPressAnimation(closeInteractionSource)
            ) {
                Text("Done")
            }
        },
        containerColor = Color(0xFF1A1A1A),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
fun SettingsRowToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(RowSurface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(IconCircle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Pink80,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp), color = Color.White)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp), color = Color.White.copy(alpha = 0.5f))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Pink80,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF333333),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun SettingsRowClickable(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .iosPressAnimationSubtle(interactionSource)
            .clip(RoundedCornerShape(20.dp))
            .background(RowSurface)
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(IconCircle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Pink80,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp), color = Color.White)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp), color = Color.White.copy(alpha = 0.5f))
        }
        if (value.isNotEmpty()) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp), color = Color.White.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.width(16.dp))
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f)
        )
    }
}
