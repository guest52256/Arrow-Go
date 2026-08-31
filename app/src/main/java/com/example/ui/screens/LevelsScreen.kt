package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ArrowLevels
import com.example.model.Difficulty
import com.example.model.GameLevel
import com.example.model.LevelProgress
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelsScreen(
    highestLevelUnlocked: Int,
    levelProgressMap: Map<Int, LevelProgress> = emptyMap(),
    onLevelSelected: (GameLevel) -> Unit,
    onBack: () -> Unit
) {
    var loadingLevel by remember { mutableStateOf<GameLevel?>(null) }

    LaunchedEffect(loadingLevel) {
        if (loadingLevel != null) {
            delay(500) // Brief smooth loading screen transition
            onLevelSelected(loadingLevel!!)
            loadingLevel = null
        }
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CAMPAIGN STAGES",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "25 Handcrafted Brain Puzzles",
                            fontSize = 12.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0F172A),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(ArrowLevels.levels) { level ->
                    val progress = levelProgressMap[level.levelNumber]
                    val isUnlocked = level.levelNumber <= highestLevelUnlocked || (progress?.isUnlocked == true)
                    val stars = progress?.stars ?: 0

                    LevelGridCard(
                        level = level,
                        isUnlocked = isUnlocked,
                        stars = stars,
                        bestTime = progress?.bestTimeSeconds ?: 0,
                        highScore = progress?.highScore ?: 0,
                        onClick = {
                            if (isUnlocked) {
                                loadingLevel = level
                            }
                        }
                    )
                }
            }

            // Dedicated Level Loading Screen Overlay
            if (loadingLevel != null) {
                val lvl = loadingLevel!!
                val lvlColor = when (lvl.difficulty) {
                    Difficulty.BEGINNER -> Color(0xFF10B981)
                    Difficulty.EASY -> Color(0xFF38BDF8)
                    Difficulty.MEDIUM -> Color(0xFFF59E0B)
                    Difficulty.HARD -> Color(0xFFEF4444)
                    Difficulty.EXPERT -> Color(0xFFA855F7)
                    Difficulty.EXTREME -> Color(0xFFEC4899)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0A0F1D).copy(alpha = 0.95f))
                        .testTag("level_loading_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = lvlColor,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Entering Stage ${lvl.levelNumber}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = lvl.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = lvlColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${lvl.gridWidth}x${lvl.gridHeight} Grid • ${lvl.arrows.size} Arrows",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LevelGridCard(
    level: GameLevel,
    isUnlocked: Boolean,
    stars: Int,
    bestTime: Int,
    highScore: Int,
    onClick: () -> Unit
) {
    val difficultyColor = when (level.difficulty) {
        Difficulty.BEGINNER -> Color(0xFF10B981)
        Difficulty.EASY -> Color(0xFF38BDF8)
        Difficulty.MEDIUM -> Color(0xFFF59E0B)
        Difficulty.HARD -> Color(0xFFEF4444)
        Difficulty.EXPERT -> Color(0xFFA855F7)
        Difficulty.EXTREME -> Color(0xFFEC4899)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = isUnlocked, onClick = onClick)
            .testTag("level_card_${level.levelNumber}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFF1E293B) else Color(0xFF1E293B).copy(alpha = 0.45f)
        ),
        border = if (isUnlocked && stars > 0) {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24).copy(alpha = 0.6f))
        } else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header: Level Number & Lock/Unlock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STAGE ${level.levelNumber}",
                        color = if (isUnlocked) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = if (isUnlocked) "Unlocked" else "Locked",
                        tint = if (isUnlocked) Color(0xFF38BDF8) else Color(0xFF64748B),
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Stage Name
                Text(
                    text = level.name,
                    color = if (isUnlocked) Color.White else Color(0xFF94A3B8),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )

                // Grid Size and Arrows Count
                Text(
                    text = "${level.gridWidth}x${level.gridHeight} • ${level.arrows.size} arrows",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Stars row or difficulty pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isUnlocked && stars > 0) {
                        Row {
                            for (i in 1..3) {
                                Text(
                                    text = if (i <= stars) "⭐" else "☆",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = if (isUnlocked) difficultyColor.copy(alpha = 0.15f) else Color(0xFF334155),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = level.difficulty.name,
                                color = if (isUnlocked) difficultyColor else Color(0xFF64748B),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (bestTime > 0) {
                        Text(
                            text = "${bestTime}s ⏱️",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }
        }
    }
}
