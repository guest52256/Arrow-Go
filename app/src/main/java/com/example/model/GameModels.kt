package com.example.model

import androidx.compose.ui.graphics.Color

enum class ArrowDirection {
    UP, DOWN, LEFT, RIGHT
}

data class Point(val x: Int, val y: Int)

data class Arrow(
    val id: String,
    val color: String, // String representation for serializing/storing (e.g., "Purple", "Green", "Red", "Blue", "Orange", "Yellow", "Cyan", "Pink")
    val direction: ArrowDirection,
    val pathPoints: List<Point>, // Winding path coordinates from tail to head
    val isHinted: Boolean = false,
    val isSliding: Boolean = false,
    val slideProgress: Float = 0f // 0f to 1f for slide animation
)

data class Obstacle(
    val x: Int,
    val y: Int,
    val colorName: String = "Gray"
)

enum class Difficulty {
    BEGINNER, EASY, MEDIUM, HARD, EXPERT, EXTREME
}

data class GameLevel(
    val id: String,
    val levelNumber: Int,
    val name: String,
    val difficulty: Difficulty,
    val gridWidth: Int = 12,
    val gridHeight: Int = 12,
    val arrows: List<Arrow>,
    val obstacles: List<Obstacle> = emptyList(),
    val initialHearts: Int = 3,
    val moveLimit: Int? = null, // null means unlimited
    val rewardMin: Int = 100,
    val rewardMax: Int = 200,
    val backgroundColor: String = "White",
    val lineColor: String = "Default"
)

enum class MembershipType {
    FREE, STARTER, PRO, ULTRA
}

data class UserWallet(
    val gameCoins: Int = 500,
    val cashCoins: Int = 0
)

data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String?,
    val photoUrl: String?,
    val isGuest: Boolean,
    val membershipPlan: MembershipType = MembershipType.FREE,
    val currentLevel: Int = 1,
    val highestLevel: Int = 1,
    val wallets: UserWallet = UserWallet(),
    val referralCode: String = "",
    val referredBy: String? = null
)

enum class TransactionType {
    LEVEL_REWARD, AD_REWARD, REFERRAL_REWARD, GAME_PURCHASE, WITHDRAWAL, BONUS
}

data class WalletTransaction(
    val id: String,
    val type: TransactionType,
    val gameAmount: Int,
    val cashAmount: Int,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MembershipPlan(
    val type: MembershipType,
    val title: String,
    val price: String,
    val levelRewardRange: String,
    val cashablePercent: Int,
    val adEarningRange: String,
    val referralReward: Int,
    val dailyHintLimit: String,
    val dailyUndoLimit: String,
    val dailyReviveLimit: String,
    val dailyShuffleLimit: String
)
