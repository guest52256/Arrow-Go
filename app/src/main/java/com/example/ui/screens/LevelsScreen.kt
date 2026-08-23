package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ArrowLevels
import com.example.model.Difficulty
import com.example.model.GameLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelsScreen(
    highestLevelUnlocked: Int,
    onLevelSelected: (GameLevel) -> Unit,
    onBack: () -> Unit
) {
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CAMPAIGN STAGES",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
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
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(ArrowLevels.levels) { level ->
                    val isUnlocked = level.levelNumber <= highestLevelUnlocked
                    LevelGridItem(
                        level = level,
                        isUnlocked = isUnlocked,
                        onClick = {
                            if (isUnlocked) {
                                onLevelSelected(level)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LevelGridItem(
    level: GameLevel,
    isUnlocked: Boolean,
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
            .height(170.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = isUnlocked, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFF1E293B) else Color(0xFF1E293B).copy(alpha = 0.4f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header of Card: Level index and Unlocked State
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
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Title
                Text(
                    text = level.name,
                    color = if (isUnlocked) Color.White else Color(0xFF94A3B8),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Subtitle: grid properties and counts
                Column {
                    Text(
                        text = "${level.gridWidth}x${level.gridHeight} • ${level.arrows.size} arrows",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Difficulty Tag Badge
                    Surface(
                        color = if (isUnlocked) difficultyColor.copy(alpha = 0.15f) else Color(0xFF334155),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = level.difficulty.name,
                            color = if (isUnlocked) difficultyColor else Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
