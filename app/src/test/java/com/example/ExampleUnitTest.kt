package com.example

import com.example.data.ArrowLevels
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    private fun getOccupiedPointsForArrow(arrow: Arrow): Set<Point> {
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
                for (y in minY..maxY) occupied.add(Point(p1.x, y))
            } else if (p1.y == p2.y) {
                for (x in minX..maxX) occupied.add(Point(x, p1.y))
            } else {
                // If diagonal step exists, treat endpoints and path
                occupied.add(p1)
                occupied.add(p2)
            }
        }
        return occupied
    }

    private fun canArrowExit(arrow: Arrow, allArrows: List<Arrow>, obstacles: List<Obstacle>, gridWidth: Int, gridHeight: Int): Boolean {
        if (arrow.pathPoints.isEmpty()) return false
        val head = arrow.pathPoints.last()
        val rayPoints = mutableListOf<Point>()
        when (arrow.direction) {
            ArrowDirection.UP -> for (y in head.y - 1 downTo -1) rayPoints.add(Point(head.x, y))
            ArrowDirection.DOWN -> for (y in head.y + 1..gridHeight) rayPoints.add(Point(head.x, y))
            ArrowDirection.LEFT -> for (x in head.x - 1 downTo -1) rayPoints.add(Point(x, head.y))
            ArrowDirection.RIGHT -> for (x in head.x + 1..gridWidth) rayPoints.add(Point(x, head.y))
        }
        val otherArrows = allArrows.filter { it.id != arrow.id }
        val occupiedByOthers = otherArrows.flatMap { getOccupiedPointsForArrow(it) }.toSet()
        val occupiedByObstacles = obstacles.map { Point(it.x, it.y) }.toSet()
        for (rayPt in rayPoints) {
            if (occupiedByOthers.contains(rayPt) || occupiedByObstacles.contains(rayPt)) {
                return false
            }
        }
        return true
    }

    @Test
    fun verifyNoArrowIntersections() {
        val failureMessages = mutableListOf<String>()
        for (level in ArrowLevels.levels) {
            val arrowOccupiedMap = mutableMapOf<String, Set<Point>>()
            for (arrow in level.arrows) {
                val occupied = getOccupiedPointsForArrow(arrow)
                arrowOccupiedMap[arrow.id] = occupied
            }

            // Check point overlaps
            val arrowList = level.arrows
            for (i in 0 until arrowList.size) {
                for (j in i + 1 until arrowList.size) {
                    val a1 = arrowList[i]
                    val a2 = arrowList[j]
                    val pts1 = arrowOccupiedMap[a1.id]!!
                    val pts2 = arrowOccupiedMap[a2.id]!!
                    val intersection = pts1.intersect(pts2)
                    if (intersection.isNotEmpty()) {
                        failureMessages.add("Level '${level.name}' (${level.id}): arrows '${a1.id}' and '${a2.id}' share points $intersection")
                    }
                }
            }
        }
        if (failureMessages.isNotEmpty()) {
            System.err.println("=== INTERSECTION FAILURES (${failureMessages.size}) ===")
            failureMessages.forEach { System.err.println(it) }
            fail("Found ${failureMessages.size} arrow intersections:\n" + failureMessages.joinToString("\n"))
        }
    }

    @Test
    fun verifyAllLevelsAreSolvable() {
        for (level in ArrowLevels.levels) {
            var remainingArrows = level.arrows
            val clearedOrder = mutableListOf<String>()
            while (remainingArrows.isNotEmpty()) {
                val clearArrow = remainingArrows.find {
                    canArrowExit(it, remainingArrows, level.obstacles, level.gridWidth, level.gridHeight)
                }
                assertNotNull("Level ${level.name} (id: ${level.id}) must have a solvable arrow sequence, but got stuck with remaining: ${remainingArrows.map { it.id }} after clearing $clearedOrder", clearArrow)
                clearedOrder.add(clearArrow!!.id)
                remainingArrows = remainingArrows.filter { it.id != clearArrow.id }
            }
            assertEquals("Level ${level.name} should clear all ${level.arrows.size} arrows", level.arrows.size, clearedOrder.size)
        }
    }
}
