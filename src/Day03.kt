import kotlin.time.measureTime

// We better not talk about regex :D

fun main() {

    fun findAndCalculateMuls(input: String): Int {
        val mulRegex = Regex(
            """
            mul\((\d+),(\d+)\)
        """.trimIndent()
        )
        val results = mulRegex.findAll(input).map {
            it.groupValues[1].toInt() to it.groupValues[2].toInt()
        }
        return results.map {
            it.first * it.second
        }.sum()
    }

    fun part1(input: List<String>): Int {
        val wholeString = input.joinToString()
        return findAndCalculateMuls(wholeString)
    }

    fun part2(input: List<String>): Int {
        val wholeString = input.joinToString()
        val sublists = wholeString.split(Regex("(?=do)|(?=don't)"))
        val filteredString = sublists.filterNot { it.startsWith("don't") }.joinToString()
        return findAndCalculateMuls(filteredString)
    }

    // Read the input from the `src/Day03.txt` file.
    val input = readInput("Day03")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
