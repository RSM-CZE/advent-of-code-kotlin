import kotlin.time.measureTime

fun main() {

    fun readInputDay05(input: List<String>): Pair<Map<Int, List<Int>>, List<List<Int>>> {
        val inputSplit = input.partition { it.contains("|") }
        val rulesMap = mutableMapOf<Int, List<Int>>()
        inputSplit.first.forEach {
            val values = it.split("|")
            rulesMap[values[1].toInt()] =
                rulesMap[values[1].toInt()]?.let { it + values[0].toInt() } ?: listOf(values[0].toInt())
        }
        return rulesMap.toMap() to inputSplit.second.filterNot { it.isEmpty() }.map { it.split(",").map { it.toInt() } }
    }

    fun List<Int>.isValid(rules: Map<Int, List<Int>>): Boolean =
        indices.all { start ->
            subList(start, size).none { rules[this[start]]?.contains(it) == true }
        }

    fun List<Int>.findInvalidity(rules: Map<Int, List<Int>>): Pair<Int, Int> {
        for (start in indices) {
            subList(start, size).firstOrNull { rules[this[start]]?.contains(it) == true }?.let {
                return this[start] to it
            }
        }
        // do not run this when valid
        return -1 to -1
    }

    fun part1(input: List<String>): Int {
        val (rulesMap, modifications) = readInputDay05(input)
        val validModifications = modifications.toMutableList()
        modifications.forEach { modification ->
            if (!modification.isValid(rulesMap)) validModifications.remove(modification)
        }
        return validModifications.sumOf {
            it[it.size / 2]
        }
    }

    fun part2(input: List<String>): Int {
        val (rulesMap, modifications) = readInputDay05(input)
        val cleanedInvalidModifications = modifications.mapNotNull { modification ->
            if (modification.isValid(rulesMap)) return@mapNotNull null
            else {
                val swappableModification = modification.toMutableList()
                do {
                    val (first, second) = swappableModification.findInvalidity(rulesMap)
                    swappableModification[swappableModification.indexOf(first)] =
                        second.also { swappableModification[swappableModification.indexOf(second)] = first }
                    val isValid = swappableModification.isValid(rulesMap)
                } while (!isValid)
                swappableModification
            }
        }
        return cleanedInvalidModifications.sumOf {
            it[it.size / 2]
        }
    }

// Read the input from the `src/Day05.txt` file.
    val input = readInput("Day05")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
