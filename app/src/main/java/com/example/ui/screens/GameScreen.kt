package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ArrowLevels
import com.example.model.*
import com.example.viewmodel.GameViewModel
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    userProfile: UserProfile?,
    onBack: () -> Unit
) {
    val activeLevel by viewModel.currentLevel.collectAsState()
    val activeArrows by viewModel.activeArrows.collectAsState()
    val remainingCount by viewModel.remainingArrowsCount.collectAsState()
    val totalCount by viewModel.totalArrowsCount.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val moves by viewModel.moves.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val goldenScore by viewModel.goldenScore.collectAsState()
    val flyingTokens by viewModel.flyingTokens.collectAsState()
    val isMagnifierActive by viewModel.isMagnifierActive.collectAsState()
    val magnifierPos by viewModel.magnifierPosition.collectAsState()
    val paletteTheme by viewModel.paletteTheme.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val gameOverReason by viewModel.gameOverReason.collectAsState()
    val lastReward by viewModel.lastClearedReward.collectAsState()
    val starsEarned by viewModel.starsEarned.collectAsState()
    val completionTime by viewModel.completionTimeSeconds.collectAsState()
    val speedBonus by viewModel.speedBonusSeconds.collectAsState()
    val blockedArrowId by viewModel.blockedArrowId.collectAsState()
    val blockedCollisionPoint by viewModel.blockedCollisionPoint.collectAsState()
    val scorePopups by viewModel.scorePopups.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var useBoardLines by remember { mutableStateOf(true) }
    var strokeThickness by remember { mutableStateOf(8.dp) }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "game_screen_anims")
    
    // Shimmer for golden score badge
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "golden_shimmer"
    )

    // Timer urgency pulse
    val timerUrgentPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "timer_pulse"
    )

    val blockedShake by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blocked_shake"
    )

    val hintPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val handBounce by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hand_bounce"
    )

    // Sunburst ray rotation for victory screen
    val sunburstRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunburst_spin"
    )

    // Dynamic timer color
    val timerColor = when {
        timerSeconds > 30 -> Color(0xFF38BDF8) // Dark Blue / Cyan
        timerSeconds > 10 -> Color(0xFFF59E0B) // Warning Orange
        else -> Color(0xFFEF4444) // Urgent Red
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
    )

    fun getArrowColor(colorName: String): Color {
        if (colorName.equals("Navy", ignoreCase = true)) {
            return Color(0xFF13204A)
        }
        return when (paletteTheme) {
            ColorPaletteTheme.NEON_GLOW -> when (colorName) {
                "Navy" -> Color(0xFF13204A)
                "Purple" -> Color(0xFFA855F7)
                "Green" -> Color(0xFF10B981)
                "Red" -> Color(0xFFEF4444)
                "Blue" -> Color(0xFF38BDF8)
                "Orange" -> Color(0xFFF59E0B)
                "Yellow" -> Color(0xFFFBBF24)
                "Cyan" -> Color(0xFF06B6D4)
                "Pink" -> Color(0xFFEC4899)
                else -> Color(0xFF13204A)
            }
            ColorPaletteTheme.CANDY_PASTEL -> when (colorName) {
                "Navy" -> Color(0xFF13204A)
                "Purple" -> Color(0xFFC084FC)
                "Green" -> Color(0xFF6EE7B7)
                "Red" -> Color(0xFFFCA5A5)
                "Blue" -> Color(0xFF93C5FD)
                "Orange" -> Color(0xFFFDBA74)
                "Yellow" -> Color(0xFFFDE047)
                "Cyan" -> Color(0xFF67E8F9)
                "Pink" -> Color(0xFFF472B6)
                else -> Color(0xFF93C5FD)
            }
            ColorPaletteTheme.RETRO_ARCADE -> when (colorName) {
                "Navy" -> Color(0xFF13204A)
                "Purple" -> Color(0xFF8B5CF6)
                "Green" -> Color(0xFF22C55E)
                "Red" -> Color(0xFFDC2626)
                "Blue" -> Color(0xFF2563EB)
                "Orange" -> Color(0xFFEA580C)
                "Yellow" -> Color(0xFFEAB308)
                "Cyan" -> Color(0xFF0891B2)
                "Pink" -> Color(0xFFDB2777)
                else -> Color(0xFF2563EB)
            }
            ColorPaletteTheme.CYBER_PUNK -> when (colorName) {
                "Navy" -> Color(0xFF13204A)
                "Purple" -> Color(0xFFD946EF)
                "Green" -> Color(0xFF4ADE80)
                "Red" -> Color(0xFFF43F5E)
                "Blue" -> Color(0xFF00F0FF)
                "Orange" -> Color(0xFFFF5E00)
                "Yellow" -> Color(0xFFFFE600)
                "Cyan" -> Color(0xFF00FFFF)
                "Pink" -> Color(0xFFFF007F)
                else -> Color(0xFF00F0FF)
            }
            ColorPaletteTheme.CLASSIC_CLEAN -> when (colorName) {
                "Navy" -> Color(0xFF13204A)
                "Purple" -> Color(0xFF6366F1)
                "Green" -> Color(0xFF059669)
                "Red" -> Color(0xFFE11D48)
                "Blue" -> Color(0xFF13204A)
                "Orange" -> Color(0xFFD97706)
                "Yellow" -> Color(0xFFCA8A04)
                "Cyan" -> Color(0xFF0D9488)
                "Pink" -> Color(0xFFBE185D)
                else -> Color(0xFF13204A)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ARROWS GO",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Lvl ${activeLevel.levelNumber} - ${activeLevel.name}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Difficulty Pill
                                Surface(
                                    color = when (activeLevel.difficulty) {
                                        Difficulty.BEGINNER -> Color(0xFF10B981)
                                        Difficulty.EASY -> Color(0xFF06B6D4)
                                        Difficulty.MEDIUM -> Color(0xFFF59E0B)
                                        Difficulty.HARD -> Color(0xFFEC4899)
                                        Difficulty.EXPERT, Difficulty.EXTREME -> Color(0xFFEF4444)
                                    }.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        when (activeLevel.difficulty) {
                                            Difficulty.BEGINNER -> Color(0xFF10B981)
                                            Difficulty.EASY -> Color(0xFF06B6D4)
                                            Difficulty.MEDIUM -> Color(0xFFF59E0B)
                                            Difficulty.HARD -> Color(0xFFEC4899)
                                            Difficulty.EXPERT, Difficulty.EXTREME -> Color(0xFFEF4444)
                                        }
                                    )
                                ) {
                                    Text(
                                        text = activeLevel.difficulty.name,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        // Golden Points Shiny Badge in Navigation Header
                        Surface(
                            modifier = Modifier
                                .testTag("golden_score_badge"),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFDE047),
                                        Color(0xFFF59E0B),
                                        Color(0xFFFBBF24),
                                        Color(0xFFFDE047)
                                    )
                                )
                            ),
                            shadowElevation = 6.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🪙", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$goldenScore pts",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFBBF24)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("game_settings_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Top Gameplay Stats Row (Hearts, 1-Min Timer, Arrows Count)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hearts / Lives Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "LIVES", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.padding(top = 2.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                for (i in 1..3) {
                                    Text(
                                        text = if (i <= hearts) "❤️" else "🖤",
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 1.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 1-Minute Countdown Timer Card
                    Card(
                        modifier = Modifier
                            .weight(1.1f)
                            .scale(if (timerSeconds <= 10) timerUrgentPulse else 1.0f)
                            .testTag("game_timer_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = if (timerSeconds <= 10) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Timer",
                                    tint = timerColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "TIME",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${timerSeconds}s",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = timerColor,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Remaining Arrows Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "REMAINING", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text(
                                text = "$remainingCount / $totalCount",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF10B981),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // 2. Interactive Winding-Path Puzzle Board Canvas Container
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(24.dp))
                        .padding(12.dp)
                        .testTag("puzzle_board_container"),
                    contentAlignment = Alignment.Center
                ) {
                    val boardWidth = maxWidth
                    val boardHeight = maxHeight
                    val hintedArrow = activeArrows.find { it.isHinted }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(activeArrows) {
                                detectTapGestures { offset ->
                                    val stepX = size.width / (activeLevel.gridWidth + 1f)
                                    val stepY = size.height / (activeLevel.gridHeight + 1f)
                                    val maxHitDist = maxOf(stepX, stepY) * 0.9f

                                    var clickedArrowId: String? = null
                                    var closestDist = Float.MAX_VALUE

                                    for (arrow in activeArrows) {
                                        val points = viewModel.getOccupiedPointsForArrow(arrow)
                                        for (pt in points) {
                                            val ptPxX = (pt.x + 1) * stepX
                                            val ptPxY = (pt.y + 1) * stepY
                                            val dist = hypot(offset.x - ptPxX, offset.y - ptPxY)
                                            if (dist < maxHitDist && dist < closestDist) {
                                                closestDist = dist
                                                clickedArrowId = arrow.id
                                            }
                                        }
                                    }

                                    clickedArrowId?.let { arrowId ->
                                        val normX = (offset.x / size.width).coerceIn(0.1f, 0.9f)
                                        val normY = (offset.y / size.height).coerceIn(0.1f, 0.9f)
                                        viewModel.handleArrowTap(arrowId, normX, normY)
                                    }
                                }
                            }
                    ) {
                        val stepX = size.width / (activeLevel.gridWidth + 1f)
                        val stepY = size.height / (activeLevel.gridHeight + 1f)
                        val cellSize = minOf(stepX, stepY)
                        val arrowStrokeWidth = cellSize * 0.18f

                        // 2a. Background dot grid: Subtle semi-transparent light-gray dots
                        if (useBoardLines) {
                            for (gx in 0 until activeLevel.gridWidth) {
                                for (gy in 0 until activeLevel.gridHeight) {
                                    val ptX = (gx + 1) * stepX
                                    val ptY = (gy + 1) * stepY
                                    drawCircle(
                                        color = Color(0xFFCBD5E1).copy(alpha = 0.35f),
                                        radius = 2.dp.toPx(),
                                        center = Offset(ptX, ptY)
                                    )
                                }
                            }
                        }

                        // 2b. Obstacles
                        for (obs in activeLevel.obstacles) {
                            val obsX = (obs.x + 1) * stepX
                            val obsY = (obs.y + 1) * stepY
                            drawRect(
                                color = Color(0xFF64748B),
                                size = Size(stepX * 0.8f, stepY * 0.8f),
                                topLeft = Offset(obsX - stepX * 0.4f, obsY - stepY * 0.4f)
                            )
                        }

                        // 2c. Active Arrow Paths
                        for (arrow in activeArrows) {
                            if (arrow.pathPoints.isEmpty()) continue

                            val isBlocked = arrow.id == blockedArrowId
                            val strokeColor = if (isBlocked) {
                                Color(0xFFEF4444)
                            } else {
                                getArrowColor(arrow.color)
                            }

                            val shakeOffset = if (isBlocked) {
                                when (arrow.direction) {
                                    ArrowDirection.UP -> Offset(0f, -blockedShake)
                                    ArrowDirection.DOWN -> Offset(0f, blockedShake)
                                    ArrowDirection.LEFT -> Offset(-blockedShake, 0f)
                                    ArrowDirection.RIGHT -> Offset(blockedShake, 0f)
                                }
                            } else {
                                Offset.Zero
                            }

                            val rawPixelPoints = arrow.pathPoints.map {
                                Offset((it.x + 1) * stepX + shakeOffset.x, (it.y + 1) * stepY + shakeOffset.y)
                            }

                            val pixelPoints = if (arrow.isSliding && arrow.slideProgress > 0f) {
                                val segLengths = mutableListOf<Float>()
                                var pathLen = 0f
                                for (i in 0 until rawPixelPoints.size - 1) {
                                    val p1 = rawPixelPoints[i]
                                    val p2 = rawPixelPoints[i + 1]
                                    val len = hypot(p2.x - p1.x, p2.y - p1.y)
                                    segLengths.add(len)
                                    pathLen += len
                                }

                                val exitDist = size.width * 1.5f
                                val totalTravel = pathLen + exitDist
                                val progress = arrow.slideProgress.coerceIn(0f, 1f)
                                val headDist = progress * totalTravel
                                val tailDist = (progress * totalTravel - pathLen).coerceAtLeast(0f)

                                fun getPointAtDist(d: Float): Offset {
                                    if (d <= 0f) return rawPixelPoints.first()
                                    if (d <= pathLen) {
                                        var acc = 0f
                                        for (i in 0 until segLengths.size) {
                                            val len = segLengths[i]
                                            if (acc + len >= d) {
                                                val rem = d - acc
                                                val fraction = if (len > 0f) rem / len else 0f
                                                val p1 = rawPixelPoints[i]
                                                val p2 = rawPixelPoints[i + 1]
                                                return Offset(
                                                    p1.x + fraction * (p2.x - p1.x),
                                                    p1.y + fraction * (p2.y - p1.y)
                                                )
                                            }
                                            acc += len
                                        }
                                        return rawPixelPoints.last()
                                    } else {
                                        val lastPt = rawPixelPoints.last()
                                        val rayDist = d - pathLen
                                        return when (arrow.direction) {
                                            ArrowDirection.UP -> Offset(lastPt.x, lastPt.y - rayDist)
                                            ArrowDirection.DOWN -> Offset(lastPt.x, lastPt.y + rayDist)
                                            ArrowDirection.LEFT -> Offset(lastPt.x - rayDist, lastPt.y)
                                            ArrowDirection.RIGHT -> Offset(lastPt.x + rayDist, lastPt.y)
                                        }
                                    }
                                }

                                val subPoints = mutableListOf<Offset>()
                                subPoints.add(getPointAtDist(tailDist))
                                var curAcc = 0f
                                for (i in 0 until segLengths.size) {
                                    curAcc += segLengths[i]
                                    if (curAcc > tailDist && curAcc < headDist) {
                                        subPoints.add(rawPixelPoints[i + 1])
                                    }
                                }
                                subPoints.add(getPointAtDist(headDist))

                                if (subPoints.size < 2) {
                                    listOf(getPointAtDist(headDist), getPointAtDist(headDist))
                                } else {
                                    subPoints
                                }
                            } else {
                                rawPixelPoints
                            }

                            val paintPath = Path().apply {
                                moveTo(pixelPoints.first().x, pixelPoints.first().y)
                                for (i in 1 until pixelPoints.size) {
                                    lineTo(pixelPoints[i].x, pixelPoints[i].y)
                                }
                            }

                            // If Sliding, draw motion blur trail
                            if (arrow.isSliding) {
                                drawPath(
                                    path = paintPath,
                                    color = strokeColor.copy(alpha = 0.4f),
                                    style = Stroke(
                                        width = arrowStrokeWidth * 1.5f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }

                            // If Hinted, draw pulsing glow aura
                            if (arrow.isHinted) {
                                drawPath(
                                    path = paintPath,
                                    color = Color(0xFFFBBF24).copy(alpha = hintPulseAlpha),
                                    style = Stroke(
                                        width = arrowStrokeWidth + 10.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }

                            // Draw thick, sleek arrow lane path (18% grid cell size, rounded caps/joints)
                            drawPath(
                                path = paintPath,
                                color = strokeColor,
                                style = Stroke(
                                    width = arrowStrokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )

                            // Tail anchor rounded cap
                            val tailPt = pixelPoints.first()
                            drawCircle(
                                color = strokeColor,
                                radius = (arrowStrokeWidth * 0.5f),
                                center = tailPt
                            )

                            // Solid, filled geometric triangle arrowhead seamlessly attached to line path end
                            val headPt = pixelPoints.last()
                            val arrowHeadLen = cellSize * 0.40f
                            val arrowHeadBaseHalf = cellSize * 0.22f
                            val arrowPath = Path()

                            when (arrow.direction) {
                                ArrowDirection.UP -> {
                                    arrowPath.moveTo(headPt.x, headPt.y - arrowHeadLen)
                                    arrowPath.lineTo(headPt.x - arrowHeadBaseHalf, headPt.y)
                                    arrowPath.lineTo(headPt.x + arrowHeadBaseHalf, headPt.y)
                                }
                                ArrowDirection.DOWN -> {
                                    arrowPath.moveTo(headPt.x, headPt.y + arrowHeadLen)
                                    arrowPath.lineTo(headPt.x - arrowHeadBaseHalf, headPt.y)
                                    arrowPath.lineTo(headPt.x + arrowHeadBaseHalf, headPt.y)
                                }
                                ArrowDirection.LEFT -> {
                                    arrowPath.moveTo(headPt.x - arrowHeadLen, headPt.y)
                                    arrowPath.lineTo(headPt.x, headPt.y - arrowHeadBaseHalf)
                                    arrowPath.lineTo(headPt.x, headPt.y + arrowHeadBaseHalf)
                                }
                                ArrowDirection.RIGHT -> {
                                    arrowPath.moveTo(headPt.x + arrowHeadLen, headPt.y)
                                    arrowPath.lineTo(headPt.x, headPt.y - arrowHeadBaseHalf)
                                    arrowPath.lineTo(headPt.x, headPt.y + arrowHeadBaseHalf)
                                }
                            }
                            arrowPath.close()
                            drawPath(path = arrowPath, color = strokeColor)

                            // Smooth decorative apex center point
                            drawCircle(
                                color = Color.White.copy(alpha = 0.9f),
                                radius = 2.dp.toPx(),
                                center = headPt
                            )
                        }

                        // Blocked Collision Flash
                        blockedCollisionPoint?.let { colPt ->
                            val colPxX = (colPt.x + 1) * stepX
                            val colPxY = (colPt.y + 1) * stepY
                            drawCircle(
                                color = Color(0xFFEF4444).copy(alpha = 0.8f),
                                radius = 14.dp.toPx(),
                                center = Offset(colPxX, colPxY)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 6.dp.toPx(),
                                center = Offset(colPxX, colPxY)
                            )
                        }
                    }

                    // 2d. Hint Hand Pointer Overlay: Infinitely bouncing 👇 hand icon directly over head of free arrow
                    if (hintedArrow != null && hintedArrow.pathPoints.isNotEmpty() && !hintedArrow.isSliding) {
                        val head = hintedArrow.pathPoints.last()
                        val fracX = (head.x + 1f) / (activeLevel.gridWidth + 1f)
                        val fracY = (head.y + 1f) / (activeLevel.gridHeight + 1f)
                        val targetX = boardWidth * fracX
                        val targetY = boardHeight * fracY

                        val handOffsetY = when (hintedArrow.direction) {
                            ArrowDirection.DOWN -> (-42).dp + handBounce.dp
                            ArrowDirection.UP -> (-46).dp + handBounce.dp
                            else -> (-44).dp + handBounce.dp
                        }
                        val handOffsetX = 0.dp

                        Box(
                            modifier = Modifier
                                .offset(x = targetX - 30.dp + handOffsetX, y = targetY + handOffsetY)
                                .clickable { viewModel.handleArrowTap(hintedArrow.id, fracX, fracY) }
                                .testTag("hint_hand_pointer"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Surface(
                                    color = Color(0xFFFBBF24),
                                    shape = RoundedCornerShape(12.dp),
                                    shadowElevation = 8.dp,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                                ) {
                                    Text(
                                        text = "FREE TO EXIT",
                                        color = Color(0xFF0F172A),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "👇",
                                    fontSize = 32.sp
                                )
                            }
                        }
                    }

                    // 2e. Magnifier Glass 2x Lens Overlay
                    if (isMagnifierActive) {
                        var dragOffset by remember { mutableStateOf(Offset(0.5f, 0.5f)) }
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (boardWidth * dragOffset.x) - 55.dp,
                                    y = (boardHeight * dragOffset.y) - 55.dp
                                )
                                .size(110.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        val newX = (dragOffset.x + dragAmount.x / size.width).coerceIn(0.15f, 0.85f)
                                        val newY = (dragOffset.y + dragAmount.y / size.height).coerceIn(0.15f, 0.85f)
                                        dragOffset = Offset(newX, newY)
                                    }
                                }
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A).copy(alpha = 0.9f))
                                .border(3.dp, Color(0xFF38BDF8), CircleShape)
                                .testTag("magnifier_lens"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ZoomIn,
                                    contentDescription = "Zoom 2x",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "2X LENS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF38BDF8)
                                )
                                Text(
                                    text = "Drag to Inspect",
                                    fontSize = 8.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }

                    // 2f. Animated Flying Token Trajectories (Coins 🪙 and Red Emojis 😡)
                    for (token in flyingTokens) {
                        val p = token.progress
                        // Parabolic arc interpolation
                        val curX = (1 - p) * token.startX + p * token.endX
                        val arcHeight = 0.15f * sin(p * Math.PI.toFloat())
                        val curY = (1 - p) * token.startY + p * token.endY - arcHeight

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = boardWidth * curX - 25.dp,
                                    y = boardHeight * curY - 25.dp
                                )
                                .scale(1.0f + 0.3f * sin(p * Math.PI.toFloat()))
                                .testTag("flying_token_${token.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (token.isPositive) Color(0xFFFBBF24) else Color(0xFFEF4444),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
                                shadowElevation = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = token.emoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = token.text,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (token.isPositive) Color(0xFF0F172A) else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. In-Game Tool Buttons (Hint, Magnifier, Palette Switcher, Undo, Shuffle)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hint Button
                    HudButton(
                        modifier = Modifier.weight(1f),
                        title = "HINT",
                        icon = Icons.Default.Lightbulb,
                        color = Color(0xFFFBBF24),
                        onClick = { viewModel.triggerHint() }
                    )

                    // Magnifier Tool Button
                    HudButton(
                        modifier = Modifier.weight(1f),
                        title = if (isMagnifierActive) "ZOOM OFF" else "LENS 2X",
                        icon = Icons.Default.Search,
                        color = if (isMagnifierActive) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                        onClick = { viewModel.toggleMagnifier() }
                    )

                    // Palette Switcher Button
                    HudButton(
                        modifier = Modifier.weight(1.1f),
                        title = paletteTheme.displayName.take(8),
                        icon = Icons.Default.Palette,
                        color = Color(0xFFEC4899),
                        onClick = { viewModel.cyclePaletteTheme() }
                    )

                    // Undo Button
                    HudButton(
                        modifier = Modifier.weight(1f),
                        title = "UNDO",
                        icon = Icons.Default.Undo,
                        color = Color(0xFF38BDF8),
                        onClick = { viewModel.triggerUndo() }
                    )

                    // Reset / Restart Button
                    HudButton(
                        modifier = Modifier.weight(1f),
                        title = "RESTART",
                        icon = Icons.Default.Refresh,
                        color = Color(0xFF64748B),
                        onClick = { viewModel.resetLevel() }
                    )
                }
            }
        }

        // 4. Dynamic Level Complete Victory Screen
        if (gameState == GameViewModel.GameState.LEVEL_COMPLETE && lastReward != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0F1D).copy(alpha = 0.95f))
                    .testTag("level_complete_screen"),
                contentAlignment = Alignment.Center
            ) {
                // Animated Radiant Sunburst Background
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(sunburstRotation)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val rayRadius = size.width * 1.2f
                    val numRays = 16
                    val rayAngle = 360f / numRays

                    val themeRayColor = when (activeLevel.levelNumber % 5) {
                        0 -> Color(0xFF10B981) // Emerald
                        1 -> Color(0xFF8B5CF6) // Violet
                        2 -> Color(0xFFFBBF24) // Gold
                        3 -> Color(0xFFF43F5E) // Crimson
                        else -> Color(0xFF06B6D4) // Cyan
                    }

                    for (i in 0 until numRays step 2) {
                        val a1 = Math.toRadians((i * rayAngle).toDouble()).toFloat()
                        val a2 = Math.toRadians(((i + 1) * rayAngle).toDouble()).toFloat()

                        val p1 = Offset(center.x + rayRadius * cos(a1), center.y + rayRadius * sin(a1))
                        val p2 = Offset(center.x + rayRadius * cos(a2), center.y + rayRadius * sin(a2))

                        val path = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(p1.x, p1.y)
                            lineTo(p2.x, p2.y)
                            close()
                        }
                        drawPath(path = path, color = themeRayColor.copy(alpha = 0.12f))
                    }
                }

                // Victory Content Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        Brush.linearGradient(
                            listOf(Color(0xFFFBBF24), Color(0xFF38BDF8), Color(0xFF10B981))
                        )
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // "Train Your Brain" Banner (Bold Black text)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFBBF24),
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = "🧠 Train Your Brain",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // "Level X Completed!" in Dark Red (#8B0000)
                        Text(
                            text = "Level ${activeLevel.levelNumber} Completed!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF8B0000),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Star Rating (3 Stars)
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..3) {
                                Text(
                                    text = if (i <= starsEarned) "⭐" else "☆",
                                    fontSize = 32.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Performance & Score Badges
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Score Points", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                        Text("${goldenScore} pts 🪙", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFFBBF24))
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Time Spent", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                        Text("${completionTime}s ⏱️", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF38BDF8))
                                    }
                                }

                                if (speedBonus > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "⚡ Completed ${speedBonus}s before 1 min limit!",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Coins Reward Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Reward: +${lastReward!!.totalCoins} Coins", fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24), fontSize = 14.sp)
                            if (!lastReward!!.bonusCoinsClaimed) {
                                TextButton(
                                    onClick = { viewModel.watchLevelCompleteBonusAd() },
                                    modifier = Modifier.testTag("double_coins_ad_btn")
                                ) {
                                    Text("🎬 2X Bonus", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text("🎉 2X Added", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons: "Main" (Bold Black label) & "Next Stage"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // "Main" Level Select navigation button with bold black label
                            Button(
                                onClick = onBack,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("main_level_select_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Main", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }

                            // "Next Stage" Button
                            Button(
                                onClick = {
                                    val nextNum = activeLevel.levelNumber + 1
                                    val nextLvl = ArrowLevels.levels.find { it.levelNumber == nextNum }
                                    if (nextLvl != null) {
                                        viewModel.loadLevel(nextLvl)
                                    } else {
                                        onBack()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("next_stage_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = if (activeLevel.levelNumber < ArrowLevels.levels.size) "Next Stage ➔" else "Victory!",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Game Over Dialog (Out of Lives or Time's Up)
        if (gameState == GameViewModel.GameState.GAME_OVER) {
            AlertDialog(
                onDismissRequest = { /* Prevent dismiss */ },
                title = {
                    Text(
                        text = if (gameOverReason == GameViewModel.GameOverReason.OUT_OF_LIVES) "Out of Lives! 💔" else "Time's Up! ⏰",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (gameOverReason == GameViewModel.GameOverReason.OUT_OF_LIVES)
                                "You've collided into blocking arrows too many times."
                            else
                                "The 1-minute countdown timer reached zero.",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        // Continue with Hearts or Time
                        Button(
                            onClick = {
                                if (gameOverReason == GameViewModel.GameOverReason.OUT_OF_LIVES) {
                                    viewModel.continueWithHearts()
                                } else {
                                    viewModel.continueWithTime()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("continue_game_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (gameOverReason == GameViewModel.GameOverReason.OUT_OF_LIVES) "Continue (+3 Hearts ❤️)" else "Continue (+30s Time ⏱️)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.resetLevel() },
                        modifier = Modifier.testTag("retry_level_button")
                    ) {
                        Text("Retry Stage", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("exit_to_menu_button")
                    ) {
                        Text("Exit to Menu", color = Color(0xFF94A3B8))
                    }
                }
            )
        }

        // 6. Settings Dialog
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = {
                    Text("Game Settings", fontWeight = FontWeight.Bold, color = Color.White)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sound Effects", color = Color.White, fontWeight = FontWeight.Medium)
                            Switch(
                                checked = viewModel.isSoundEnabled,
                                onCheckedChange = { viewModel.isSoundEnabled = it }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Haptic Feedback", color = Color.White, fontWeight = FontWeight.Medium)
                            Switch(
                                checked = viewModel.isHapticsEnabled,
                                onCheckedChange = { viewModel.isHapticsEnabled = it }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Background Dot Grid", color = Color.White, fontWeight = FontWeight.Medium)
                            Switch(
                                checked = useBoardLines,
                                onCheckedChange = { useBoardLines = it }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettingsDialog = false }) {
                        Text("Done", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun HudButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("hud_button_${title.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}
