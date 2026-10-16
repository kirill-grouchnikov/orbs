package org.pushingpixels.orbs

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.pushingpixels.orbs.engine.MODE_FRAMES
import kotlin.math.roundToInt

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

/**
 * ThinkingOrb — a Compose component that renders animated thinking orbs.
 *
 * Ported from the TypeScript thinking-orbs library by Jakub Antalík.
 */
@Composable
public fun ThinkingOrb(
    modifier: Modifier = Modifier,
    state: OrbState = OrbState.Working,
    size: OrbSize = OrbSize.Large,
    speed: Double = 1.0,
    paused: Boolean = false,
    isDark: Boolean = true
) {
    val sizeDp = size.value
    val resolved = remember(state, size) { resolvePreset(state, sizeDp) }
    val modeFrame = remember(resolved.mode) { MODE_FRAMES[resolved.mode] }
    val effectiveSpeed = resolved.speed * speed

    val animatableFloat = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            // Animate from current value to value + 100f
            animatableFloat.animateTo(
                targetValue = animatableFloat.value + 1000f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        }
    }

    val density = LocalDensity.current.density
    val frame = rememberUpdatedState(
        modeFrame?.invoke(density * sizeDp.toDouble(), (animatableFloat.value / 1000f) * effectiveSpeed, resolved.opts)
    )

    Canvas(
        modifier = Modifier.size(sizeDp.dp, sizeDp.dp).then(modifier)
    ) {
        val currentFrame = frame.value ?: return@Canvas

        // Draw lines first (for connecting mode)
        for (line in currentFrame.lines) {
            val alpha = line.a
            val w = if (isDark) 1 - line.white else line.white
            val g = (w * 255).roundToInt()
            drawLine(
                color = Color(red = g, green = g, blue = g, alpha = (alpha * 255).roundToInt()),
                start = Offset(line.x1.toFloat(), line.y1.toFloat()),
                end = Offset(line.x2.toFloat(), line.y2.toFloat()),
                strokeWidth = line.w.toFloat() * density
            )
        }

        // Draw dots
        for (dot in currentFrame.dots) {
            val alpha = dot.a
            val w = if (isDark) 1 - dot.white else dot.white
            val g = (w * 255).roundToInt()
            drawCircle(
                color = Color(red = g, green = g, blue = g, alpha = (alpha * 255).roundToInt()),
                center = Offset(dot.x.toFloat(), dot.y.toFloat()),
                radius = dot.r.toFloat()
            )
        }
    }
}
