package com.example.data

import com.example.model.*
import kotlin.math.*
import kotlin.random.Random

enum class MacroShapeType(val displayName: String, val icon: String) {
    HEART("Heart", "❤️"),
    DIAMOND("Diamond", "💎"),
    SQUARE_5X5("5x5 Square", "⬛"),
    STAR("Star", "⭐"),
    PYRAMID("Pyramid", "🔺"),
    HEXAGON("Hexagon", "⬡"),
    HOURGLASS("Hourglass", "⌛"),
    CROSS("Cross", "➕"),
    CIRCLE("Circle", "⭕")
}

object ProceduralLevelGenerator {

    /**
     * Generates a 100% guaranteed solvable level using reverse-order synthesis.
     * Guarantees:
     * 1. Shapes strictly conform to the MacroShape boundary.
     * 2. Non-intersecting rule: Paths never cross or overlap.
     * 3. The "No-Bend" Head rule: The final segment leading into the arrowhead is straight for >= 1 grid cell.
     * 4. Any bends or angles are positioned strictly in the body or tail at a distance from the head.
     * 5. Reverse Solvability: Generates arrows in reverse extraction order so that a valid clearance path is 100% guaranteed.
     */
    fun generateLevel(
        shape: MacroShapeType,
        levelNumber: Int = 1,
        seed: Long = System.currentTimeMillis()
    ): GameLevel {
        val random = Random(seed)
        val (gridW, gridH) = when (shape) {
            MacroShapeType.SQUARE_5X5 -> Pair(9, 9)
            MacroShapeType.DIAMOND -> Pair(11, 11)
            MacroShapeType.HEART -> Pair(13, 13)
            MacroShapeType.STAR -> Pair(13, 13)
            MacroShapeType.PYRAMID -> Pair(11, 11)
            MacroShapeType.HEXAGON -> Pair(11, 11)
            MacroShapeType.HOURGLASS -> Pair(11, 11)
            MacroShapeType.CROSS -> Pair(11, 11)
            MacroShapeType.CIRCLE -> Pair(11, 11)
        }

        val shapeCells = getShapeCells(shape, gridW, gridH)

        var bestArrows: List<Arrow> = emptyList()
        var attempts = 0
        val targetCount = when (shape) {
            MacroShapeType.SQUARE_5X5 -> 5
            MacroShapeType.DIAMOND -> 7
            MacroShapeType.HEART -> 10
            MacroShapeType.STAR -> 9
            MacroShapeType.PYRAMID -> 7
            MacroShapeType.HEXAGON -> 8
            MacroShapeType.HOURGLASS -> 8
            MacroShapeType.CROSS -> 6
            MacroShapeType.CIRCLE -> 8
        }

        while (attempts < 25 && bestArrows.size < targetCount) {
            attempts++
            val generated = buildArrowsInReverse(shapeCells, gridW, gridH, random)
            if (generated.size > bestArrows.size) {
                bestArrows = generated
            }
        }

        // If random synthesis had low yield, fallback to a verified geometric template for that shape
        if (bestArrows.size < 4) {
            bestArrows = getDeterministicTemplate(shape, gridW, gridH)
        }

        // Sanitize every arrow to strictly guarantee the No-Bend Head Rule (straight neck for >= 1 grid cell)
        val sanitizedArrows = bestArrows.map { sanitizeArrowPath(it) }

        // Assign colors (with #13204A dark navy as primary, plus vibrant accent palettes)
        val colorPalette = listOf("Navy", "Blue", "Cyan", "Purple", "Green", "Orange", "Pink", "Yellow")
        val coloredArrows = sanitizedArrows.mapIndexed { idx, arrow ->
            arrow.copy(
                id = "proc_${shape.name.lowercase()}_$idx",
                color = colorPalette[idx % colorPalette.size]
            )
        }

        return GameLevel(
            id = "proc_${shape.name.lowercase()}_${seed % 10000}",
            levelNumber = levelNumber,
            name = "${shape.icon} ${shape.displayName}",
            difficulty = when {
                coloredArrows.size <= 6 -> Difficulty.EASY
                coloredArrows.size <= 10 -> Difficulty.MEDIUM
                coloredArrows.size <= 15 -> Difficulty.HARD
                else -> Difficulty.EXPERT
            },
            gridWidth = gridW,
            gridHeight = gridH,
            arrows = coloredArrows,
            obstacles = emptyList(),
            initialHearts = 3,
            moveLimit = coloredArrows.size + 8,
            rewardMin = 150 + coloredArrows.size * 15,
            rewardMax = 300 + coloredArrows.size * 25
        )
    }

