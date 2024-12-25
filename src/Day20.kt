import java.util.*
import kotlin.time.measureTime

private data class GridVector(val x: Int, val y: Int)

private data class GridPosition(val x: Int, val y: Int) {
    operator fun plus(vector: GridVector): GridPosition {
        return GridPosition(x + vector.x, y + vector.y)
    }
}

private data class Cheat(
    val start: GridPosition,
    val to: GridPosition,
    val additionalStep: Int = 1
) {
    fun calculateWonDiff(maze: Map<GridPosition, Int>): Int {
        return maze[to]!! - maze[start]!! - additionalStep - 1 // -1 for the index offset
    }
}

private enum class GridDirection(val vector: GridVector) {
    NORTH(GridVector(0, -1)),
    EAST(GridVector(1, 0)),
    SOUTH(GridVector(0, 1)),
    WEST(GridVector(-1, 0))
}

private fun findPossibleSteps(
    grid: List<List<Char>>,
    position: GridPosition
): List<GridPosition> {
    return mutableListOf<GridPosition>().apply {
        for (direction in GridDirection.entries) {
            val newPosition = position + direction.vector
            if (newPosition.x !in grid.first().indices || newPosition.y !in grid.indices) continue
            if (grid[newPosition.y][newPosition.x] == '.' || grid[newPosition.y][newPosition.x] == 'E' || grid[newPosition.y][newPosition.x] == 'S') {
                add(newPosition)
            }
        }
    }
}

private fun findNextStep(
    grid: List<List<Char>>,
    position: GridPosition,
    previousPosition: GridPosition?
): GridPosition {
    for (direction in GridDirection.entries) {
        val newPosition = position + direction.vector
        if (newPosition == previousPosition) continue
        if (grid[newPosition.y][newPosition.x] == '.' || grid[newPosition.y][newPosition.x] == 'E') {
            return newPosition
        }
    }
    throw IllegalStateException("No next point :o")
}

private fun walkMaze(
    grid: List<List<Char>>,
    startPosition: GridPosition,
    endPosition: GridPosition,
): Map<GridPosition, Int> {
    var previousPosition: GridPosition? = null
    var currPosition = startPosition
    var index = 1
    return mutableMapOf(startPosition to 0).apply {
        while (currPosition != endPosition) {
            val nextStep = findNextStep(grid, currPosition, previousPosition)
            previousPosition = currPosition
            currPosition = nextStep
            set(currPosition, index)
            index++
        }
    }
}

private fun findCheats(grid: List<List<Char>>, maze: Map<GridPosition, Int>): List<Cheat> {
    return mutableListOf<Cheat>().apply {
        for (y in 1..<grid.size - 1)
            for (x in 1..<grid.first().size - 1) {
                if (grid[y][x] == '#') {
                    val position = GridPosition(x, y)
                    val nextPositions =
                        findPossibleSteps(grid, position).sortedBy { maze[it] }.filter { maze[it]!! > -1 }
                    if (nextPositions.size == 1) continue
                    for (i in nextPositions.indices) {
                        for (j in i..<nextPositions.size) {
                            if (i == j) continue
                            add(Cheat(nextPositions[i], nextPositions[j]))
                        }
                    }
                }
            }
    }
}

// Use Djikstra to find min - important == we do not care if we pass through the track ;)
private fun findCheats2(grid: List<List<Char>>, maze: Map<GridPosition, Int>): List<Cheat> {
    return mutableListOf<Cheat>().apply {
        for (y in 1..<grid.size - 1)
            for (x in 1..<grid.first().size - 1) {
                if (grid[y][x] == '.' || grid[y][x] == 'S' || grid[y][x] == 'E') {
                    val startPosition = GridPosition(x, y)
                    val distances = mutableMapOf<GridPosition, Int>().withDefault { Int.MAX_VALUE }
                    val priorityQueue =
                        PriorityQueue<Pair<GridPosition, Int>>(compareBy { it.second }).apply {
                            add(
                                startPosition to 0
                            )
                        }
                    distances[startPosition] = 0

                    while (priorityQueue.isNotEmpty()) {
                        val (position, currentDist) = priorityQueue.poll()

                        if (distances[position]!! < currentDist) continue
                        if (currentDist >= 20) break // priority queue handles this ;)

                        for (direction in GridDirection.entries) {
                            val adjacent = position + direction.vector
                            val totalDist = currentDist + 1
                            if (totalDist < distances.getValue(adjacent)) {
                                distances[adjacent] = totalDist
                                priorityQueue.add(adjacent to totalDist)
                            }
                        }
                    }
                    for (distance in distances) {
                        if (distance.key == startPosition) continue
                        if (maze[distance.key] == null) continue
                        if (maze[distance.key]!! < 1) continue
                        add(
                            Cheat(
                                startPosition,
                                distance.key,
                                additionalStep = distances[distance.key]!! - 1,
                            )
                        )
                    }
                }
            }
    }
}

fun main() {
    fun readInputForDay(input: List<String>): List<List<Char>> {
        return input.map { it.toList() }
    }

    fun part1(input: List<String>): Int {
        val grid = readInputForDay(input)
        val startPosition = grid.mapIndexed { index, item ->
            item.indexOfFirst { it == 'S' } to index
        }.first { it.first != -1 }.let { GridPosition(it.first, it.second) }

        val endPosition = grid.mapIndexed { index, item ->
            item.indexOfFirst { it == 'E' } to index
        }.first { it.first != -1 }.let { GridPosition(it.first, it.second) }

        val maze = walkMaze(grid, startPosition, endPosition) // Careful - this includes startPosition!
        val cheats = findCheats(grid, maze)
        var count = 0
        for (cheat in cheats) {
            if (cheat.calculateWonDiff(maze) >= 100) {
                count++
            }
        }
        return count
    }

    fun part2(input: List<String>): Int {
        val grid = readInputForDay(input)
        val startPosition = grid.mapIndexed { index, item ->
            item.indexOfFirst { it == 'S' } to index
        }.first { it.first != -1 }.let { GridPosition(it.first, it.second) }

        val endPosition = grid.mapIndexed { index, item ->
            item.indexOfFirst { it == 'E' } to index
        }.first { it.first != -1 }.let { GridPosition(it.first, it.second) }

        val maze = walkMaze(grid, startPosition, endPosition) // Careful - this includes startPosition!
        val cheats = findCheats2(grid, maze)
        var count = 0
        mutableListOf<Cheat>().apply {
            for (cheat in cheats) {
                if (cheat.calculateWonDiff(maze) >= 100) {
                    count++
                    add(cheat)
                }
            }
        }
        return count
    }

    // Read the input from the `src/Day20.txt` file.
    val input = readInput("Day20")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
