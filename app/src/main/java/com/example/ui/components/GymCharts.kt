package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun WeeklyWorkoutsBarChart(
    workoutCounts: List<Int> = listOf(1, 1, 0, 1, 1, 0, 1), // Mon-Sun
    modifier: Modifier = Modifier
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val maxVal = (workoutCounts.maxOrNull() ?: 1).coerceAtLeast(1)

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(workoutCounts) {
        animProgress.animateTo(1f, animationSpec = tween(1000))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            workoutCounts.forEachIndexed { idx, count ->
                val barFraction = (count.toFloat() / maxVal.toFloat()) * animProgress.value
                val dayLabel = days.getOrElse(idx) { "" }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth(0.55f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barHeight = size.height * barFraction.coerceIn(0.1f, 1f)
                            val isToday = idx == 6

                            // Background bar track
                            drawRoundRect(
                                color = GymCardElevated,
                                size = Size(size.width, size.height),
                                cornerRadius = CornerRadius(12f, 12f)
                            )

                            // Active bar fill
                            if (count > 0) {
                                val brush = if (isToday) {
                                    Brush.verticalGradient(listOf(GymAccent, GymPrimary))
                                } else {
                                    Brush.verticalGradient(listOf(GymPrimary, GymPrimaryVariant))
                                }

                                drawRoundRect(
                                    brush = brush,
                                    topLeft = Offset(0f, size.height - barHeight),
                                    size = Size(size.width, barHeight),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dayLabel,
                        fontSize = 11.sp,
                        fontWeight = if (idx == 6) FontWeight.Bold else FontWeight.Medium,
                        color = if (idx == 6) GymAccent else GymTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun WeightProgressLineChart(
    weights: List<Float> = listOf(74.2f, 75.0f, 75.8f, 76.2f, 76.5f),
    modifier: Modifier = Modifier
) {
    if (weights.isEmpty()) return

    val minW = (weights.minOrNull() ?: 70f) - 1f
    val maxW = (weights.maxOrNull() ?: 80f) + 1f
    val range = (maxW - minW).coerceAtLeast(1f)

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(weights) {
        animProgress.animateTo(1f, animationSpec = tween(1200))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val spacing = width / (weights.size - 1).coerceAtLeast(1)

            val points = weights.mapIndexed { idx, w ->
                val x = idx * spacing
                val normalizedY = (w - minW) / range
                val y = height - (normalizedY * height * 0.8f) - (height * 0.1f)
                Offset(x, y)
            }

            // Draw area gradient fill
            val pathFill = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, height)
                    lineTo(points.first().x, points.first().y)
                    for (i in 0 until points.size - 1) {
                        val p1 = points[i]
                        val p2 = points[i + 1]
                        val cx = (p1.x + p2.x) / 2
                        cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
                    }
                    lineTo(points.last().x, height)
                    close()
                }
            }

            drawPath(
                path = pathFill,
                brush = Brush.verticalGradient(
                    colors = listOf(GymAccent.copy(alpha = 0.35f * animProgress.value), Color.Transparent)
                )
            )

            // Draw line
            val strokePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    for (i in 0 until points.size - 1) {
                        val p1 = points[i]
                        val p2 = points[i + 1]
                        val cx = (p1.x + p2.x) / 2
                        cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
                    }
                }
            }

            drawPath(
                path = strokePath,
                color = GymAccent,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw points
            points.forEach { pt ->
                drawCircle(
                    color = GymBackground,
                    radius = 6.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = GymAccent,
                    radius = 4.dp.toPx(),
                    center = pt
                )
            }
        }
    }
}

@Composable
fun ProgressRingCard(
    progress: Float, // 0.0f to 1.0f
    title: String,
    subtitle: String,
    ringColor: Color = GymAccent,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(progress) {
        animProgress.animateTo(progress.coerceIn(0f, 1f), animationSpec = tween(1000))
    }

    GlassCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = GymTextSecondary
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    // Background Ring
                    drawCircle(
                        color = GymCardElevated,
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress Arc
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animProgress.value,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymTextPrimary
                )
            }
        }
    }
}
