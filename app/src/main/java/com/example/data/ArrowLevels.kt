package com.example.data

import com.example.model.*

object ArrowLevels {
    val levels = listOf(
        // Level 1: Beginner
        GameLevel(
            id = "level_1",
            levelNumber = 1,
            name = "C1: Easy Start",
            difficulty = Difficulty.BEGINNER,
            gridWidth = 12,
            gridHeight = 12,
            arrows = listOf(
                Arrow(
                    id = "arrow_1_1",
                    color = "Green",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 2), Point(4, 2), Point(4, 4), Point(7, 4))
                ),
                Arrow(
                    id = "arrow_1_2",
                    color = "Purple",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(9, 10), Point(9, 7), Point(6, 7), Point(6, 4))
                ),
                Arrow(
                    id = "arrow_1_3",
                    color = "Blue",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(1, 7), Point(1, 10), Point(3, 10))
                ),
                Arrow(
                    id = "arrow_1_4",
                    color = "Orange",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(10, 2), Point(7, 2), Point(7, 1))
                )
            ),
            initialHearts = 3,
            moveLimit = 15,
            rewardMin = 100,
            rewardMax = 200
        ),
        // Level 2: Easy
        GameLevel(
            id = "level_2",
            levelNumber = 2,
            name = "C2: Crossings",
            difficulty = Difficulty.EASY,
            gridWidth = 12,
            gridHeight = 12,
            arrows = listOf(
                Arrow(
                    id = "arrow_2_1",
                    color = "Pink",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(10, 2), Point(6, 2), Point(6, 1))
                ),
                Arrow(
                    id = "arrow_2_2",
                    color = "Orange",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(2, 2), Point(2, 5), Point(4, 5))
                ),
                Arrow(
                    id = "arrow_2_3",
                    color = "Cyan",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(3, 10), Point(3, 7), Point(6, 7))
                ),
                Arrow(
                    id = "arrow_2_4",
                    color = "Yellow",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 8), Point(5, 8), Point(5, 10))
                ),
                Arrow(
                    id = "arrow_2_5",
                    color = "Red",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(9, 10), Point(9, 6), Point(11, 6))
                )
            ),
            initialHearts = 3,
            moveLimit = 20,
            rewardMin = 120,
            rewardMax = 220
        ),
        // Level 3: Medium
        GameLevel(
            id = "level_3",
            levelNumber = 3,
            name = "C3: Snake Path",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 12,
            gridHeight = 12,
            arrows = listOf(
                Arrow(
                    id = "arrow_3_1",
                    color = "Red",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(1, 1), Point(3, 1), Point(3, 3), Point(5, 3))
                ),
                Arrow(
                    id = "arrow_3_2",
                    color = "Green",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 6), Point(4, 6), Point(4, 9), Point(7, 9))
                ),
                Arrow(
                    id = "arrow_3_3",
                    color = "Blue",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(9, 10), Point(9, 7), Point(7, 7), Point(7, 5))
                ),
                Arrow(
                    id = "arrow_3_4",
                    color = "Yellow",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(10, 2), Point(7, 2), Point(7, 4))
                ),
                Arrow(
                    id = "arrow_3_5",
                    color = "Purple",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(10, 6), Point(10, 8), Point(11, 8))
                )
            ),
            initialHearts = 3,
            moveLimit = 25,
            rewardMin = 140,
            rewardMax = 240
        ),
        // Level 4: Medium
        GameLevel(
            id = "level_4",
            levelNumber = 4,
            name = "C4: Maze Run",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 12,
            gridHeight = 12,
            arrows = listOf(
                Arrow(
                    id = "arrow_4_1",
                    color = "Cyan",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 2), Point(3, 2), Point(3, 4), Point(5, 4))
                ),
                Arrow(
                    id = "arrow_4_2",
                    color = "Orange",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(7, 1), Point(7, 4), Point(9, 4), Point(9, 6))
                ),
                Arrow(
                    id = "arrow_4_3",
                    color = "Pink",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(10, 9), Point(7, 9), Point(7, 7), Point(4, 7))
                ),
                Arrow(
                    id = "arrow_4_4",
                    color = "Green",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(2, 10), Point(2, 7), Point(4, 7))
                ),
                Arrow(
                    id = "arrow_4_5",
                    color = "Red",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 11), Point(5, 11))
                )
            ),
            initialHearts = 3,
            moveLimit = 22,
            rewardMin = 150,
            rewardMax = 250
        ),
        // Level 5: Hard
        GameLevel(
            id = "level_5",
            levelNumber = 5,
            name = "C5: Complex Winding",
            difficulty = Difficulty.HARD,
            gridWidth = 12,
            gridHeight = 12,
            arrows = listOf(
                Arrow(
                    id = "arrow_5_1",
                    color = "Blue",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 1), Point(4, 1), Point(4, 3), Point(7, 3))
                ),
                Arrow(
                    id = "arrow_5_2",
                    color = "Purple",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(9, 1), Point(9, 4), Point(11, 4))
                ),
                Arrow(
                    id = "arrow_5_3",
                    color = "Yellow",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(10, 7), Point(7, 7), Point(7, 5), Point(4, 5))
                ),
                Arrow(
                    id = "arrow_5_4",
                    color = "Green",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(2, 9), Point(2, 6), Point(5, 6), Point(5, 4))
                ),
                Arrow(
                    id = "arrow_5_5",
                    color = "Pink",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 10), Point(4, 10), Point(4, 11))
                ),
                Arrow(
                    id = "arrow_5_6",
                    color = "Orange",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(8, 9), Point(8, 11))
                )
            ),
            initialHearts = 3,
            moveLimit = 28,
            rewardMin = 180,
            rewardMax = 300
        ),
        // Level 6: Hard
        GameLevel(
            id = "level_6",
            levelNumber = 6,
            name = "C6: Interlock",
            difficulty = Difficulty.HARD,
            gridWidth = 12,
            gridHeight = 12,
            arrows = listOf(
                Arrow(
                    id = "arrow_6_1",
                    color = "Red",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(2, 1), Point(2, 4), Point(4, 4), Point(4, 6))
                ),
                Arrow(
                    id = "arrow_6_2",
                    color = "Cyan",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(6, 1), Point(8, 1), Point(8, 3), Point(10, 3))
                ),
                Arrow(
                    id = "arrow_6_3",
                    color = "Yellow",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(10, 10), Point(10, 7), Point(7, 7), Point(7, 5))
                ),
                Arrow(
                    id = "arrow_6_4",
                    color = "Green",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(8, 9), Point(5, 9), Point(5, 7), Point(2, 7))
                ),
                Arrow(
                    id = "arrow_6_5",
                    color = "Purple",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 10), Point(4, 10))
                )
            ),
            initialHearts = 3,
            moveLimit = 30,
            rewardMin = 200,
            rewardMax = 350
        ),
        // Level 7: Expert
        GameLevel(
            id = "level_7",
            levelNumber = 7,
            name = "C7: Matrix Flow",
            difficulty = Difficulty.EXPERT,
            gridWidth = 12,
            gridHeight = 12,
            arrows = listOf(
                Arrow(
                    id = "arrow_7_1",
                    color = "Orange",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(2, 2), Point(2, 5), Point(5, 5), Point(5, 2))
                ),
                Arrow(
                    id = "arrow_7_2",
                    color = "Green",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(7, 1), Point(7, 4), Point(10, 4), Point(10, 7))
                ),
                Arrow(
                    id = "arrow_7_3",
                    color = "Pink",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 8), Point(4, 8), Point(4, 10), Point(7, 10))
                ),
                Arrow(
                    id = "arrow_7_4",
                    color = "Blue",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(11, 9), Point(8, 9), Point(8, 6), Point(6, 6))
                ),
                Arrow(
                    id = "arrow_7_5",
                    color = "Yellow",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(3, 11), Point(5, 11))
                )
            ),
            initialHearts = 3,
            moveLimit = 30,
            rewardMin = 250,
            rewardMax = 400
        ),
        // Level 8: Expert
        GameLevel(
            id = "level_8",
            levelNumber = 8,
            name = "C8: Grid Locks",
            difficulty = Difficulty.EXPERT,
            gridWidth = 12,
            gridHeight = 12,
            arrows = listOf(
                Arrow(
                    id = "arrow_8_1",
                    color = "Blue",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(10, 1), Point(6, 1), Point(6, 3), Point(2, 3))
                ),
                Arrow(
                    id = "arrow_8_2",
                    color = "Yellow",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(1, 5), Point(4, 5), Point(4, 8), Point(7, 8))
                ),
                Arrow(
                    id = "arrow_8_3",
                    color = "Cyan",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(2, 10), Point(5, 10), Point(5, 7), Point(9, 7))
                ),
                Arrow(
                    id = "arrow_8_4",
                    color = "Purple",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(10, 11), Point(10, 9), Point(8, 9), Point(8, 5))
                ),
                Arrow(
                    id = "arrow_8_5",
                    color = "Red",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 1), Point(3, 1))
                )
            ),
            initialHearts = 3,
            moveLimit = 32,
            rewardMin = 280,
            rewardMax = 450
        ),
        // Level 9: Little Bee (Matching the reference screenshot with bee silhouette composition)
        GameLevel(
            id = "level_9",
            levelNumber = 9,
            name = "C9: Little Bee",
            difficulty = Difficulty.EXTREME,
            gridWidth = 12,
            gridHeight = 12,
            arrows = listOf(
                // Head / Antennas
                Arrow(
                    id = "arrow_9_1",
                    color = "Orange",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(5, 3), Point(5, 2), Point(4, 2))
                ),
                Arrow(
                    id = "arrow_9_2",
                    color = "Orange",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(6, 3), Point(6, 2), Point(7, 2))
                ),
                // Left Wing complex winding paths
                Arrow(
                    id = "arrow_9_3",
                    color = "Blue",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(4, 4), Point(2, 4), Point(2, 5), Point(1, 5))
                ),
                Arrow(
                    id = "arrow_9_4",
                    color = "Cyan",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(3, 6), Point(1, 6), Point(1, 4), Point(2, 4)) // Adjusted to prevent overlap with 9_3
                ),
                Arrow(
                    id = "arrow_9_5",
                    color = "Green",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 7), Point(3, 7), Point(3, 6), Point(5, 6))
                ),
                Arrow(
                    id = "arrow_9_6",
                    color = "Red",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(2, 8), Point(4, 8), Point(4, 9), Point(2, 9))
                ),
                // Right Wing complex winding paths
                Arrow(
                    id = "arrow_9_7",
                    color = "Blue",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(7, 4), Point(9, 4), Point(9, 5), Point(10, 5))
                ),
                Arrow(
                    id = "arrow_9_8",
                    color = "Cyan",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(8, 6), Point(10, 6), Point(10, 4), Point(9, 4))
                ),
                Arrow(
                    id = "arrow_9_9",
                    color = "Green",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(10, 7), Point(8, 7), Point(8, 6), Point(6, 6))
                ),
                Arrow(
                    id = "arrow_9_10",
                    color = "Red",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(9, 8), Point(7, 8), Point(7, 9), Point(9, 9))
                ),
                // Body / Torso
                Arrow(
                    id = "arrow_9_11",
                    color = "Purple",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(5, 4), Point(5, 7), Point(6, 7), Point(6, 9))
                ),
                Arrow(
                    id = "arrow_9_12",
                    color = "Pink",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(6, 4), Point(6, 6), Point(5, 6), Point(5, 8))
                ),
                // Stinger / Tail stripes
                Arrow(
                    id = "arrow_9_13",
                    color = "Yellow",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(5, 10), Point(5, 11))
                ),
                Arrow(
                    id = "arrow_9_14",
                    color = "Orange",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(6, 10), Point(6, 11))
                )
            ),
            initialHearts = 5,
            moveLimit = 50,
            rewardMin = 400,
            rewardMax = 800
        ),
        // Level 10: Master Loop
        GameLevel(
            id = "level_10",
            levelNumber = 10,
            name = "C10: Ultimate Master",
            difficulty = Difficulty.EXTREME,
            gridWidth = 12,
            gridHeight = 12,
            arrows = listOf(
                Arrow(
                    id = "arrow_10_1",
                    color = "Purple",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 1), Point(4, 1), Point(4, 3), Point(7, 3))
                ),
                Arrow(
                    id = "arrow_10_2",
                    color = "Blue",
                    direction = ArrowDirection.UP,
                    pathPoints = listOf(Point(8, 10), Point(8, 6), Point(5, 6), Point(5, 2))
                ),
                Arrow(
                    id = "arrow_10_3",
                    color = "Orange",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(10, 2), Point(7, 2), Point(7, 5), Point(3, 5))
                ),
                Arrow(
                    id = "arrow_10_4",
                    color = "Cyan",
                    direction = ArrowDirection.DOWN,
                    pathPoints = listOf(Point(2, 8), Point(5, 8), Point(5, 10), Point(8, 10))
                ),
                Arrow(
                    id = "arrow_10_5",
                    color = "Green",
                    direction = ArrowDirection.LEFT,
                    pathPoints = listOf(Point(11, 9), Point(8, 9), Point(8, 7), Point(4, 7))
                ),
                Arrow(
                    id = "arrow_10_6",
                    color = "Pink",
                    direction = ArrowDirection.RIGHT,
                    pathPoints = listOf(Point(1, 11), Point(5, 11), Point(5, 9))
                )
            ),
            initialHearts = 5,
            moveLimit = 40,
            rewardMin = 350,
            rewardMax = 600
        )
    )
}
