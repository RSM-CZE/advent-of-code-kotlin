import kotlin.time.measureTime

private typealias Path = List<Point>

private data class Point(val x: Int, val y: Int) {
    fun directions(): List<Point> = listOf(left(), right(), up(), down())
    fun left() = Point(x - 1, y)
    fun right() = Point(x + 1, y)
    fun up() = Point(x, y - 1)
    fun down() = Point(x, y + 1)
}

private data class Trailhead(val startPosition: Point, val paths: List<Path>) {
    val score: Int get() = paths.distinctBy { it.last() }.size
    val rating: Int get() = paths.size
}

private data class Map(val grid: List<List<Int>>) {

    fun findStartingPoints(): List<Point> {
        return mutableListOf<Point>().apply {
            for (y in grid.indices) {
                for (x in grid[y].indices) {
                    if (grid[y][x] == 0) {
                        add(Point(x, y))
                    }
                }
            }
        }
    }

    fun findTrailheads(): List<Trailhead> {
        return mutableListOf<Trailhead>().apply {
            for (startPosition in findStartingPoints()) {
                val possiblePaths: List<Path> = findPaths(startPosition)
                if (possiblePaths.isNotEmpty()) add(Trailhead(startPosition, possiblePaths))
            }
        }
    }

    fun findPaths(startPosition: Point): List<Path> {
        var paths: List<Path> = listOf(mutableListOf(startPosition))
        repeat(9) {
            paths = paths.flatMap { path ->
                path.last().getNextPoints().map { path + it }
            }
        }
        return paths
    }

    fun Point.getNextPoints(): List<Point> {
        val currValue = grid[y][x]
        return directions()
            .filter { it.y in 0..grid.lastIndex && it.x in 0..grid[y].lastIndex && grid[it.y][it.x] == currValue + 1 }
    }
}

fun main() {
    fun readInputForDay(input: List<String>): List<List<Int>> {
        return input.map { it.toList().map { it.digitToInt() } }
    }

    fun part1(input: List<String>): Int {
        val grid = readInputForDay(input)
        return Map(grid).findTrailheads().sumOf { it.score }
    }

    fun part2(input: List<String>): Int {
        val grid = readInputForDay(input)
        return Map(grid).findTrailheads().sumOf { it.rating }
    }

    // Read the input from the `src/Day10.txt` file.
    val input = readInput("Day10")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
