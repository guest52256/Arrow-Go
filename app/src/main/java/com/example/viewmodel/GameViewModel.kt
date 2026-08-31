package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.model.*
import com.example.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Stack

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val database = androidx.room.Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "arrow_go_database"
    ).fallbackToDestructiveMigration().build()
    
    val repository = GameRepository(application, database.gameDao())
    val soundManager = SoundManager.getInstance(application)
    
    // UI state streams
    val userProfile = repository.currentUserProfile
    val transactions = repository.transactions
    val levelProgressMap = repository.levelProgressMap

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

    // 60-Second Countdown Timer
    private val _timerSeconds = MutableStateFlow(60)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()
    private var timerJob: Job? = null

    // Golden Shiny Points Score System
    private val _goldenScore = MutableStateFlow(0)
    val goldenScore: StateFlow<Int> = _goldenScore.asStateFlow()

    // Animated Flying Token Trajectories
    private val _flyingTokens = MutableStateFlow<List<FlyingToken>>(emptyList())
    val flyingTokens: StateFlow<List<FlyingToken>> = _flyingTokens.asStateFlow()

    // Magnifier Inspection Tool
    private val _isMagnifierActive = MutableStateFlow(false)
    val isMagnifierActive: StateFlow<Boolean> = _isMagnifierActive.asStateFlow()

    private val _magnifierPosition = MutableStateFlow(Offset(0.5f, 0.5f))
    val magnifierPosition: StateFlow<Offset> = _magnifierPosition.asStateFlow()

    // Color Palette Switcher
    private val _paletteTheme = MutableStateFlow(ColorPaletteTheme.NEON_GLOW)
    val paletteTheme: StateFlow<ColorPaletteTheme> = _paletteTheme.asStateFlow()

    // Sound & Haptics settings
    var isSoundEnabled: Boolean
        get() = soundManager.isSoundEnabled
        set(value) { soundManager.isSoundEnabled = value }

    var isHapticsEnabled: Boolean
        get() = soundManager.isHapticsEnabled
        set(value) { soundManager.isHapticsEnabled = value }

    // Undo Stack
    private val undoStack = Stack<List<Arrow>>()

    // Feature Daily Quotas
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

    private val _gameOverReason = MutableStateFlow(GameOverReason.OUT_OF_LIVES)
    val gameOverReason: StateFlow<GameOverReason> = _gameOverReason.asStateFlow()

    private val _blockedArrowId = MutableStateFlow<String?>(null)
    val blockedArrowId: StateFlow<String?> = _blockedArrowId.asStateFlow()

    private val _blockedCollisionPoint = MutableStateFlow<Point?>(null)
    val blockedCollisionPoint: StateFlow<Point?> = _blockedCollisionPoint.asStateFlow()

    private val _scorePopups = MutableStateFlow<List<ScorePopup>>(emptyList())
    val scorePopups: StateFlow<List<ScorePopup>> = _scorePopups.asStateFlow()

    private val _comboStreak = MutableStateFlow(0)
    val comboStreak: StateFlow<Int> = _comboStreak.asStateFlow()

    private val _lastClearedReward = MutableStateFlow<LevelRewardResult?>(null)
    val lastClearedReward: StateFlow<LevelRewardResult?> = _lastClearedReward.asStateFlow()

    // Completion metrics
    private val _starsEarned = MutableStateFlow(3)
    val starsEarned: StateFlow<Int> = _starsEarned.asStateFlow()

    private val _completionTimeSeconds = MutableStateFlow(0)
    val completionTimeSeconds: StateFlow<Int> = _completionTimeSeconds.asStateFlow()

    private val _speedBonusSeconds = MutableStateFlow(0)
    val speedBonusSeconds: StateFlow<Int> = _speedBonusSeconds.asStateFlow()

    enum class GameState {
        PLAYING, LEVEL_COMPLETE, GAME_OVER
    }

    enum class GameOverReason {
        OUT_OF_LIVES, TIME_UP
    }

    data class LevelRewardResult(
        val totalCoins: Int,
        val cashCoins: Int,
        val gameCoins: Int,
        val bonusCoinsClaimed: Boolean = false
    )

    init {
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
        val sanitizedArrows = level.arrows.map { ProceduralLevelGenerator.sanitizeArrowPath(it.copy()) }
        val sanitizedLevel = level.copy(arrows = sanitizedArrows)
        _currentLevel.value = sanitizedLevel
        _activeArrows.value = sanitizedArrows
        _totalArrowsCount.value = sanitizedArrows.size
        _remainingArrowsCount.value = sanitizedArrows.size
        _hearts.value = level.initialHearts
        _moves.value = 0
        _goldenScore.value = 0
        _flyingTokens.value = emptyList()
        _isMagnifierActive.value = false
        undoStack.clear()
        _gameState.value = GameState.PLAYING
        _lastClearedReward.value = null
        _timerSeconds.value = 60
        startTimer()
    }

    fun resetLevel() {
        loadLevel(_currentLevel.value)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerSeconds.value > 0 && _gameState.value == GameState.PLAYING) {
                delay(1000)
                if (_gameState.value == GameState.PLAYING) {
                    _timerSeconds.value -= 1
                    if (_timerSeconds.value <= 0) {
                        // Timer expired
                        soundManager.playErrorBuzzer()
                        _gameOverReason.value = GameOverReason.TIME_UP
                        _gameState.value = GameState.GAME_OVER
                    }
                }
            }
        }
    }

    fun toggleMagnifier() {
        _isMagnifierActive.value = !_isMagnifierActive.value
        soundManager.playClick()
    }

    fun updateMagnifierPosition(normalizedOffset: Offset) {
        _magnifierPosition.value = normalizedOffset
    }

    fun cyclePaletteTheme() {
        val allThemes = ColorPaletteTheme.values()
        val nextIndex = (allThemes.indexOf(_paletteTheme.value) + 1) % allThemes.size
        _paletteTheme.value = allThemes[nextIndex]
        soundManager.playClick()
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

        val otherArrows = allArrows.filter { it.id != arrow.id }
        val occupiedByOthers = otherArrows.flatMap { getOccupiedPointsForArrow(it) }.toSet()
        val occupiedByObstacles = _currentLevel.value.obstacles.map { Point(it.x, it.y) }.toSet()

        for (rayPt in rayPoints) {
            if (occupiedByOthers.contains(rayPt) || occupiedByObstacles.contains(rayPt)) {
                return false
            }
        }
        return true
    }

    fun getFirstCollisionPoint(arrow: Arrow, allArrows: List<Arrow>): Point? {
        if (arrow.pathPoints.isEmpty()) return null
        val head = arrow.pathPoints.last()
        val gridWidth = _currentLevel.value.gridWidth
        val gridHeight = _currentLevel.value.gridHeight

        val rayPoints = mutableListOf<Point>()
        when (arrow.direction) {
            ArrowDirection.UP -> for (y in head.y - 1 downTo 0) rayPoints.add(Point(head.x, y))
            ArrowDirection.DOWN -> for (y in head.y + 1..gridHeight) rayPoints.add(Point(head.x, y))
            ArrowDirection.LEFT -> for (x in head.x - 1 downTo 0) rayPoints.add(Point(x, head.y))
            ArrowDirection.RIGHT -> for (x in head.x + 1..gridWidth) rayPoints.add(Point(x, head.y))
        }

        val otherArrows = allArrows.filter { it.id != arrow.id }
        val occupiedByOthers = otherArrows.flatMap { getOccupiedPointsForArrow(it) }.toSet()
        val occupiedByObstacles = _currentLevel.value.obstacles.map { Point(it.x, it.y) }.toSet()

        for (rayPt in rayPoints) {
            if (occupiedByOthers.contains(rayPt) || occupiedByObstacles.contains(rayPt)) {
                return rayPt
            }
        }
        return null
    }

    fun handleArrowTap(arrowId: String, screenTouchX: Float = 0.5f, screenTouchY: Float = 0.5f) {
        if (_gameState.value != GameState.PLAYING) return
        
        val arrows = _activeArrows.value
        val arrow = arrows.find { it.id == arrowId } ?: return
        
        if (arrow.isSliding) return

        if (canArrowExit(arrow, arrows)) {
            // Correct Move
            soundManager.playWhoosh()
            soundManager.playCoinChime()

            val stateCopy = arrows.map { it.copy(isHinted = false) }
            undoStack.push(stateCopy)
            _moves.value += 1
            _comboStreak.value += 1
            _goldenScore.value += 10
            
            val combo = _comboStreak.value
            val popupText = if (combo > 1) "COMBO x$combo! +${10 * combo}" else "+10"
            val headPt = arrow.pathPoints.lastOrNull() ?: Point(0, 0)
            
            // Score popup tag
            val newPopup = ScorePopup(
                text = popupText,
                gridX = headPt.x.toFloat(),
                gridY = headPt.y.toFloat()
            )
            _scorePopups.value = _scorePopups.value + newPopup

            // Spawn Flying Golden Coin Trajectory
            spawnFlyingToken(
                isPositive = true,
                startX = screenTouchX,
                startY = screenTouchY,
                text = "+10"
            )

            viewModelScope.launch {
                delay(900)
                _scorePopups.value = _scorePopups.value.filter { it.id != newPopup.id }
            }

            // Start sliding animation
            _activeArrows.value = arrows.map {
                if (it.id == arrowId) {
                    it.copy(isSliding = true, isHinted = false, slideProgress = 0f)
                } else {
                    it.copy(isHinted = false)
                }
            }

            // Animate slide
            viewModelScope.launch {
                var progress = 0f
                while (progress < 1.0f) {
                    delay(16)
                    val step = (0.07f + progress * 0.14f).coerceAtLeast(0.06f)
                    progress += step
                    _activeArrows.value = _activeArrows.value.map {
                        if (it.id == arrowId) {
                            it.copy(slideProgress = minOf(progress, 1f))
                        } else {
                            it
                        }
                    }
                }
                val remaining = _activeArrows.value.filter { it.id != arrowId }
                _activeArrows.value = remaining
                _remainingArrowsCount.value = remaining.size

                if (remaining.isEmpty()) {
                    triggerLevelComplete()
                }
            }
        } else {
            // Blocked Move / Error
            soundManager.playErrorBuzzer()
            _comboStreak.value = 0
            _hearts.value = maxOf(0, _hearts.value - 1)
            _goldenScore.value = maxOf(0, _goldenScore.value - 10)
            
            // Spawn Flying Red Angry Emoji
            spawnFlyingToken(
                isPositive = false,
                startX = screenTouchX,
                startY = screenTouchY,
                text = "-10"
            )

            val collision = getFirstCollisionPoint(arrow, arrows)
            _blockedArrowId.value = arrowId
            _blockedCollisionPoint.value = collision

            viewModelScope.launch {
                delay(400)
                if (_blockedArrowId.value == arrowId) {
                    _blockedArrowId.value = null
                    _blockedCollisionPoint.value = null
                }
            }

            if (_hearts.value <= 0) {
                timerJob?.cancel()
                _gameOverReason.value = GameOverReason.OUT_OF_LIVES
                _gameState.value = GameState.GAME_OVER
            }
        }
    }

    private fun spawnFlyingToken(isPositive: Boolean, startX: Float, startY: Float, text: String) {
        val token = FlyingToken(
            isPositive = isPositive,
            startX = startX,
            startY = startY,
            endX = 0.85f, // Top-right Score Badge location
            endY = 0.06f,
            text = text,
            emoji = if (isPositive) "🪙" else "😡",
            progress = 0f
        )
        _flyingTokens.value = _flyingTokens.value + token

        viewModelScope.launch {
            var p = 0f
            while (p < 1f) {
                delay(16)
                p += 0.05f
                _flyingTokens.value = _flyingTokens.value.map {
                    if (it.id == token.id) it.copy(progress = minOf(p, 1f)) else it
                }
            }
            delay(50)
            _flyingTokens.value = _flyingTokens.value.filter { it.id != token.id }
        }
    }

    private suspend fun triggerLevelComplete() {
        timerJob?.cancel()
        soundManager.playVictoryFanfare()
        _gameState.value = GameState.LEVEL_COMPLETE
        
        val timeElapsed = (60 - _timerSeconds.value).coerceAtLeast(1)
        val speedBonus = (60 - timeElapsed).coerceAtLeast(0)
        _completionTimeSeconds.value = timeElapsed
        _speedBonusSeconds.value = speedBonus

        // Star calculation
        val stars = when {
            _hearts.value >= 3 && timeElapsed <= 35 -> 3
            _hearts.value >= 2 && timeElapsed <= 50 -> 2
            else -> 1
        }
        _starsEarned.value = stars

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
        
        // Save to repository (completes profile and persists level progress in Room)
        repository.completeLevel(level)
        repository.recordLevelCompletion(
            levelNumber = level.levelNumber,
            stars = stars,
            timeSeconds = timeElapsed,
            score = _goldenScore.value
        )
    }

    fun continueWithHearts() {
        _hearts.value = 3
        _gameState.value = GameState.PLAYING
        startTimer()
    }

    fun continueWithTime() {
        _timerSeconds.value = 30
        _gameState.value = GameState.PLAYING
        startTimer()
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
            soundManager.playClick()
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
        soundManager.playHintChime()
        val arrows = _activeArrows.value
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
        soundManager.playClick()
        _activeArrows.value = _activeArrows.value.shuffled()
    }

    fun triggerReviveWithAd() {
        viewModelScope.launch {
            repository.claimAdReward("Revive Completed")
            _hearts.value = _currentLevel.value.initialHearts
            _timerSeconds.value = 60
            _gameState.value = GameState.PLAYING
            startTimer()
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
