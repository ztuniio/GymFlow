package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*
import kotlin.random.Random

private data class ConfettiParticle(
    val xRatio: Float,
    val initialYRatio: Float,
    val speed: Float,
    val sizePx: Float,
    val color: Color,
    val angle: Float
)

@Composable
fun ConfettiOverlay(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val particles = remember {
        val colors = listOf(GymAccent, GymPrimary, GymOrange, GymCyan, GymPink, GymSuccess)
        List(60) {
            ConfettiParticle(
                xRatio = Random.nextFloat(),
                initialYRatio = -0.1f - Random.nextFloat() * 0.3f,
                speed = 0.8f + Random.nextFloat() * 0.8f,
                sizePx = 10f + Random.nextFloat() * 14f,
                color = colors[Random.nextInt(colors.size)],
                angle = Random.nextFloat() * 360f
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(isVisible) {
        progress.animateTo(1f, animationSpec = tween(2800))
        onDismiss()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            val currentY = (p.initialYRatio + (progress.value * p.speed * 1.4f)) * height
            val currentX = (p.xRatio * width) + (kotlin.math.sin((progress.value * 10) + p.angle) * 30f)

            if (currentY in 0f..height) {
                drawRect(
                    color = p.color,
                    topLeft = Offset(currentX, currentY),
                    size = Size(p.sizePx, p.sizePx * 1.5f)
                )
            }
        }
    }
}
