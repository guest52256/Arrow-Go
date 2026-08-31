package com.example.data

import androidx.room.*
import com.example.model.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val uid: String,
    val displayName: String,
    val email: String?,
    val photoUrl: String?,
    val isGuest: Boolean,
    val membershipPlan: String, // MembershipType enum as String
    val currentLevel: Int,
    val highestLevel: Int,
    val gameCoins: Int,
    val cashCoins: Int,
    val referralCode: String,
    val referredBy: String?
) {
    fun toDomainModel(): UserProfile {
        return UserProfile(
            uid = uid,
            displayName = displayName,
            email = email,
            photoUrl = photoUrl,
            isGuest = isGuest,
            membershipPlan = MembershipType.valueOf(membershipPlan),
            currentLevel = currentLevel,
            highestLevel = highestLevel,
            wallets = UserWallet(gameCoins = gameCoins, cashCoins = cashCoins),
            referralCode = referralCode,
            referredBy = referredBy
        )
    }

    companion object {
        fun fromDomainModel(profile: UserProfile): UserProfileEntity {
            return UserProfileEntity(
                uid = profile.uid,
                displayName = profile.displayName,
                email = profile.email,
                photoUrl = profile.photoUrl,
                isGuest = profile.isGuest,
                membershipPlan = profile.membershipPlan.name,
                currentLevel = profile.currentLevel,
                highestLevel = profile.highestLevel,
                gameCoins = profile.wallets.gameCoins,
                cashCoins = profile.wallets.cashCoins,
                referralCode = profile.referralCode,
                referredBy = profile.referredBy
            )
        }
    }
}

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey val id: String,
    val type: String, // TransactionType enum as String
    val gameAmount: Int,
    val cashAmount: Int,
    val title: String,
    val timestamp: Long
) {
    fun toDomainModel(): WalletTransaction {
        return WalletTransaction(
            id = id,
            type = TransactionType.valueOf(type),
            gameAmount = gameAmount,
            cashAmount = cashAmount,
            title = title,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDomainModel(tx: WalletTransaction): WalletTransactionEntity {
            return WalletTransactionEntity(
                id = tx.id,
                type = tx.type.name,
                gameAmount = tx.gameAmount,
                cashAmount = tx.cashAmount,
                title = tx.title,
                timestamp = tx.timestamp
            )
        }
    }
}

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelNumber: Int,
    val isUnlocked: Boolean,
    val stars: Int,
    val bestTimeSeconds: Int,
    val highScore: Int
) {
    fun toDomainModel(): LevelProgress {
        return LevelProgress(
            levelNumber = levelNumber,
            isUnlocked = isUnlocked,
            stars = stars,
            bestTimeSeconds = bestTimeSeconds,
            highScore = highScore
        )
    }

    companion object {
        fun fromDomainModel(progress: LevelProgress): LevelProgressEntity {
            return LevelProgressEntity(
                levelNumber = progress.levelNumber,
                isUnlocked = progress.isUnlocked,
                stars = progress.stars,
                bestTimeSeconds = progress.bestTimeSeconds,
                highScore = progress.highScore
            )
        }
    }
}

@Dao
interface GameDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()

    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun getTransactionsFlow(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: WalletTransactionEntity)

    @Query("DELETE FROM wallet_transactions")
    suspend fun clearTransactions()

    @Query("SELECT * FROM level_progress")
    fun getLevelProgressListFlow(): Flow<List<LevelProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevelProgress(progress: LevelProgressEntity)

    @Query("DELETE FROM level_progress")
    suspend fun clearLevelProgress()
}

@Database(entities = [UserProfileEntity::class, WalletTransactionEntity::class, LevelProgressEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
