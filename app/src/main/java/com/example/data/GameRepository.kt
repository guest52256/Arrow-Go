package com.example.data

import android.content.Context
import android.util.Log
import com.example.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GameRepository(
    private val context: Context,
    private val gameDao: GameDao
) {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private var auth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    init {
        try {
            // Attempt to initialize Firebase. Will fail gracefully if google-services.json is missing or invalid.
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            Log.d("GameRepository", "Firebase Auth and Firestore initialized successfully")
        } catch (e: Exception) {
            Log.w("GameRepository", "Firebase initialization skipped or failed. Falling back to local-only mode: ${e.message}")
        }
    }

    private val _currentUserProfile = MutableStateFlow<UserProfile?>(null)
    val currentUserProfile: StateFlow<UserProfile?> = _currentUserProfile.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val _levelProgressMap = MutableStateFlow<Map<Int, LevelProgress>>(emptyMap())
    val levelProgressMap: StateFlow<Map<Int, LevelProgress>> = _levelProgressMap.asStateFlow()

    init {
        // Observe Room changes
        ioScope.launch {
            gameDao.getUserProfileFlow().collect { entity ->
                if (entity != null) {
                    _currentUserProfile.value = entity.toDomainModel()
                } else {
                    // Create default guest profile if database is completely empty
                    createDefaultGuestProfile()
                }
            }
        }

        ioScope.launch {
            gameDao.getTransactionsFlow().collect { list ->
                _transactions.value = list.map { it.toDomainModel() }
            }
        }

        ioScope.launch {
            gameDao.getLevelProgressListFlow().collect { list ->
                _levelProgressMap.value = list.associate { it.levelNumber to it.toDomainModel() }
            }
        }
    }

    private suspend fun createDefaultGuestProfile() {
        val guestUid = "guest_" + UUID.randomUUID().toString().take(8)
        val guestProfile = UserProfile(
            uid = guestUid,
            displayName = "Guest Player",
            email = null,
            photoUrl = null,
            isGuest = true,
            membershipPlan = MembershipType.FREE,
            currentLevel = 1,
            highestLevel = 1,
            wallets = UserWallet(gameCoins = 500, cashCoins = 0),
            referralCode = generateReferralCode()
        )
        saveProfileLocally(guestProfile)
        
        // Log transaction for starter coins
        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            type = TransactionType.BONUS,
            gameAmount = 500,
            cashAmount = 0,
            title = "Welcome Gift"
        )
        saveTransactionLocally(tx)
    }

    private fun generateReferralCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars.random() }.joinToString("")
    }

    private suspend fun saveProfileLocally(profile: UserProfile) {
        gameDao.insertUserProfile(UserProfileEntity.fromDomainModel(profile))
        _currentUserProfile.value = profile
    }

    private suspend fun saveTransactionLocally(tx: WalletTransaction) {
        gameDao.insertTransaction(WalletTransactionEntity.fromDomainModel(tx))
        // Firestore sync if online
        syncTransactionToFirestore(tx)
    }

    // Server Authoritative Economy (Simulated locally for speed/security, and synced to Firestore if signed in)
    suspend fun completeLevel(level: GameLevel) {
        val currentProfile = _currentUserProfile.value ?: return
        
        // Generate random reward based on Membership type
        val plan = currentProfile.membershipPlan
        val randomRange = when (plan) {
            MembershipType.FREE -> 100..200
            MembershipType.STARTER -> 150..300
            MembershipType.PRO -> 200..400
            MembershipType.ULTRA -> 300..500
        }
        val totalReward = randomRange.random()
        
        // Calculate splits based on cashable percent
        val cashablePercent = when (plan) {
            MembershipType.FREE -> 20
            MembershipType.STARTER -> 50
            MembershipType.PRO -> 70
            MembershipType.ULTRA -> 100
        }
        
        val cashCoins = (totalReward * cashablePercent) / 100
        val gameCoins = totalReward - cashCoins
        
        // Create transactional event
        val transactionId = "level_tx_" + UUID.randomUUID().toString().take(12)
        val levelTx = WalletTransaction(
            id = transactionId,
            type = TransactionType.LEVEL_REWARD,
            gameAmount = gameCoins,
            cashAmount = cashCoins,
            title = "Level ${level.levelNumber} Completed (${plan.name})"
        )
        
        // Update user state
        val nextLevel = level.levelNumber + 1
        val newHighest = maxOf(currentProfile.highestLevel, nextLevel)
        
        val updatedProfile = currentProfile.copy(
            currentLevel = nextLevel,
            highestLevel = newHighest,
            wallets = UserWallet(
                gameCoins = currentProfile.wallets.gameCoins + gameCoins,
                cashCoins = currentProfile.wallets.cashCoins + cashCoins
            )
        )
        
        saveProfileLocally(updatedProfile)
        saveTransactionLocally(levelTx)
        syncProfileToFirestore(updatedProfile)
    }

    suspend fun claimAdReward(adPlacement: String) {
        val currentProfile = _currentUserProfile.value ?: return
        val plan = currentProfile.membershipPlan
        
        // Calculate ad rewards based on plan
        val adRange = when (plan) {
            MembershipType.FREE -> 100..300
            MembershipType.STARTER -> 100..500
            MembershipType.PRO -> 300..1000
            MembershipType.ULTRA -> 1000..2000
        }
        
        val coinsReward = adRange.random()
        // Ad coins split: 50% game coins, 50% cash coins
        val cashCoins = coinsReward / 2
        val gameCoins = coinsReward - cashCoins
        
        val transactionId = "ad_tx_" + UUID.randomUUID().toString().take(12)
        val adTx = WalletTransaction(
            id = transactionId,
            type = TransactionType.AD_REWARD,
            gameAmount = gameCoins,
            cashAmount = cashCoins,
            title = "Watched Ad: $adPlacement"
        )
        
        val updatedProfile = currentProfile.copy(
            wallets = UserWallet(
                gameCoins = currentProfile.wallets.gameCoins + gameCoins,
                cashCoins = currentProfile.wallets.cashCoins + cashCoins
            )
        )
        
        saveProfileLocally(updatedProfile)
        saveTransactionLocally(adTx)
        syncProfileToFirestore(updatedProfile)
    }

    suspend fun redeemReferral(code: String): Result<String> {
        val currentProfile = _currentUserProfile.value ?: return Result.failure(Exception("Profile not loaded"))
        
        if (code.trim().equals(currentProfile.referralCode, ignoreCase = true)) {
            return Result.failure(Exception("Cannot redeem your own referral code"))
        }
        if (currentProfile.referredBy != null) {
            return Result.failure(Exception("You have already been referred by someone"))
        }
        
        val plan = currentProfile.membershipPlan
        val referralReward = when (plan) {
            MembershipType.FREE -> 500
            MembershipType.STARTER -> 700
            MembershipType.PRO -> 1000
            MembershipType.ULTRA -> 1300
        }
        
        // Split reward: 50% Cash, 50% Game
        val cashAmount = referralReward / 2
        val gameAmount = referralReward - cashAmount
        
        val txId = "ref_tx_" + UUID.randomUUID().toString().take(12)
        val refTx = WalletTransaction(
            id = txId,
            type = TransactionType.REFERRAL_REWARD,
            gameAmount = gameAmount,
            cashAmount = cashAmount,
            title = "Referral Bonus (Code: $code)"
        )
        
        val updatedProfile = currentProfile.copy(
            referredBy = code,
            wallets = UserWallet(
                gameCoins = currentProfile.wallets.gameCoins + gameAmount,
                cashCoins = currentProfile.wallets.cashCoins + cashAmount
            )
        )
        
        saveProfileLocally(updatedProfile)
        saveTransactionLocally(refTx)
        syncProfileToFirestore(updatedProfile)
        
        return Result.success("Referral successfully redeemed! Added $referralReward Coins.")
    }

    suspend fun buyMembership(plan: MembershipType): Boolean {
        val currentProfile = _currentUserProfile.value ?: return false
        
        // Upgrading simply replaces plan and gifts PKR bonus to both game/cash wallet
        val upgradeBonus = when (plan) {
            MembershipType.FREE -> 0
            MembershipType.STARTER -> 1000
            MembershipType.PRO -> 2500
            MembershipType.ULTRA -> 5000
        }
        
        val cashBonus = upgradeBonus / 2
        val gameBonus = upgradeBonus - cashBonus
        
        val txId = "member_tx_" + UUID.randomUUID().toString().take(12)
        val tx = WalletTransaction(
            id = txId,
            type = TransactionType.BONUS,
            gameAmount = gameBonus,
            cashAmount = cashBonus,
            title = "Upgraded to ${plan.name} Membership"
        )
        
        val updatedProfile = currentProfile.copy(
            membershipPlan = plan,
            wallets = UserWallet(
                gameCoins = currentProfile.wallets.gameCoins + gameBonus,
                cashCoins = currentProfile.wallets.cashCoins + cashBonus
            )
        )
        
        saveProfileLocally(updatedProfile)
        saveTransactionLocally(tx)
        syncProfileToFirestore(updatedProfile)
        return true
    }

    suspend fun requestWithdrawal(amount: Int): Result<String> {
        val currentProfile = _currentUserProfile.value ?: return Result.failure(Exception("Profile not loaded"))
        
        if (amount < 500) {
            return Result.failure(Exception("Minimum withdrawal limit is 500 Coins"))
        }
        if (currentProfile.wallets.cashCoins < amount) {
            return Result.failure(Exception("Insufficient Cash Wallet balance"))
        }
        
        val txId = "with_tx_" + UUID.randomUUID().toString().take(12)
        val withdrawalTx = WalletTransaction(
            id = txId,
            type = TransactionType.WITHDRAWAL,
            gameAmount = 0,
            cashAmount = -amount,
            title = "Cash Out Requested"
        )
        
        val updatedProfile = currentProfile.copy(
            wallets = UserWallet(
                gameCoins = currentProfile.wallets.gameCoins,
                cashCoins = currentProfile.wallets.cashCoins - amount
            )
        )
        
        saveProfileLocally(updatedProfile)
        saveTransactionLocally(withdrawalTx)
        syncProfileToFirestore(updatedProfile)
        
        return Result.success("Withdrawal of $amount Coins requested successfully!")
    }

    suspend fun resetGameProgress() {
        val currentProfile = _currentUserProfile.value ?: return
        val updatedProfile = currentProfile.copy(
            currentLevel = 1,
            wallets = UserWallet(gameCoins = 500, cashCoins = 0)
        )
        saveProfileLocally(updatedProfile)
        gameDao.clearTransactions()
        
        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            type = TransactionType.BONUS,
            gameAmount = 500,
            cashAmount = 0,
            title = "Game Reset Welcome Gift"
        )
        saveTransactionLocally(tx)
        syncProfileToFirestore(updatedProfile)
    }

    suspend fun linkGoogleAccount(googleUserEmail: String, googleDisplayName: String, googlePhotoUrl: String?): Boolean {
        val currentProfile = _currentUserProfile.value ?: return false
        val updatedProfile = currentProfile.copy(
            displayName = googleDisplayName,
            email = googleUserEmail,
            photoUrl = googlePhotoUrl,
            isGuest = false
        )
        saveProfileLocally(updatedProfile)
        syncProfileToFirestore(updatedProfile)
        return true
    }

    suspend fun logOutGoogle() {
        val currentProfile = _currentUserProfile.value ?: return
        val updatedProfile = currentProfile.copy(
            displayName = "Guest Player",
            email = null,
            photoUrl = null,
            isGuest = true
        )
        saveProfileLocally(updatedProfile)
    }

    // Firestore Integration helpers that handle failure gracefully
    private fun syncProfileToFirestore(profile: UserProfile) {
        val db = firestore ?: return
        ioScope.launch {
            try {
                db.collection("users").document(profile.uid).set(UserProfileEntity.fromDomainModel(profile))
                    .addOnSuccessListener {
                        Log.d("GameRepository", "Successfully synced user profile to Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.w("GameRepository", "Failed to sync profile to Firestore: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.w("GameRepository", "Firestore write exception skipped: ${e.message}")
            }
        }
    }

    private fun syncTransactionToFirestore(tx: WalletTransaction) {
        val db = firestore ?: return
        ioScope.launch {
            try {
                db.collection("walletTransactions").document(tx.id).set(WalletTransactionEntity.fromDomainModel(tx))
                    .addOnSuccessListener {
                        Log.d("GameRepository", "Successfully synced transaction to Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.w("GameRepository", "Failed to sync transaction to Firestore: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.w("GameRepository", "Firestore tx write exception skipped: ${e.message}")
            }
        }
    }

    suspend fun recordLevelCompletion(levelNumber: Int, stars: Int, timeSeconds: Int, score: Int) {
        val existing = _levelProgressMap.value[levelNumber]
        val bestStars = maxOf(existing?.stars ?: 0, stars)
        val bestTime = if ((existing?.bestTimeSeconds ?: 0) == 0) timeSeconds else minOf(existing!!.bestTimeSeconds, timeSeconds)
        val bestScore = maxOf(existing?.highScore ?: 0, score)

        val progress = LevelProgress(
            levelNumber = levelNumber,
            isUnlocked = true,
            stars = bestStars,
            bestTimeSeconds = bestTime,
            highScore = bestScore
        )
        gameDao.insertLevelProgress(LevelProgressEntity.fromDomainModel(progress))

        // Also unlock the next level
        val nextLevelNumber = levelNumber + 1
        val nextExisting = _levelProgressMap.value[nextLevelNumber]
        if (nextExisting == null || !nextExisting.isUnlocked) {
            val nextProgress = LevelProgress(
                levelNumber = nextLevelNumber,
                isUnlocked = true,
                stars = nextExisting?.stars ?: 0,
                bestTimeSeconds = nextExisting?.bestTimeSeconds ?: 0,
                highScore = nextExisting?.highScore ?: 0
            )
            gameDao.insertLevelProgress(LevelProgressEntity.fromDomainModel(nextProgress))
        }
    }
}
