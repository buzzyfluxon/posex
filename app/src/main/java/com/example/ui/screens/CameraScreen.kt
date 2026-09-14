/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example.ui.screens

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.data.AppSettings
import com.example.ui.theme.IconCircle
import com.example.ui.theme.Pink80
import com.example.ui.theme.PillTrack
import com.example.ui.theme.iosPressAnimation
import com.example.ui.theme.iosPressAnimationSubtle
import kotlinx.coroutines.delay
import android.Manifest

@Composable
fun CameraScreen(
    initialImageUri: String?,
    appSettings: AppSettings,
    onNavigateBack: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToSettings: () -> Unit
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
                val grantInteractionSource = remember { MutableInteractionSource() }
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    interactionSource = grantInteractionSource,
                    modifier = Modifier.iosPressAnimation(grantInteractionSource)
                ) {
                    Text("Grant Permission")
                }
            }
        }
        return
    }

    var availableCameras by remember { mutableStateOf<List<CameraInfo>>(emptyList()) }
    var selectedCameraIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val infos = cameraProvider.availableCameraInfos
        if (infos.isNotEmpty()) {
            availableCameras = infos
            val firstBackIndex = infos.indexOfFirst {
                CameraSelector.DEFAULT_BACK_CAMERA.filter(listOf(it)).isNotEmpty()
            }
            selectedCameraIndex = if (firstBackIndex >= 0) firstBackIndex else 0
        }
    }

    val cameraSelector: CameraSelector? = remember(availableCameras, selectedCameraIndex) {
        val info = availableCameras.getOrNull(selectedCameraIndex) ?: return@remember null
        CameraSelector.Builder()
            .addCameraFilter { infos -> infos.filter { it == info } }
            .build()
    }

    val switchToNextCamera: () -> Unit = {
        if (availableCameras.isNotEmpty()) {
            selectedCameraIndex = (selectedCameraIndex + 1) % availableCameras.size
        }
    }

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

        if (cameraSelector != null) {
            CameraPreview(
                cameraSelector = cameraSelector,
                imageCapture = imageCapture,
                lifecycleOwner = lifecycleOwner,
                modifier = Modifier.fillMaxSize()
            )
        }

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
            val closeInteractionSource = remember { MutableInteractionSource() }
            IconButton(
                onClick = onNavigateBack,
                interactionSource = closeInteractionSource,
                modifier = Modifier.iosPressAnimation(closeInteractionSource)
            ) {
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
                val galleryInteractionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .iosPressAnimationSubtle(galleryInteractionSource)
                        .clip(RoundedCornerShape(50))
                        .clickable(interactionSource = galleryInteractionSource, indication = LocalIndication.current) { onNavigateToGallery() }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Gallery", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                val gridInteractionSource = remember { MutableInteractionSource() }
                IconButton(
                    onClick = { showGrid = !showGrid },
                    interactionSource = gridInteractionSource,
                    modifier = Modifier.iosPressAnimation(gridInteractionSource)
                ) {
                    Icon(
                        Icons.Filled.GridOn,
                        "Toggle Grid",
                        tint = if (showGrid) Pink80 else Color.White
                    )
                }
                val flashInteractionSource = remember { MutableInteractionSource() }
                IconButton(
                    onClick = {
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                            else -> ImageCapture.FLASH_MODE_OFF
                        }
                    },
                    interactionSource = flashInteractionSource,
                    modifier = Modifier.iosPressAnimation(flashInteractionSource)
                ) {
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
                val timerInteractionSource = remember { MutableInteractionSource() }
                IconButton(
                    onClick = {
                        val currentIndex = timerOptions.indexOf(timerSeconds)
                        timerSeconds = timerOptions[(currentIndex + 1) % timerOptions.size]
                    },
                    interactionSource = timerInteractionSource,
                    modifier = Modifier.iosPressAnimation(timerInteractionSource)
                ) {
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
                val flipInteractionSource = remember { MutableInteractionSource() }
                IconButton(
                    onClick = switchToNextCamera,
                    interactionSource = flipInteractionSource,
                    modifier = Modifier.iosPressAnimation(flipInteractionSource),
                    enabled = availableCameras.size > 1
                ) {
                    Icon(Icons.Filled.Refresh, "Switch Camera", tint = Color.White)
                }
            }
        }

        if (availableCameras.size > 2) {
            CameraSwitcherBar(
                cameras = availableCameras,
                selectedIndex = selectedCameraIndex,
                onSelect = { selectedCameraIndex = it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 172.dp)
            )
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
                    Text("Opacity\n${(opacity * 100).toInt()}%", color = Color.White, fontSize = 11.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    VerticalOpacitySlider(
                        value = opacity,
                        onValueChange = { opacity = it },
                        modifier = Modifier
                            .width(40.dp)
                            .height(180.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                val zoomInteractionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .iosPressAnimation(zoomInteractionSource)
                        .clip(CircleShape)
                        .background(IconCircle)
                        .clickable(interactionSource = zoomInteractionSource, indication = LocalIndication.current) {
                            scale = (scale * 1.2f).coerceIn(0.5f, 5f)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ZoomIn, "Zoom", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))

                val fitInteractionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .iosPressAnimation(fitInteractionSource)
                        .clip(CircleShape)
                        .background(IconCircle)
                        .clickable(interactionSource = fitInteractionSource, indication = LocalIndication.current) {
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

                val moveInteractionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .iosPressAnimation(moveInteractionSource)
                        .clip(CircleShape)
                        .background(IconCircle)
                        .clickable(interactionSource = moveInteractionSource, indication = LocalIndication.current) {
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

                val pickImageInteractionSource = remember { MutableInteractionSource() }
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    interactionSource = pickImageInteractionSource,
                    modifier = Modifier
                        .size(48.dp)
                        .iosPressAnimation(pickImageInteractionSource)
                        .clip(RoundedCornerShape(14.dp))
                        .background(IconCircle)
                ) {
                    Icon(Icons.Outlined.Image, "Pick Image", tint = Color.White)
                }

                val shutterInteractionSource = remember { MutableInteractionSource() }
                val isShutterPressed by shutterInteractionSource.collectIsPressedAsState()
                val shutterInnerScale by animateFloatAsState(
                    targetValue = if (isShutterPressed) 0.85f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    ),
                    label = "shutterScale"
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(3.dp, Pink80, CircleShape)
                        .clickable(
                            interactionSource = shutterInteractionSource,
                            indication = LocalIndication.current,
                            enabled = !isCapturing && countdownValue == null
                        ) {
                            triggerCapture()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .graphicsLayer { scaleX = shutterInnerScale; scaleY = shutterInnerScale }
                            .clip(CircleShape)
                            .background(Pink80)
                    )
                }

                val tuneInteractionSource = remember { MutableInteractionSource() }
                IconButton(
                    onClick = onNavigateToSettings,
                    interactionSource = tuneInteractionSource,
                    modifier = Modifier
                        .size(48.dp)
                        .iosPressAnimation(tuneInteractionSource)
                        .clip(RoundedCornerShape(14.dp))
                        .background(IconCircle)
                ) {
                    Icon(Icons.Outlined.Tune, "Settings", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun VerticalOpacitySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onValueChange((1f - offset.y / size.height.toFloat()).coerceIn(0f, 1f))
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onValueChange((1f - change.position.y / size.height.toFloat()).coerceIn(0f, 1f))
                    }
                )
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        val trackHeight = maxHeight

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(4.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.25f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(4.dp)
                .height(trackHeight * value)
                .clip(RoundedCornerShape(2.dp))
                .background(Pink80)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 10.dp - trackHeight * value)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, Pink80, CircleShape)
        )
    }
}

@Composable
fun CameraSwitcherBar(
    cameras: List<CameraInfo>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var backCount = 0
    var frontCount = 0
    val labels = cameras.map { info ->
        val isBack = CameraSelector.DEFAULT_BACK_CAMERA.filter(listOf(info)).isNotEmpty()
        if (isBack) {
            backCount += 1
            if (cameras.count { CameraSelector.DEFAULT_BACK_CAMERA.filter(listOf(it)).isNotEmpty() } > 1) "Back $backCount" else "Back"
        } else {
            frontCount += 1
            if (cameras.count { CameraSelector.DEFAULT_FRONT_CAMERA.filter(listOf(it)).isNotEmpty() } > 1) "Front $frontCount" else "Front"
        }
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(PillTrack)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .iosPressAnimationSubtle(interactionSource)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) Pink80 else Color.Transparent)
                    .clickable(interactionSource = interactionSource, indication = LocalIndication.current) { onSelect(index) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.8f),
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun CameraPreview(
    cameraSelector: CameraSelector,
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
