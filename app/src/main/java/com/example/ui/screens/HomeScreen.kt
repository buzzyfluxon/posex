/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.LocalIndication
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IconCircle
import com.example.ui.theme.MauveCard
import com.example.ui.theme.MauveCardDark
import com.example.ui.theme.Pink80
import com.example.ui.theme.PolaroidCream
import com.example.ui.theme.RowSurface
import com.example.ui.theme.iosPressAnimation
import com.example.ui.theme.iosPressAnimationSubtle

@Composable
fun HomeScreen(
    onNavigateToCamera: (String?) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                onNavigateToCamera(uri.toString())
            }
        }
    )

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

                val path2 = Path().apply {
                    moveTo(0f, size.height * 0.65f)
                    quadraticBezierTo(
                        size.width * 0.2f, size.height * 0.65f,
                        size.width * 0.35f, size.height
                    )
                }
                drawPath(
                    path = path2,
                    color = Pink80.copy(alpha = 0.3f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 64.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.White)) {
                                append("Pose ")
                            }
                            withStyle(style = SpanStyle(color = Pink80)) {
                                append("X")
                            }
                        },
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Better poses. Brighter you.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                val settingsInteractionSource = remember { MutableInteractionSource() }
                IconButton(
                    onClick = onNavigateToSettings,
                    interactionSource = settingsInteractionSource,
                    modifier = Modifier.iosPressAnimation(settingsInteractionSource)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = Pink80,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF1A1A1A))
                    .border(1.dp, Pink80.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
                    .padding(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(78.dp, 96.dp)
                                .offset(x = 16.dp, y = (-6).dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MauveCardDark)
                        )
                        Box(
                            modifier = Modifier
                                .size(78.dp, 96.dp)
                                .offset(x = (-16).dp, y = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MauveCard)
                        )
                        Box(
                            modifier = Modifier
                                .size(88.dp, 108.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PolaroidCream)
                                .padding(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MauveCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = Pink80,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Overlay a pose",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontStyle = FontStyle.Italic
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Pick an image, adjust it, and\nget the perfect shot.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    val startInteractionSource = remember { MutableInteractionSource() }
                    Button(
                        onClick = { onNavigateToCamera(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = Pink80),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(0.dp),
                        interactionSource = startInteractionSource,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp)
                            .iosPressAnimation(startInteractionSource)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Start",
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            QuickAccessItem(
                icon = Icons.Outlined.Image,
                title = "Pick Image",
                subtitle = "Choose from your gallery",
                onClick = {
                    photoPickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            QuickAccessItem(
                icon = Icons.Outlined.PhotoCamera,
                title = "Camera",
                subtitle = "Open camera directly",
                onClick = { onNavigateToCamera(null) }
            )
        }
    }
}

@Composable
fun QuickAccessItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
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
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(IconCircle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Pink80,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = Color.White.copy(alpha = 0.5f)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f)
        )
    }
}
