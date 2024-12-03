import kotlin.time.measureTime

// We better not talk about this day :D

fun main() {

    fun String.paranthesisValue(): String? {
        val paranthesisRegex = Regex("(?<value>(?<=\\().*(?=\\)))")
        return paranthesisRegex.find(this)?.groupValues?.first()
    }

    fun findAndCalculateMuls(input: String): Int {
        val mulRegex = Regex(
            """
            mul\(\d+,\d+\)
        """.trimIndent()
        )
        val results = mulRegex.findAll(input).map { it.value }
        return results.mapNotNull {
            it.paranthesisValue()
                ?.split(",")
                ?.reduce { a, b -> (a.toInt() * b.toInt()).toString() }
                ?.toInt()
        }.sum()
    }

    fun part1(input: List<String>): Int {
        val wholeString = input.joinToString()
        return findAndCalculateMuls(wholeString)
    }

    fun part2(input: List<String>): Int {
        val wholeString = input.joinToString()
        val sublists = wholeString.split(Regex("(?=do)|(?=don't)"))
        val filteredString = sublists.filterNot { it.contains("don't") }.joinToString()
        return findAndCalculateMuls(filteredString)
    }

    // Read the input from the `src/Day03.txt` file.
    val input = readInput("Day03")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
