package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Stack

class GameViewModel(application: Application) : AndroidViewModel(application) {
    // We instantiate Room and Repository here simply.
    private val database = androidx.room.Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "arrow_go_database"
    ).fallbackToDestructiveMigration().build()
    
    val repository = GameRepository(application, database.gameDao())
    
    // UI state streams
    val userProfile = repository.currentUserProfile
    val transactions = repository.transactions

    // Current Game Level state
    private val _currentLevel = MutableStateFlow<GameLevel>(ArrowLevels.levels[0])
    val currentLevel: StateFlow<GameLevel> = _currentLevel.asStateFlow()

    private val _activeArrows = MutableStateFlow<List<Arrow>>(emptyList())
    val activeArrows: StateFlow<List<Arrow>> = _activeArrows.asStateFlow()

    private val _remainingArrowsCount = MutableStateFlow(0)
    val remainingArrowsCount: StateFlow<Int> = _remainingArrowsCount.asStateFlow()

    private val _totalArrowsCount = MutableStateFlow(0)
    val totalArrowsCount: StateFlow<Int> = _totalArrowsCount.asStateFlow()

    private val _hearts = MutableStateFlow(3)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val _moves = MutableStateFlow(0)
    val moves: StateFlow<Int> = _moves.asStateFlow()

    // Undo Stack
    private val undoStack = Stack<List<Arrow>>()

    // Feature Daily Quotas (Free, Starter, Pro, Ultra)
    private val _dailyHintsUsed = MutableStateFlow(0)
    val dailyHintsUsed: StateFlow<Int> = _dailyHintsUsed.asStateFlow()

    private val _dailyUndosUsed = MutableStateFlow(0)
    val dailyUndosUsed: StateFlow<Int> = _dailyUndosUsed.asStateFlow()

    private val _dailyRevivesUsed = MutableStateFlow(0)
    val dailyRevivesUsed: StateFlow<Int> = _dailyRevivesUsed.asStateFlow()

    private val _dailyShufflesUsed = MutableStateFlow(0)
    val dailyShufflesUsed: StateFlow<Int> = _dailyShufflesUsed.asStateFlow()

    // Dialog & Navigation states
    private val _gameState = MutableStateFlow<GameState>(GameState.PLAYING)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _lastClearedReward = MutableStateFlow<LevelRewardResult?>(null)
    val lastClearedReward: StateFlow<LevelRewardResult?> = _lastClearedReward.asStateFlow()

    enum class GameState {
        PLAYING, LEVEL_COMPLETE, GAME_OVER
    }

    data class LevelRewardResult(
        val totalCoins: Int,
        val cashCoins: Int,
        val gameCoins: Int,
        val bonusCoinsClaimed: Boolean = false
    )

    init {
        // Automatically load current level from user profile
        viewModelScope.launch {
            userProfile.collect { profile ->
                if (profile != null) {
                    val levelNum = profile.currentLevel
                    val campaignLevel = ArrowLevels.levels.find { it.levelNumber == levelNum }
                        ?: ArrowLevels.levels.first()
                    if (_currentLevel.value.levelNumber != campaignLevel.levelNumber || _activeArrows.value.isEmpty()) {
                        loadLevel(campaignLevel)
                    }
                }
            }
        }
    }

    fun loadLevel(level: GameLevel) {
        _currentLevel.value = level
        _activeArrows.value = level.arrows.map { it.copy() }
        _totalArrowsCount.value = level.arrows.size
        _remainingArrowsCount.value = level.arrows.size
        _hearts.value = level.initialHearts
        _moves.value = 0
        undoStack.clear()
        _gameState.value = GameState.PLAYING
        _lastClearedReward.value = null
    }

    fun resetLevel() {
        loadLevel(_currentLevel.value)
    }

    // Expand path points into individual occupied grid coordinates
    fun getOccupiedPointsForArrow(arrow: Arrow): Set<Point> {
        val occupied = mutableSetOf<Point>()
        if (arrow.pathPoints.isEmpty()) return occupied
        
        for (i in 0 until arrow.pathPoints.size - 1) {
            val p1 = arrow.pathPoints[i]
            val p2 = arrow.pathPoints[i + 1]
            val minX = minOf(p1.x, p2.x)
            val maxX = maxOf(p1.x, p2.x)
            val minY = minOf(p1.y, p2.y)
            val maxY = maxOf(p1.y, p2.y)
            
            if (p1.x == p2.x) {
                for (y in minY..maxY) {
                    occupied.add(Point(p1.x, y))
                }
            } else if (p1.y == p2.y) {
                for (x in minX..maxX) {
                    occupied.add(Point(x, p1.y))
                }
            }
        }
        return occupied
    }

    // Check if an arrow has a clear exit ray
    fun canArrowExit(arrow: Arrow, allArrows: List<Arrow>): Boolean {
        if (arrow.pathPoints.isEmpty()) return false
        val head = arrow.pathPoints.last()
        val gridWidth = _currentLevel.value.gridWidth
        val gridHeight = _currentLevel.value.gridHeight
        
        // Calculate the ray points in the exit direction
        val rayPoints = mutableListOf<Point>()
        when (arrow.direction) {
            ArrowDirection.UP -> {
                for (y in head.y - 1 downTo -1) {
                    rayPoints.add(Point(head.x, y))
                }
            }
            ArrowDirection.DOWN -> {
                for (y in head.y + 1..gridHeight) {
                    rayPoints.add(Point(head.x, y))
                }
            }
            ArrowDirection.LEFT -> {
                for (x in head.x - 1 downTo -1) {
                    rayPoints.add(Point(x, head.y))
                }
            }
            ArrowDirection.RIGHT -> {
                for (x in head.x + 1..gridWidth) {
                    rayPoints.add(Point(x, head.y))
                }
            }
        }

        // Get all occupied grid coordinates of OTHER arrows and obstacles
        val otherArrows = allArrows.filter { it.id != arrow.id }
        val occupiedByOthers = otherArrows.flatMap { getOccupiedPointsForArrow(it) }.toSet()
        val occupiedByObstacles = _currentLevel.value.obstacles.map { Point(it.x, it.y) }.toSet()

        // If any point on our exit ray is occupied, we are blocked
        for (rayPt in rayPoints) {
            if (occupiedByOthers.contains(rayPt) || occupiedByObstacles.contains(rayPt)) {
                return false
            }
        }
        return true
    }

    fun handleArrowTap(arrowId: String) {
        if (_gameState.value != GameState.PLAYING) return
        
        val arrows = _activeArrows.value
        val arrow = arrows.find { it.id == arrowId } ?: return
        
        if (arrow.isSliding) return // Already sliding

        // Push current state to undo stack before moving
        val stateCopy = arrows.map { it.copy(isHinted = false) }

        if (canArrowExit(arrow, arrows)) {
            // Success move
            undoStack.push(stateCopy)
            _moves.value += 1
            
            // Start sliding animation
            _activeArrows.value = arrows.map {
                if (it.id == arrowId) {
                    it.copy(isSliding = true, isHinted = false, slideProgress = 0f)
                } else {
                    it.copy(isHinted = false)
                }
            }

            // Animate slide in background
            viewModelScope.launch {
                var progress = 0f
                while (progress < 1.0f) {
                    delay(30)
                    progress += 0.15f
                    _activeArrows.value = _activeArrows.value.map {
                        if (it.id == arrowId) {
                            it.copy(slideProgress = minOf(progress, 1f))
                        } else {
                            it
                        }
                    }
                }
                // Remove the arrow from the board
                val remaining = _activeArrows.value.filter { it.id != arrowId }
                _activeArrows.value = remaining
                _remainingArrowsCount.value = remaining.size

                // Check for Level Completion
                if (remaining.isEmpty()) {
                    triggerLevelComplete()
                }
            }
        } else {
            // Blocked move - deduct Heart/Life
            _hearts.value = maxOf(0, _hearts.value - 1)
            
            // Subtle pulse error visual feedback can go here
            if (_hearts.value <= 0) {
                _gameState.value = GameState.GAME_OVER
            }
        }
    }

    private suspend fun triggerLevelComplete() {
        _gameState.value = GameState.LEVEL_COMPLETE
        
        // Calculate level reward based on membership plan
        val profile = userProfile.value ?: return
        val level = _currentLevel.value
        
        val plan = profile.membershipPlan
        val randomRange = when (plan) {
            MembershipType.FREE -> 100..200
            MembershipType.STARTER -> 150..300
            MembershipType.PRO -> 200..400
            MembershipType.ULTRA -> 300..500
        }
        val totalReward = randomRange.random()
        val cashablePercent = when (plan) {
            MembershipType.FREE -> 20
            MembershipType.STARTER -> 50
            MembershipType.PRO -> 70
            MembershipType.ULTRA -> 100
        }
        
        val cashCoins = (totalReward * cashablePercent) / 100
        val gameCoins = totalReward - cashCoins
        
        _lastClearedReward.value = LevelRewardResult(
            totalCoins = totalReward,
            cashCoins = cashCoins,
            gameCoins = gameCoins
        )
        
        // Save to repository (completes and updates db)
        repository.completeLevel(level)
    }

    fun triggerUndo() {
        if (undoStack.isNotEmpty()) {
            val plan = userProfile.value?.membershipPlan ?: MembershipType.FREE
            val limit = when (plan) {
                MembershipType.FREE -> 1
                MembershipType.STARTER -> 3
                MembershipType.PRO -> 10
                MembershipType.ULTRA -> Int.MAX_VALUE
            }
            
            if (_dailyUndosUsed.value < limit) {
                _dailyUndosUsed.value += 1
                restorePreviousState()
            }
        }
    }

    fun triggerUndoWithAd() {
        viewModelScope.launch {
            repository.claimAdReward("Undo Restored")
            restorePreviousState()
        }
    }

    private fun restorePreviousState() {
        if (undoStack.isNotEmpty()) {
            val previousArrows = undoStack.pop()
            _activeArrows.value = previousArrows
            _remainingArrowsCount.value = previousArrows.size
            if (_moves.value > 0) _moves.value -= 1
        }
    }

    fun triggerHint() {
        val plan = userProfile.value?.membershipPlan ?: MembershipType.FREE
        val limit = when (plan) {
            MembershipType.FREE -> 1
            MembershipType.STARTER -> 2
            MembershipType.PRO -> 5
            MembershipType.ULTRA -> Int.MAX_VALUE
        }
        
        if (_dailyHintsUsed.value < limit) {
            _dailyHintsUsed.value += 1
            showHint()
        }
    }

    fun triggerHintWithAd() {
        viewModelScope.launch {
            repository.claimAdReward("Hint Restored")
            showHint()
        }
    }

    private fun showHint() {
        val arrows = _activeArrows.value
        // Find first arrow with exit path clear
        val clearArrow = arrows.find { canArrowExit(it, arrows) }
        if (clearArrow != null) {
            _activeArrows.value = arrows.map {
                if (it.id == clearArrow.id) {
                    it.copy(isHinted = true)
                } else {
                    it.copy(isHinted = false)
                }
            }
        }
    }

    fun triggerShuffle() {
        val plan = userProfile.value?.membershipPlan ?: MembershipType.FREE
        val limit = when (plan) {
            MembershipType.FREE -> 1
            MembershipType.STARTER -> 3
            MembershipType.PRO -> 5
            MembershipType.ULTRA -> 5
        }
        
        if (_dailyShufflesUsed.value < limit) {
            _dailyShufflesUsed.value += 1
            shuffleLayering()
        }
    }

    fun triggerShuffleWithAd() {
        viewModelScope.launch {
            repository.claimAdReward("Shuffle Restored")
            shuffleLayering()
        }
    }

    private fun shuffleLayering() {
        // Shuffle simply rearranges the list layer rendering/priority, which solves visual block order
        _activeArrows.value = _activeArrows.value.shuffled()
    }

    fun triggerReviveWithAd() {
        viewModelScope.launch {
            repository.claimAdReward("Revive Completed")
            _hearts.value = _currentLevel.value.initialHearts
            _gameState.value = GameState.PLAYING
        }
    }

    fun watchLevelCompleteBonusAd() {
        viewModelScope.launch {
            repository.claimAdReward("Level Double Bonus")
            val currentReward = _lastClearedReward.value ?: return@launch
            _lastClearedReward.value = currentReward.copy(
                totalCoins = currentReward.totalCoins * 2,
                cashCoins = currentReward.cashCoins * 2,
                gameCoins = currentReward.gameCoins * 2,
                bonusCoinsClaimed = true
            )
        }
    }
}
