package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
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
import com.example.model.MembershipPlan
import com.example.model.MembershipType
import com.example.model.UserProfile

val plansList = listOf(
    MembershipPlan(
        type = MembershipType.FREE,
        title = "FREE",
        price = "PKR 0",
        levelRewardRange = "100–200",
        cashablePercent = 20,
        adEarningRange = "100–300",
        referralReward = 500,
        dailyHintLimit = "1 / day",
        dailyUndoLimit = "1 / day",
        dailyReviveLimit = "1 / day",
        dailyShuffleLimit = "1 / day"
    ),
    MembershipPlan(
        type = MembershipType.STARTER,
        title = "STARTER",
        price = "PKR 299/mo",
        levelRewardRange = "150–300",
        cashablePercent = 50,
        adEarningRange = "100–500",
        referralReward = 700,
        dailyHintLimit = "2 / day",
        dailyUndoLimit = "3 / day",
        dailyReviveLimit = "3 / day",
        dailyShuffleLimit = "3 / day"
    ),
    MembershipPlan(
        type = MembershipType.PRO,
        title = "PRO",
        price = "PKR 599/mo",
        levelRewardRange = "200–400",
        cashablePercent = 70,
        adEarningRange = "300–1,000",
        referralReward = 1000,
        dailyHintLimit = "5 / day",
        dailyUndoLimit = "10 / day",
        dailyReviveLimit = "5 / day",
        dailyShuffleLimit = "5 / day"
    ),
    MembershipPlan(
        type = MembershipType.ULTRA,
        title = "ULTRA",
        price = "PKR 999/mo",
        levelRewardRange = "300–500",
        cashablePercent = 100,
        adEarningRange = "1,000–2,000",
        referralReward = 1300,
        dailyHintLimit = "Unlimited*",
        dailyUndoLimit = "Unlimited*",
        dailyReviveLimit = "Unlimited*",
        dailyShuffleLimit = "5 / day"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembershipScreen(
    userProfile: UserProfile?,
    onPurchaseMembership: (MembershipType) -> Unit,
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
                        text = "UPGRADE MEMBERSHIP",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
            ) {
                item {
                    Text(
                        text = "Choose a membership plan to increase your level earnings, unlock cash withdrawal ratio, and get higher daily hints, undos, and revives quotas.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(plansList) { plan ->
                    val isActive = userProfile?.membershipPlan == plan.type
                    PlanItemCard(
                        plan = plan,
                        isActive = isActive,
                        onUpgrade = { onPurchaseMembership(plan.type) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlanItemCard(
    plan: MembershipPlan,
    isActive: Boolean,
    onUpgrade: () -> Unit
) {
    val accentColor = when (plan.type) {
        MembershipType.ULTRA -> Color(0xFFF59E0B)
        MembershipType.PRO -> Color(0xFFA855F7)
        MembershipType.STARTER -> Color(0xFF06B6D4)
        MembershipType.FREE -> Color(0xFF64748B)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFF1E293B) else Color(0xFF1E293B).copy(alpha = 0.5f)
        ),
        border = if (isActive) BorderStroke(2.dp, accentColor) else null
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = plan.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = plan.price,
                    color = accentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Perks/Benefits checklist
            PerkRow(label = "Level Coin Rewards", value = plan.levelRewardRange)
            PerkRow(label = "Cashable Percentage", value = "${plan.cashablePercent}%")
            PerkRow(label = "Ad Watching Earnings", value = "${plan.adEarningRange} Coins")
            PerkRow(label = "Referral Reward Bonus", value = "${plan.referralReward} Coins")
            PerkRow(label = "Daily Hints / Undos", value = "${plan.dailyHintLimit} / ${plan.dailyUndoLimit}")
            PerkRow(label = "Daily Revives / Shuffles", value = "${plan.dailyReviveLimit} / ${plan.dailyShuffleLimit}")

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button
            if (isActive) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Active",
                            tint = accentColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "CURRENT ACTIVE PLAN", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = onUpgrade,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "SUBSCRIBE NOW", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun PerkRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
