import kotlin.io.path.Path
import kotlin.io.path.appendText
import kotlin.time.measureTime

data class RobotPosition(val x: Int, val y: Int)

data class RobotVelocity(val x: Int, val y: Int)

data class Robot(val position: RobotPosition, val velocity: RobotVelocity) {
    fun move(times: Int, gridWidth: Int, gridHeight: Int): Robot {
        var newPositionX = position.x + (times * velocity.x)
        var newPositionY = position.y + (times * velocity.y)
        while (newPositionX < 0) {
            newPositionX += gridWidth
        }
        while (newPositionY < 0) {
            newPositionY += gridHeight
        }
        return copy(position = RobotPosition(newPositionX % gridWidth, newPositionY % gridHeight))
    }
}

fun List<Robot>.text(width: Int = 101, height: Int = 103): String {
    val array = Array(height) { Array(width) { 0 } }
    forEach { robot ->
        array[robot.position.y][robot.position.x] += 1
    }
    return array.joinToString("") { it.joinToString("") { if(it > 0) "#" else " " } + "\n" }
}

fun main() {
    fun readInputForDay(input: List<String>): List<Robot> {
        val pattern = Regex("""p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""")
        return input.mapNotNull { line ->
            pattern.find(line)?.let {
                Robot(
                    RobotPosition(it.groupValues[1].toInt(), it.groupValues[2].toInt()),
                    RobotVelocity(it.groupValues[3].toInt(), it.groupValues[4].toInt())
                )
            }
        }
    }

    fun part1(input: List<String>): Int {
        val width = 101
        val height = 103
        val robots = readInputForDay(input).map {
            it.move(100, width, height)
        }
        val ranges = listOf(
            0..<(width - 1) / 2 to 0..<(height - 1) / 2,
            (width - 1) / 2 + 1..<width to 0..<(height - 1) / 2,
            0..<(width - 1) / 2 to (height - 1) / 2 + 1..<height,
            (width - 1) / 2 + 1..<width to (height - 1) / 2 + 1..<height
        )
        val inQuadrants = ranges.map { range ->
            robots.count { robot ->
                robot.position.x in range.first && robot.position.y in range.second
            }
        }
        return inQuadrants.reduce(Int::times)
    }

    fun part2(input: List<String>): Long {
        val width = 101
        val height = 103
        var count = 0
        var robots = readInputForDay(input).map { it.move(97, width, height) }
        val file = Path("res/Day14_output.txt")
        while (true) {
            robots = robots.map {
                it.move(101, width, height)
            }
            count++
            file.appendText("Count $count:\n")
            file.appendText(robots.text(width, height))
        }
    }

    // Read the input from the `src/Day14.txt` file.
    val input = readInput("Day14")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
