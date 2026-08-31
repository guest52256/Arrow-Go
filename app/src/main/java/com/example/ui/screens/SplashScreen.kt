package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }

    // Continuous rotation for outer arrow orbit
    val infiniteTransition = rememberInfiniteTransition(label = "splash_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Progress loading timer
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        val duration = 2200L
        while (System.currentTimeMillis() - startTime < duration) {
            val elapsed = System.currentTimeMillis() - startTime
            progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
            delay(16)
        }
        progress = 1f
        delay(200)
        onSplashFinished()
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0F1D),
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF0A0F1D)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Center Logo & Title Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 3D Rotating Arrow Emblem Ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsing glow circle
                    Surface(
                        modifier = Modifier.size(150.dp),
                        shape = CircleShape,
                        color = Color(0xFF1E3A8A).copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFF38BDF8),
                                    Color(0xFFFBBF24),
                                    Color(0xFFEC4899),
                                    Color(0xFF10B981),
                                    Color(0xFF38BDF8)
                                )
                            )
                        )
                    ) {}

                    // Rotating Arrow Canvas
                    Canvas(
                        modifier = Modifier
                            .size(140.dp)
                            .rotate(rotation)
                    ) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width * 0.38f
                        val arrowColors = listOf(
                            Color(0xFF38BDF8), // Cyan
                            Color(0xFFFBBF24), // Gold
                            Color(0xFF10B981), // Emerald
                            Color(0xFFF43F5E)  // Rose
                        )

                        for (i in 0 until 4) {
                            val angleRad = Math.toRadians((i * 90.0)).toFloat()
                            val arrowX = center.x + radius * cos(angleRad)
                            val arrowY = center.y + radius * sin(angleRad)
                            val color = arrowColors[i]

                            // Draw directional arrow tip
                            val path = Path().apply {
                                val s = 14.dp.toPx()
                                val tipAngle = angleRad + Math.PI.toFloat() / 2f
                                val headX = arrowX + s * cos(tipAngle)
                                val headY = arrowY + s * sin(tipAngle)
                                val leftX = arrowX + s * 0.6f * cos(tipAngle + 2.4f)
                                val leftY = arrowY + s * 0.6f * sin(tipAngle + 2.4f)
                                val rightX = arrowX + s * 0.6f * cos(tipAngle - 2.4f)
                                val rightY = arrowY + s * 0.6f * sin(tipAngle - 2.4f)

                                moveTo(headX, headY)
                                lineTo(leftX, leftY)
                                lineTo(arrowX, arrowY)
                                lineTo(rightX, rightY)
                                close()
                            }
                            drawPath(path = path, color = color)
                            drawCircle(
                                color = color,
                                radius = 4.dp.toPx(),
                                center = Offset(arrowX, arrowY)
                            )
                        }
                    }

                    // Center Core Jewel
                    Surface(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(12.dp, CircleShape),
                        shape = CircleShape,
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF38BDF8))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = "Arrows Emblem",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Game Title
                Text(
                    text = "Arrows Go-",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Puzzle & Brain Challenge",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF38BDF8),
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Winding Path Logic & 3D Tactile Solves",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Progress Bar & Loading Indicator
                Column(
                    modifier = Modifier.width(240.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF38BDF8),
                        trackColor = Color(0xFF334155),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Loading Levels... ${(progress * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Developer Attribution Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .testTag("developer_attribution_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E293B).copy(alpha = 0.9f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF38BDF8),
                            Color(0xFFFBBF24),
                            Color(0xFF10B981)
                        )
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Badge",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Developed By Kinza Digital Hub",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "CEO Sadaqat Ali",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }
        }
    }
}
