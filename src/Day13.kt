import kotlin.time.measureTime

fun main() {
    fun readInputForDay(input: List<String>): List<String> {
        return input
    }

    fun part1(input: List<String>): Long {
        return 0
    }

    fun part2(input: List<String>): Long {
        return 0
    }

    // Read the input from the `src/Day13.txt` file.
    val input = readInput("Day13")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
