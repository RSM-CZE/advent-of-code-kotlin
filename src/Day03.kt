import kotlin.time.measureTime

// We better not talk about regex :D

fun main() {

    fun findAndCalculateMuls(input: String): Int {
        val mulRegex = Regex(
            """
            mul\((\d+),(\d+)\)
        """.trimIndent()
        )
        return mulRegex.findAll(input).sumOf {
            it.groupValues[1].toInt() * it.groupValues[2].toInt()
        }
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