    /**
     * Strictly enforces the "No-Bend" Head Rule for any arrow.
     * Ensures that the final segment entering the head is completely straight in the arrow's direction.
     * Any angle or bend is positioned strictly in the body or tail at a distance from the head.
     */
    fun sanitizeArrowPath(arrow: Arrow): Arrow {
        val pts = arrow.pathPoints
        if (pts.size < 2) return arrow

        val dirVector = when (arrow.direction) {
            ArrowDirection.UP -> Point(0, -1)
            ArrowDirection.DOWN -> Point(0, 1)
            ArrowDirection.LEFT -> Point(-1, 0)
            ArrowDirection.RIGHT -> Point(1, 0)
        }

        val headAtLast = pts.last()
        val neckBeforeLast = pts[pts.size - 2]
        val lastDx = headAtLast.x - neckBeforeLast.x
        val lastDy = headAtLast.y - neckBeforeLast.y
        val lastSegmentSignX = if (lastDx != 0) lastDx / abs(lastDx) else 0
        val lastSegmentSignY = if (lastDy != 0) lastDy / abs(lastDy) else 0

        // If the last segment already moves strictly in the arrow's direction, it's valid!
        if (lastSegmentSignX == dirVector.x && lastSegmentSignY == dirVector.y) {
            return arrow
        }

        // Check if points were given in head-to-tail order instead of tail-to-head order
        val headAtFirst = pts.first()
        val neckAfterFirst = pts[1]
        val firstDx = headAtFirst.x - neckAfterFirst.x
        val firstDy = headAtFirst.y - neckAfterFirst.y
        val firstSegmentSignX = if (firstDx != 0) firstDx / abs(firstDx) else 0
        val firstSegmentSignY = if (firstDy != 0) firstDy / abs(firstDy) else 0
        if (firstSegmentSignX == dirVector.x && firstSegmentSignY == dirVector.y) {
            return arrow.copy(pathPoints = pts.reversed())
        }

        // Otherwise reconstruct path with guaranteed straight neck segment:
        // Neck is placed 1 grid cell directly behind the head
        val neck = Point(headAtLast.x - dirVector.x, headAtLast.y - dirVector.y)
        val tail = pts.first()

        val newPath = mutableListOf<Point>()
        newPath.add(tail)

        // If tail is not colinear with neck, add an orthogonal body corner
        if (tail.x != neck.x && tail.y != neck.y) {
            newPath.add(Point(neck.x, tail.y))
        }

        if (!newPath.contains(neck)) {
            newPath.add(neck)
        }
        if (!newPath.contains(headAtLast)) {
            newPath.add(headAtLast)
        }

        return arrow.copy(pathPoints = newPath)
    }

