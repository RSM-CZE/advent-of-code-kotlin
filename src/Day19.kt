import kotlin.time.measureTime

private fun List<String>.createWord(
    word: String,
    cache: MutableMap<String, Long> = mutableMapOf()
): Long {
    return if (word.isEmpty()) 1
    else cache.getOrPut(word) {
        filter { word.startsWith(it) }.sumOf {
            createWord(word.removePrefix(it), cache)
        }
    }
}

fun main() {
    fun readInputForDay(input: List<String>): Pair<List<String>, List<String>> {
        val patterns = input.first().split(",").map { it.trim() }

        val words = input.subList(2, input.size)
        return patterns to words
    }

    fun part1Regex(input: List<String>): Int {
        val (patterns, words) = readInputForDay(input)
        val regex = Regex("^(${patterns.joinToString("|")})*$")
        return words.filter { regex.matches(it) }.size
    }

    fun part1(input: List<String>): Int {
        val (patterns, words) = readInputForDay(input)
        return words.count { patterns.createWord(it) > 0 }
    }

    fun part2(input: List<String>): Long {
        val (patterns, words) = readInputForDay(input)
        return words.sumOf { patterns.createWord(it) }
    }

// Read the input from the `src/Day19.txt` file.
    val input = readInput("Day19")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
