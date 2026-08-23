package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import com.example.data.ArrowLevels
import com.example.model.*
import com.example.viewmodel.GameViewModel
import kotlin.math.hypot

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
    val gameState by viewModel.gameState.collectAsState()
    val lastReward by viewModel.lastClearedReward.collectAsState()

    // Preferences for line width/colors (mocked/integrated from state)
    var useColorStrokes by remember { mutableStateOf(true) }
    var strokeThickness by remember { mutableStateOf(8.dp) }
    var useBoardLines by remember { mutableStateOf(true) }

    // Floating Hint Glow pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "hint_pulse")
    val hintPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ARROW GO",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = activeLevel.name,
                            fontSize = 12.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetLevel() }, modifier = Modifier.testTag("reload_button")) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Stage", tint = Color.White)
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Gameplay Status Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hearts Card
                    StatusCard(
                        modifier = Modifier.weight(1f),
                        title = "HEARTS",
                        value = "❤️ $hearts",
                        icon = Icons.Default.Favorite,
                        iconColor = Color(0xFFEF4444)
                    )

                    // Moves Card
                    StatusCard(
                        modifier = Modifier.weight(1f),
                        title = "MOVES",
                        value = "$moves",
                        icon = Icons.Default.DirectionsRun,
                        iconColor = Color(0xFF38BDF8)
                    )

                    // Remaining Arrows Card
                    StatusCard(
                        modifier = Modifier.weight(1.2f),
                        title = "REMAINING",
                        value = "$remainingCount / $totalCount",
                        icon = Icons.Default.GridOn,
                        iconColor = Color(0xFF10B981)
                    )
                }

                // Interactive Winding-Path Puzzle Board Canvas Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(24.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val density = LocalDensity.current
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(activeArrows) {
                                detectTapGestures { offset ->
                                    // Calculate cell grid scaling
                                    val stepX = size.width / 13f
                                    val stepY = size.height / 13f

                                    var clickedArrowId: String? = null
                                    var closestDist = Float.MAX_VALUE

                                    // Find clicked arrow winding segment
                                    for (arrow in activeArrows) {
                                        val points = viewModel.getOccupiedPointsForArrow(arrow)
                                        for (pt in points) {
                                            val ptPxX = (pt.x + 1) * stepX
                                            val ptPxY = (pt.y + 1) * stepY
                                            val dist = hypot(offset.x - ptPxX, offset.y - ptPxY)
                                            // 24.dp margin of error
                                            with(density) {
                                                if (dist < 24.dp.toPx() && dist < closestDist) {
                                                    closestDist = dist
                                                    clickedArrowId = arrow.id
                                                }
                                            }
                                        }
                                    }

                                    clickedArrowId?.let { arrowId ->
                                        viewModel.handleArrowTap(arrowId)
                                    }
                                }
                            }
                    ) {
                        val stepX = size.width / 13f
                        val stepY = size.height / 13f

                        // 1. Draw sub-transparent background dot coordinates layer
                        if (useBoardLines) {
                            for (gx in 0 until activeLevel.gridWidth) {
                                for (gy in 0 until activeLevel.gridHeight) {
                                    val ptX = (gx + 1) * stepX
                                    val ptY = (gy + 1) * stepY
                                    drawCircle(
                                        color = Color(0xFF475569).copy(alpha = 0.5f),
                                        radius = 2.dp.toPx(),
                                        center = Offset(ptX, ptY)
                                    )
                                }
                            }
                        }

                        // 2. Draw obstacles
                        for (obs in activeLevel.obstacles) {
                            val obsX = (obs.x + 1) * stepX
                            val obsY = (obs.y + 1) * stepY
                            drawRect(
                                color = Color(0xFF64748B),
                                size = Size(stepX * 0.8f, stepY * 0.8f),
                                topLeft = Offset(obsX - stepX * 0.4f, obsY - stepY * 0.4f)
                            )
                        }

                        // 3. Draw active arrow paths with anchors, heads, and glowing hints
                        for (arrow in activeArrows) {
                            if (arrow.pathPoints.isEmpty()) continue

                            val strokeColor = if (useColorStrokes) {
                                when (arrow.color) {
                                    "Purple" -> Color(0xFFA855F7)
                                    "Green" -> Color(0xFF10B981)
                                    "Red" -> Color(0xFFEF4444)
                                    "Blue" -> Color(0xFF38BDF8)
                                    "Orange" -> Color(0xFFF59E0B)
                                    "Yellow" -> Color(0xFFFBBF24)
                                    "Cyan" -> Color(0xFF06B6D4)
                                    "Pink" -> Color(0xFFEC4899)
                                    else -> Color(0xFF38BDF8)
                                }
                            } else {
                                Color.White
                            }

                            // Calculate path-shortening and tail-erasing animation along winding segments and exit ray
                            val rawPixelPoints = arrow.pathPoints.map {
                                Offset((it.x + 1) * stepX, (it.y + 1) * stepY)
                            }

                            val pixelPoints = if (arrow.isSliding && arrow.slideProgress > 0f) {
                                // Compute winding segment lengths
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

                                // Helper to get point at distance d along (winding path + straight exit ray)
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
                                        // Straight exit ray from last point
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

                                // Collect all original vertices between tailDist and headDist plus exact cut points
                                val subPoints = mutableListOf<Offset>()
                                val actualTailDist = tailDist.coerceAtMost(pathLen)
                                val actualHeadDist = headDist.coerceAtMost(pathLen)

                                subPoints.add(getPointAtDist(tailDist))

                                // Add intermediate path points that fall strictly between tailDist and headDist
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

                            // Build compose graphics path
                            val paintPath = Path().apply {
                                moveTo(pixelPoints.first().x, pixelPoints.first().y)
                                for (i in 1 until pixelPoints.size) {
                                    lineTo(pixelPoints[i].x, pixelPoints[i].y)
                                }
                            }

                            // 3a. If Hinted, draw pulsing glow aura first
                            if (arrow.isHinted) {
                                drawPath(
                                    path = paintPath,
                                    color = Color.White.copy(alpha = hintPulseAlpha),
                                    style = Stroke(
                                        width = strokeThickness.toPx() + 8.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }

                            // 3b. Draw solid color lane path
                            drawPath(
                                path = paintPath,
                                color = strokeColor,
                                style = Stroke(
                                    width = strokeThickness.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )

                            // 3c. Draw distinct anchor circle at the tail (P0)
                            val tailPt = pixelPoints.first()
                            drawCircle(
                                color = strokeColor,
                                radius = (strokeThickness.toPx() * 0.75f),
                                center = tailPt
                            )

                            // 3d. Draw sharp, filled arrowhead at exit tip pointing in direction
                            val headPt = pixelPoints.last()
                            val arrowPath = Path()
                            val arrowSize = strokeThickness.toPx() * 1.5f

                            when (arrow.direction) {
                                ArrowDirection.UP -> {
                                    arrowPath.moveTo(headPt.x, headPt.y - arrowSize)
                                    arrowPath.lineTo(headPt.x - arrowSize * 0.7f, headPt.y)
                                    arrowPath.lineTo(headPt.x + arrowSize * 0.7f, headPt.y)
                                }
                                ArrowDirection.DOWN -> {
                                    arrowPath.moveTo(headPt.x, headPt.y + arrowSize)
                                    arrowPath.lineTo(headPt.x - arrowSize * 0.7f, headPt.y)
                                    arrowPath.lineTo(headPt.x + arrowSize * 0.7f, headPt.y)
                                }
                                ArrowDirection.LEFT -> {
                                    arrowPath.moveTo(headPt.x - arrowSize, headPt.y)
                                    arrowPath.lineTo(headPt.x, headPt.y - arrowSize * 0.7f)
                                    arrowPath.lineTo(headPt.x, headPt.y + arrowSize * 0.7f)
                                }
                                ArrowDirection.RIGHT -> {
                                    arrowPath.moveTo(headPt.x + arrowSize, headPt.y)
                                    arrowPath.lineTo(headPt.x, headPt.y - arrowSize * 0.7f)
                                    arrowPath.lineTo(headPt.x, headPt.y + arrowSize * 0.7f)
                                }
                            }
                            arrowPath.close()
                            drawPath(path = arrowPath, color = strokeColor)

                            // 3e. Finish each head tip with a bright white core dot inside
                            drawCircle(
                                color = Color.White,
                                radius = 2.5.dp.toPx(),
                                center = headPt
                            )
                        }
                    }
                }

                // Inline visual preference adjustment sliders (Display, stroke, and grid dots)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Color vs Mono stroke toggle button
                        TextButton(onClick = { useColorStrokes = !useColorStrokes }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (useColorStrokes) "COLOR" else "MONO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Board lines grid toggle button
                        TextButton(onClick = { useBoardLines = !useBoardLines }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.GridGoldenratio, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (useBoardLines) "GRID ON" else "GRID OFF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Thick vs thin stroke toggler
                        TextButton(onClick = {
                            strokeThickness = if (strokeThickness == 8.dp) 4.dp else if (strokeThickness == 4.dp) 11.dp else 8.dp
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LineStyle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (strokeThickness == 4.dp) "THIN" else if (strokeThickness == 11.dp) "BOLD" else "NORMAL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Interactive Control HUD Panel: Reset, Hint, Undo, Shuffle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Reset Button
                    HudButton(
                        modifier = Modifier.weight(1f),
                        title = "RESET",
                        icon = Icons.Default.Cached,
                        color = Color(0xFF64748B),
                        onClick = { viewModel.resetLevel() }
                    )

                    // Hint Button
                    HudButton(
                        modifier = Modifier.weight(1.2f),
                        title = "HINT",
                        icon = Icons.Default.Lightbulb,
                        color = Color(0xFFFBBF24),
                        onClick = { viewModel.triggerHint() }
                    )

                    // Undo Button
                    HudButton(
                        modifier = Modifier.weight(1.2f),
                        title = "UNDO",
                        icon = Icons.Default.Undo,
                        color = Color(0xFF38BDF8),
                        onClick = { viewModel.triggerUndo() }
                    )

                    // Shuffle Button
                    HudButton(
                        modifier = Modifier.weight(1.2f),
                        title = "SHUFFLE",
                        icon = Icons.Default.Shuffle,
                        color = Color(0xFFA855F7),
                        onClick = { viewModel.triggerShuffle() }
                    )
                }
            }
        }

        // Level Complete Dialog
        if (gameState == GameViewModel.GameState.LEVEL_COMPLETE && lastReward != null) {
            AlertDialog(
                onDismissRequest = { /* Prevent dismiss on outside tap */ },
                title = {
                    Text(
                        text = "STAGE COMPLETED!",
                        color = Color(0xFF10B981),
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
                            text = "Amazing solve! Paths cleared safely.",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "TOTAL LEVEL REWARD",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "+${lastReward!!.totalCoins} Coins",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFBBF24),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Game Wallet", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        Text("+${lastReward!!.gameCoins}", fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Cash Wallet", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        Text("+${lastReward!!.cashCoins}", fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                    }
                                }
                            }
                        }

                        if (!lastReward!!.bonusCoinsClaimed) {
                            Spacer(modifier = Modifier.height(16.dp))
                            // Double coins ad offer
                            Button(
                                onClick = { viewModel.watchLevelCompleteBonusAd() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("watch_bonus_ad_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.OndemandVideo, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Watch Ad for Double Rewards!", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "🎉 Double Reward Bonus Added!",
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Find and load next level
                            val nextNum = activeLevel.levelNumber + 1
                            val nextLvl = ArrowLevels.levels.find { it.levelNumber == nextNum }
                            if (nextLvl != null) {
                                viewModel.loadLevel(nextLvl)
                            } else {
                                // Campaign completed! Return back
                                onBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                        modifier = Modifier.testTag("next_stage_button")
                    ) {
                        Text(if (activeLevel.levelNumber < ArrowLevels.levels.size) "Next Stage" else "Campaign Completed!")
                    }
                }
            )
        }

        // Game Over Dialog
        if (gameState == GameViewModel.GameState.GAME_OVER) {
            AlertDialog(
                onDismissRequest = { /* Prevent dismiss */ },
                title = {
                    Text(
                        text = "GAME OVER",
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
                            text = "You've run out of lives / hearts. Watch an ad to fully revive and keep your progress on this stage!",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = { viewModel.triggerReviveWithAd() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("watch_revive_ad_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LiveTv, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Watch Ad to Revive", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.resetLevel() },
                        modifier = Modifier.testTag("retry_level_button")
                    ) {
                        Text("Retry Stage", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
fun StatusCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = value,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun HudButton(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("${title.lowercase()}_hud_button"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