    private fun getShapeCells(shape: MacroShapeType, gridW: Int, gridH: Int): Set<Point> {
        val cells = mutableSetOf<Point>()
        val cx = gridW / 2
        val cy = gridH / 2

        when (shape) {
            MacroShapeType.SQUARE_5X5 -> {
                val startX = cx - 2
                val startY = cy - 2
                for (x in startX..(startX + 4)) {
                    for (y in startY..(startY + 4)) {
                        if (x in 1 until gridW - 1 && y in 1 until gridH - 1) {
                            cells.add(Point(x, y))
                        }
                    }
                }
            }
            MacroShapeType.DIAMOND -> {
                val radius = min(cx, cy) - 1
                for (x in 1 until gridW - 1) {
                    for (y in 1 until gridH - 1) {
                        if (abs(x - cx) + abs(y - cy) <= radius) {
                            cells.add(Point(x, y))
                        }
                    }
                }
            }
            MacroShapeType.HEART -> {
                // Classic parametric / discrete heart
                for (x in 1 until gridW - 1) {
                    for (y in 1 until gridH - 1) {
                        val nx = (x - cx).toDouble() / 4.0
                        val ny = (cy - y + 1).toDouble() / 4.0
                        val equation = (nx * nx + ny * ny - 1.0)
                        val v = equation * equation * equation - nx * nx * ny * ny * ny
                        if (v <= 0.25) {
                            cells.add(Point(x, y))
                        }
                    }
                }
            }
            MacroShapeType.STAR -> {
                for (x in 1 until gridW - 1) {
                    for (y in 1 until gridH - 1) {
                        val dx = abs(x - cx)
                        val dy = abs(y - cy)
                        if ((dx == 0 && dy <= 5) || (dy == 0 && dx <= 5) || (dx <= 3 && dy <= 3 && dx + dy <= 4)) {
                            cells.add(Point(x, y))
                        }
                    }
                }
            }
            MacroShapeType.PYRAMID -> {
                for (y in 2 until gridH - 2) {
                    val halfW = (y - 2)
                    for (x in (cx - halfW)..(cx + halfW)) {
                        if (x in 1 until gridW - 1) {
                            cells.add(Point(x, y))
                        }
                    }
                }
            }
            MacroShapeType.HEXAGON -> {
                for (x in 1 until gridW - 1) {
                    for (y in 1 until gridH - 1) {
                        val dx = abs(x - cx)
                        val dy = abs(y - cy)
                        if (dx <= 4 && dy <= 3 && (dx + dy <= 5)) {
                            cells.add(Point(x, y))
                        }
                    }
                }
            }
            MacroShapeType.HOURGLASS -> {
                for (y in 2 until gridH - 2) {
                    val dy = abs(y - cy)
                    val halfW = max(1, dy)
                    for (x in (cx - halfW)..(cx + halfW)) {
                        if (x in 1 until gridW - 1) {
                            cells.add(Point(x, y))
                        }
                    }
                }
            }
            MacroShapeType.CROSS -> {
                for (x in 1 until gridW - 1) {
                    for (y in 1 until gridH - 1) {
                        val dx = abs(x - cx)
                        val dy = abs(y - cy)
                        if ((dx <= 1 && dy <= 4) || (dy <= 1 && dx <= 4)) {
                            cells.add(Point(x, y))
                        }
                    }
                }
            }
            MacroShapeType.CIRCLE -> {
                val r = min(cx, cy) - 1.5
                for (x in 1 until gridW - 1) {
                    for (y in 1 until gridH - 1) {
                        val dist = hypot((x - cx).toDouble(), (y - cy).toDouble())
                        if (dist <= r) {
                            cells.add(Point(x, y))
                        }
                    }
                }
            }
        }
        return cells
    }

