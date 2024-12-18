import java.util.*
import kotlin.time.measureTime

private enum class ReindeerDirection(val vector: ReindeerVector) {
    NORTH(ReindeerVector(0, -1)),
    EAST(ReindeerVector(1, 0)),
    SOUTH(ReindeerVector(0, 1)),
    WEST(ReindeerVector(-1, 0));

    fun turn(): List<ReindeerDirection> {
        return when (this) {
            NORTH -> listOf(EAST, WEST)
            EAST -> listOf(NORTH, SOUTH)
            SOUTH -> listOf(EAST, WEST)
            WEST -> listOf(NORTH, SOUTH)
        }
    }

    operator fun unaryMinus(): ReindeerDirection = when (this) {
        NORTH -> SOUTH
        EAST -> WEST
        WEST -> EAST
        SOUTH -> NORTH
    }
}

private data class ReindeerVector(val x: Int, val y: Int)

private data class ReindeerPosition(val x: Int, val y: Int) {
    operator fun plus(vector: ReindeerVector): ReindeerPosition {
        return ReindeerPosition(x + vector.x, y + vector.y)
    }
}

private fun dijkstra2(grid: List<List<Char>>, startPosition: ReindeerPosition, endPosition: ReindeerPosition): Int {
    val distances = mutableMapOf<Pair<ReindeerPosition, ReindeerDirection>, Int>().withDefault { Int.MAX_VALUE }
    val prevs =
        mutableMapOf<Pair<ReindeerPosition, ReindeerDirection>, List<Pair<ReindeerPosition, ReindeerDirection>>>().withDefault { emptyList() }
    val priorityQueue =
        PriorityQueue<Triple<ReindeerPosition, Int, ReindeerDirection>>(compareBy { it.second }).apply {
            add(
                Triple(startPosition, 0, ReindeerDirection.EAST)
            )
        }
    distances[startPosition to ReindeerDirection.EAST] = 0
    while (priorityQueue.isNotEmpty()) {
        val (position, currentDist, currDirection) = priorityQueue.poll()

        if (distances[position to currDirection]!! < currentDist) continue

        if (position == endPosition) {
            val counted = mutableSetOf<Pair<ReindeerPosition, ReindeerDirection>>()
            val seen = mutableSetOf<ReindeerPosition>()
            fun go(position: ReindeerPosition, direction: ReindeerDirection) {
                if (!counted.add(position to direction)) {
                    // Already been here
                    return
                }
                seen.add(position)
                for ((prev, prevDirection) in prevs.getValue(position to direction)) {
                    go(prev, prevDirection)
                }
            }
            go(position, currDirection)
            return seen.size
        }

        val adjacent = position + currDirection.vector
        if (adjacent.x !in grid.first().indices || adjacent.y !in grid.indices) continue
        if (grid[adjacent.y][adjacent.x] != '#') {
            val totalDist = currentDist + 1
            if (totalDist < distances.getValue(adjacent to currDirection)) {
                distances[adjacent to currDirection] = totalDist
                prevs[adjacent to currDirection] = listOf(position to currDirection)
                priorityQueue.add(Triple(adjacent, totalDist, currDirection))
            } else if (totalDist == distances.getValue(adjacent to currDirection)) {
                prevs[adjacent to currDirection] =
                    prevs.getValue(adjacent to currDirection).plus(position to currDirection)
            }
        }

        currDirection.turn().forEach { direction ->
            val totalDist = currentDist + 1000
            if (totalDist < distances.getValue(position to direction)) {
                distances[position to direction] = totalDist
                prevs[position to direction] = listOf(position to currDirection)
                priorityQueue.add(Triple(position, totalDist, direction))
            } else if (totalDist == distances.getValue(position to direction)) {
                prevs[position to direction] = prevs.getValue(position to direction).plus(position to currDirection)
            }
        }
    }
    throw IllegalStateException("No way can be found!")
}

private fun dijkstra(grid: List<List<Char>>, startPosition: ReindeerPosition, endPosition: ReindeerPosition): Int {
    val distances = mutableMapOf<Pair<ReindeerPosition, ReindeerDirection>, Int>().withDefault { Int.MAX_VALUE }
    val priorityQueue =
        PriorityQueue<Triple<ReindeerPosition, Int, ReindeerDirection>>(compareBy { it.second }).apply {
            add(
                Triple(startPosition, 0, ReindeerDirection.EAST)
            )
        }
    distances[startPosition to ReindeerDirection.EAST] = 0
    // Let's not care about visited loops in the first iteration
    // add early return for E is found!
    while (priorityQueue.isNotEmpty()) {
        val (position, currentDist, currDirection) = priorityQueue.poll()

        if (distances[position to currDirection]!! < currentDist) continue

        if (position == endPosition) {
            // This is where we start
            return currentDist
        }

        val adjacent = position + currDirection.vector
        if (adjacent.x !in grid.first().indices || adjacent.y !in grid.indices) continue
        if (grid[adjacent.y][adjacent.x] != '#') {
            val totalDist = currentDist + 1
            if (totalDist < distances.getValue(adjacent to currDirection)) {
                distances[adjacent to currDirection] = totalDist
                priorityQueue.add(Triple(adjacent, totalDist, currDirection))
            }
        }

        currDirection.turn().forEach { direction ->
            val totalDist = currentDist + 1000
            if (totalDist < distances.getValue(position to direction)) {
                distances[position to direction] = totalDist
                priorityQueue.add(Triple(position, totalDist, direction))
            }
        }
    }
    throw IllegalStateException("No way can be found!")
}

fun main() {
    fun readInputForDay(input: List<String>): List<List<Char>> {
        return input.map { it.toList() }
    }

    fun part1(input: List<String>): Int {
        val grid = readInputForDay(input)
        val startPosition = grid.mapIndexed { index, item ->
            item.indexOfFirst { it == 'S' } to index
        }.first { it.first != -1 }.let { ReindeerPosition(it.first, it.second) }

        val endPosition = grid.mapIndexed { index, item ->
            item.indexOfFirst { it == 'E' } to index
        }.first { it.first != -1 }.let { ReindeerPosition(it.first, it.second) }

        return dijkstra(grid, startPosition, endPosition)
    }

    fun part2(input: List<String>): Int {
        val grid = readInputForDay(input)
        val startPosition = grid.mapIndexed { index, item ->
            item.indexOfFirst { it == 'S' } to index
        }.first { it.first != -1 }.let { ReindeerPosition(it.first, it.second) }

        val endPosition = grid.mapIndexed { index, item ->
            item.indexOfFirst { it == 'E' } to index
        }.first { it.first != -1 }.let { ReindeerPosition(it.first, it.second) }

        return dijkstra2(grid, startPosition, endPosition)
    }

    // Read the input from the `src/Day16.txt` file.
    val input = readInput("Day16")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
