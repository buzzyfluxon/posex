/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.iosPressAnimation(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.94f,
    pressedAlpha: Float = 0.85f
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "iosPressScale"
    )
    val dim by animateFloatAsState(
        targetValue = if (isPressed) pressedAlpha else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "iosPressAlpha"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .alpha(dim)
}

@Composable
fun Modifier.iosPressAnimationSubtle(
    interactionSource: MutableInteractionSource
): Modifier = iosPressAnimation(
    interactionSource = interactionSource,
    pressedScale = 0.97f,
    pressedAlpha = 0.92f
)
