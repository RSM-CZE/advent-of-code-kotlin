import kotlin.time.measureTime

fun main() {
    fun readInputDay04(input: List<String>): MutableList<List<Int>> {
        val rows = mutableListOf<List<Int>>()

        input.forEach {
            rows.add(it.split(" ").map { it.toInt() })
        }
        return rows
    }


    fun part1(input: List<String>): Int {
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

// Read the input from the `src/Day04.txt` file.
    val input = readInput("Day04")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
