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

/**
 * iOS-style press feedback: a quick, springy scale-down plus a slight dim while the
 * finger is down, bouncing back to normal on release. This mirrors the tactile feel of
 * UIKit/SwiftUI controls (e.g. UIButton highlight state, SwiftUI's default button press).
 *
 * Usage: create (and `remember`) a [MutableInteractionSource], pass it both to this
 * modifier AND to the clickable/Button/IconButton/etc. so the press state driving the
 * animation matches the actual touch target:
 *
 * ```
 * val interactionSource = remember { MutableInteractionSource() }
 * Button(
 *     onClick = { ... },
 *     interactionSource = interactionSource,
 *     modifier = Modifier.iosPressAnimation(interactionSource)
 * ) { ... }
 * ```
 */
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

/**
 * Slightly gentler variant for large surfaces (cards, list rows) where a big scale
 * change would look jarring — same spring feel, smaller travel.
 */
@Composable
fun Modifier.iosPressAnimationSubtle(
    interactionSource: MutableInteractionSource
): Modifier = iosPressAnimation(
    interactionSource = interactionSource,
    pressedScale = 0.97f,
    pressedAlpha = 0.92f
)
