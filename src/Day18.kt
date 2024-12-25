import java.util.*
import kotlin.time.measureTime

private data class MemoryVector(val x: Int, val y: Int)

private data class MemoryPosition(val x: Int, val y: Int) {
    fun move(direction: MemoryDirection): MemoryPosition {
        return MemoryPosition(x + direction.vector.x, y + direction.vector.y)
    }
}

private enum class MemoryDirection(val vector: MemoryVector) {
    NORTH(MemoryVector(0, -1)),
    EAST(MemoryVector(1, 0)),
    SOUTH(MemoryVector(0, 1)),
    WEST(MemoryVector(-1, 0))
}

private fun findShortestPath(map: Map<MemoryPosition, Boolean>): Int {
    val distances = mutableMapOf<MemoryPosition, Int>().withDefault { Int.MAX_VALUE }
    val priorityQueue = PriorityQueue<Pair<MemoryPosition, Int>>(compareBy { it.second }).apply {
        add(MemoryPosition(0, 0) to 0)
    }
    distances[MemoryPosition(0, 0)] = 0
    while (priorityQueue.isNotEmpty()) {
        val (position, currentDist) = priorityQueue.poll()

        if (position.x < 0 || position.x > 70 || position.y > 70 || position.y < 0) continue

        for (direction in MemoryDirection.entries) {
            val newPosition = position.move(direction)
            if (!map.getOrDefault(newPosition, false)) {
                if (distances.getValue(newPosition) > currentDist + 1) {
                    distances[newPosition] = currentDist + 1
                    priorityQueue.add(newPosition to currentDist + 1)
                }
            }
        }
    }
    return distances[MemoryPosition(70, 70)]!!
}

fun main() {
    fun readInputForDay(input: List<String>): List<MemoryPosition> {
        return input.map {
            it.split(",").map { it.trim() }.let { (x, y) -> MemoryPosition(x.toInt(), y.toInt()) }
        }
    }

    fun part1(input: List<String>): Long {
        val positionMap = readInputForDay(input).take(1024).associateWith { true }
        return findShortestPath(positionMap).toLong()
    }

    fun part2(input: List<String>): String {
        var amount = 1024 // I know that this works :D
        try {
            // Lmao :D
            while (true) {
                val positionMap = readInputForDay(input).take(amount).associateWith { true }
                findShortestPath(positionMap).toLong()
                amount++
            }
        } catch (e: NullPointerException) {
            return readInputForDay(input)[amount - 1].let { (x, y) -> "$x,$y" }
        }
    }

    // Read the input from the `src/Day18.txt` file.
    val input = readInput("Day18")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
