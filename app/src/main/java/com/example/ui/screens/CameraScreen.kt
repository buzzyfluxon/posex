/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example.ui.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Timer3
import androidx.compose.material.icons.filled.Timer10
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.AppSettings
import com.example.ui.theme.IconCircle
import com.example.ui.theme.Pink80
import com.example.ui.theme.PillTrack
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.delay
import android.Manifest

@Composable
fun CameraScreen(
    initialImageUri: String?,
    appSettings: AppSettings,
    onNavigateBack: () -> Unit,
    onNavigateToGallery: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (!hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission is required.", color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
            }
        }
        return
    }

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableStateOf(ImageCapture.FLASH_MODE_OFF) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(flashMode) {
        imageCapture.flashMode = flashMode
    }

    val timerOptions = listOf(0, 3, 10)
    var timerSeconds by remember { mutableStateOf(0) }
    var countdownValue by remember { mutableStateOf<Int?>(null) }

    var imageUri by remember { mutableStateOf<String?>(initialImageUri) }
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var rotation by remember { mutableStateOf(0f) }
    var opacity by remember { mutableStateOf(0.6f) }
    var isOverlayVisible by remember { mutableStateOf(true) }

    var showGrid by remember { mutableStateOf(false) }
    var showOpacitySlider by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }

    var aiInstruction by remember { mutableStateOf<String?>(null) }

    val startCapture: () -> Unit = {
        isCapturing = true
        takePhoto(
            context = context,
            imageCapture = imageCapture,
            onPhotoTaken = { isCapturing = false },
            onError = { isCapturing = false }
        )
    }

    val triggerCapture: () -> Unit = {
        if (timerSeconds > 0) {
            countdownValue = timerSeconds
        } else {
            startCapture()
        }
    }

    LaunchedEffect(countdownValue) {
        val current = countdownValue ?: return@LaunchedEffect
        if (current > 0) {
            delay(1000)
            countdownValue = current - 1
        } else {
            countdownValue = null
            startCapture()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                imageUri = uri.toString()
                scale = 1f
                offsetX = 0f
                offsetY = 0f
                rotation = 0f
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        CameraPreview(
            lensFacing = lensFacing,
            imageCapture = imageCapture,
            lifecycleOwner = lifecycleOwner,
            modifier = Modifier.fillMaxSize()
        )

        if (showGrid) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stepX = size.width / 3
                val stepY = size.height / 3
                for (i in 1..2) {
                    drawLine(color = Color.White.copy(alpha = 0.5f), start = Offset(stepX * i, 0f), end = Offset(stepX * i, size.height), strokeWidth = 2f)
                    drawLine(color = Color.White.copy(alpha = 0.5f), start = Offset(0f, stepY * i), end = Offset(size.width, stepY * i), strokeWidth = 2f)
                }
            }
        }

        if (imageUri != null && isOverlayVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, panRotation ->
                            scale = (scale * zoom).coerceIn(0.5f, 5f)
                            offsetX += pan.x
                            offsetY += pan.y
                            rotation += panRotation
                        }
                    }
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Reference",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY,
                            rotationZ = rotation,
                            alpha = opacity
                        )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Outlined.Close, "Close", tint = Color.White)
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(PillTrack)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Pink80)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Camera", color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onNavigateToGallery() }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Gallery", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showGrid = !showGrid }) {
                    Icon(
                        Icons.Filled.GridOn,
                        "Toggle Grid",
                        tint = if (showGrid) Pink80 else Color.White
                    )
                }
                IconButton(onClick = {
                    flashMode = when (flashMode) {
                        ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                        ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                        else -> ImageCapture.FLASH_MODE_OFF
                    }
                }) {
                    Icon(
                        when (flashMode) {
                            ImageCapture.FLASH_MODE_ON -> Icons.Filled.FlashOn
                            ImageCapture.FLASH_MODE_AUTO -> Icons.Filled.FlashAuto
                            else -> Icons.Filled.FlashOff
                        },
                        "Toggle Flash",
                        tint = if (flashMode == ImageCapture.FLASH_MODE_OFF) Color.White else Pink80
                    )
                }
                IconButton(onClick = {
                    val currentIndex = timerOptions.indexOf(timerSeconds)
                    timerSeconds = timerOptions[(currentIndex + 1) % timerOptions.size]
                }) {
                    Icon(
                        when (timerSeconds) {
                            3 -> Icons.Filled.Timer3
                            10 -> Icons.Filled.Timer10
                            else -> Icons.Filled.Timer
                        },
                        "Toggle Timer",
                        tint = if (timerSeconds == 0) Color.White else Pink80
                    )
                }
                IconButton(onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                }) {
                    Icon(Icons.Filled.Refresh, "Flip Camera", tint = Color.White)
                }
            }
        }

        if (imageUri != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF222222))
                        .padding(vertical = 24.dp, horizontal = 12.dp)
                ) {
                    Text("Opacity\n${(opacity * 100).toInt()}%", color = Color.White, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = opacity,
                        onValueChange = { opacity = it },
                        modifier = Modifier.height(180.dp).graphicsLayer(rotationZ = -90f, translationY = -12f),
                        colors = SliderDefaults.colors(
                            thumbColor = Pink80,
                            activeTrackColor = Pink80,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(IconCircle)
                        .clickable { scale = (scale * 1.2f).coerceIn(0.5f, 5f) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ZoomIn, "Zoom", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(IconCircle)
                        .clickable {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                            rotation = 0f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ZoomOutMap, "Fit to screen", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(IconCircle)
                        .clickable {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                            rotation = 0f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.OpenWith, "Move", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        if (countdownValue != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$countdownValue",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp
                )
            }
        }

        if (aiInstruction != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(aiInstruction!!, color = Color.White, fontWeight = FontWeight.Medium)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Photo", color = Pink80, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Portrait", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Text("Video", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Text("More", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(IconCircle)
                ) {
                    Icon(Icons.Outlined.Image, "Pick Image", tint = Color.White)
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(3.dp, Pink80, CircleShape)
                        .clickable(enabled = !isCapturing && countdownValue == null) {
                            triggerCapture()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Pink80)
                    )
                }

                IconButton(
                    onClick = {  },
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(IconCircle)
                ) {
                    Icon(Icons.Outlined.Tune, "Settings", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    lensFacing: Int,
    imageCapture: ImageCapture,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = modifier,
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", e)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoTaken: () -> Unit,
    onError: (Exception) -> Unit
) {
    val name = "PoseX_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onPhotoTaken()
            }
            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraScreen", "Photo capture failed: ${exc.message}", exc)
                onError(exc)
            }
        }
    )
}