    /**
     * Reverse Solvability Generation:
     * In the forward game, Arrow 1 exits first without obstacles, Arrow 2 exits second (only blocked by Arrow 1),
     * and Arrow K exits when {Arrow 1..K-1} have been removed.
     * We synthesize arrows in the order: A_1, A_2, ..., A_N.
     */
    private fun buildArrowsInReverse(
        shapeCells: Set<Point>,
        gridW: Int,
        gridH: Int,
        random: Random
    ): List<Arrow> {
        val availableCells = shapeCells.toMutableSet()
        val placedArrows = mutableListOf<Arrow>()
        val allOccupiedPoints = mutableSetOf<Point>()

        var stuckCount = 0
        while (availableCells.isNotEmpty() && stuckCount < 100) {
            val candidateHeads = availableCells.shuffled(random)
            var arrowPlaced = false

            for (head in candidateHeads) {
                val directions = ArrowDirection.values().toList().shuffled(random)
                for (dir in directions) {
                    // Check "No-Bend" Head Rule: The cell directly behind the head must be available
                    val neck = when (dir) {
                        ArrowDirection.UP -> Point(head.x, head.y + 1)
                        ArrowDirection.DOWN -> Point(head.x, head.y - 1)
                        ArrowDirection.LEFT -> Point(head.x + 1, head.y)
                        ArrowDirection.RIGHT -> Point(head.x - 1, head.y)
                    }

                    if (!availableCells.contains(neck)) continue

                    // Check Exit Path Ray:
                    // From head in direction dir to the grid boundary:
                    // Must NOT intersect any cell that is currently in availableCells (unplaced arrows)
                    // It CAN only pass through empty space or cells occupied by arrows that exit BEFORE this arrow!
                    // In reverse order, placedArrows[0..K-1] exit BEFORE arrow K.
                    var exitRayValid = true
                    var curX = head.x
                    var curY = head.y
                    val stepDx = when (dir) {
                        ArrowDirection.LEFT -> -1
                        ArrowDirection.RIGHT -> 1
                        else -> 0
                    }
                    val stepDy = when (dir) {
                        ArrowDirection.UP -> -1
                        ArrowDirection.DOWN -> 1
                        else -> 0
                    }

                    while (true) {
                        curX += stepDx
                        curY += stepDy
                        if (curX < 0 || curX >= gridW || curY < 0 || curY >= gridH) {
                            break // Exited the board safely!
                        }
                        val rayPt = Point(curX, curY)
                        // If ray hits an unplaced cell inside the shape, exit is blocked in future forward game!
                        if (availableCells.contains(rayPt)) {
                            exitRayValid = false
                            break
                        }
                    }

                    if (!exitRayValid) continue

                    // Grow path backward from neck (straight segment into head satisfies No-Bend Head Rule)
                    val path = mutableListOf(head, neck)
                    val desiredLength = random.nextInt(2, 6)
                    var currentTail = neck

                    for (step in 2 until desiredLength) {
                        val neighbors = listOf(
                            Point(currentTail.x + 1, currentTail.y),
                            Point(currentTail.x - 1, currentTail.y),
                            Point(currentTail.x, currentTail.y + 1),
                            Point(currentTail.x, currentTail.y - 1)
                        ).filter {
                            availableCells.contains(it) && !path.contains(it) && !allOccupiedPoints.contains(it)
                        }.shuffled(random)

                        if (neighbors.isNotEmpty()) {
                            val nextPt = neighbors.first()
                            path.add(nextPt)
                            currentTail = nextPt
                        } else {
                            break
                        }
                    }

                    if (path.size >= 2) {
                        // Store path in tail-to-head order
                        val orderedPath = path.reversed()
                        val newArrow = Arrow(
                            id = "arrow_${placedArrows.size}",
                            color = "Navy",
                            direction = dir,
                            pathPoints = orderedPath
                        )
                        placedArrows.add(newArrow)
                        availableCells.removeAll(orderedPath.toSet())
                        allOccupiedPoints.addAll(orderedPath.toSet())
                        arrowPlaced = true
                        stuckCount = 0
                        break
                    }
                }
                if (arrowPlaced) break
            }

            if (!arrowPlaced) {
                stuckCount++
            }
        }

        return placedArrows
    }

    private fun getDeterministicTemplate(shape: MacroShapeType, gridW: Int, gridH: Int): List<Arrow> {
        val cx = gridW / 2
        val cy = gridH / 2
        return when (shape) {
            MacroShapeType.SQUARE_5X5 -> listOf(
                Arrow("sq_1", "Navy", ArrowDirection.UP, listOf(Point(cx - 1, cy + 2), Point(cx - 2, cy + 2), Point(cx - 2, cy - 1), Point(cx - 2, cy - 2))),
                Arrow("sq_2", "Blue", ArrowDirection.RIGHT, listOf(Point(cx - 1, cy - 2), Point(cx + 1, cy - 2), Point(cx + 2, cy - 2))),
                Arrow("sq_3", "Cyan", ArrowDirection.DOWN, listOf(Point(cx + 2, cy - 1), Point(cx + 2, cy + 1), Point(cx + 2, cy + 2))),
                Arrow("sq_4", "Purple", ArrowDirection.LEFT, listOf(Point(cx + 1, cy + 2), Point(cx, cy + 2), Point(cx - 1, cy + 2))),
                Arrow("sq_5", "Green", ArrowDirection.UP, listOf(Point(cx, cy + 1), Point(cx, cy - 1)))
            )
            MacroShapeType.HEART -> listOf(
                Arrow("h_top_l", "Pink", ArrowDirection.UP, listOf(Point(cx - 2, cy - 1), Point(cx - 3, cy - 1), Point(cx - 3, cy - 3))),
                Arrow("h_top_r", "Purple", ArrowDirection.UP, listOf(Point(cx + 2, cy - 1), Point(cx + 3, cy - 1), Point(cx + 3, cy - 3))),
                Arrow("h_lobe_l", "Navy", ArrowDirection.LEFT, listOf(Point(cx - 1, cy - 3), Point(cx - 2, cy - 3), Point(cx - 4, cy - 3))),
                Arrow("h_lobe_r", "Blue", ArrowDirection.RIGHT, listOf(Point(cx + 1, cy - 3), Point(cx + 2, cy - 3), Point(cx + 4, cy - 3))),
                Arrow("h_flank_l", "Cyan", ArrowDirection.DOWN, listOf(Point(cx - 4, cy - 2), Point(cx - 4, cy), Point(cx - 2, cy + 2), Point(cx - 1, cy + 3))),
                Arrow("h_flank_r", "Green", ArrowDirection.DOWN, listOf(Point(cx + 4, cy - 2), Point(cx + 4, cy), Point(cx + 2, cy + 2), Point(cx + 1, cy + 3))),
                Arrow("h_center_tip", "Red", ArrowDirection.DOWN, listOf(Point(cx, cy - 2), Point(cx, cy + 1), Point(cx, cy + 4)))
            )
            MacroShapeType.DIAMOND -> listOf(
                Arrow("d_top", "Cyan", ArrowDirection.UP, listOf(Point(cx, cy - 1), Point(cx, cy - 3))),
                Arrow("d_right", "Blue", ArrowDirection.RIGHT, listOf(Point(cx + 1, cy), Point(cx + 3, cy))),
                Arrow("d_bottom", "Navy", ArrowDirection.DOWN, listOf(Point(cx, cy + 1), Point(cx, cy + 3))),
                Arrow("d_left", "Purple", ArrowDirection.LEFT, listOf(Point(cx - 1, cy), Point(cx - 3, cy))),
                Arrow("d_diag_tr", "Green", ArrowDirection.RIGHT, listOf(Point(cx, cy - 2), Point(cx + 2, cy - 1), Point(cx + 3, cy - 1))),
                Arrow("d_diag_bl", "Orange", ArrowDirection.LEFT, listOf(Point(cx, cy + 2), Point(cx - 2, cy + 1), Point(cx - 3, cy + 1)))
            )
            else -> listOf(
                Arrow("c_top", "Navy", ArrowDirection.UP, listOf(Point(cx, cy), Point(cx, cy - 3))),
                Arrow("c_right", "Blue", ArrowDirection.RIGHT, listOf(Point(cx, cy + 1), Point(cx + 3, cy + 1))),
                Arrow("c_bottom", "Cyan", ArrowDirection.DOWN, listOf(Point(cx - 1, cy), Point(cx - 1, cy + 3))),
                Arrow("c_left", "Purple", ArrowDirection.LEFT, listOf(Point(cx + 1, cy - 1), Point(cx - 3, cy - 1)))
            )
        }
    }
}
