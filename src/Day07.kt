import kotlin.time.measureTime

fun main() {
    fun readInputDay07(input: List<String>): List<Pair<Long, List<Long>>> {
        return input.map {
            it.split(": ").let {
                it.first().toLong() to it.last().split(" ").map { it.toLong() }
            }
        }
    }

    fun String.findFirstNumber(): Long {
        return if (!contains('+') && !contains('*') && !contains('|')) toLong()
        else split('+', '*', '|').first { it.firstOrNull()?.isDigit() == true }.toLong()
    }

    fun String.calculate(): Long {
        var result = findFirstNumber()
        for (i in indices) {
            if (this[i] == '+') {
                result += substring(i + 1).findFirstNumber()
            } else if (this[i] == '*') {
                result *= substring(i + 1).findFirstNumber()
            } else if (this[i] == '|') {
                result = "$result${substring(i + 1).findFirstNumber()}".toLong()
            }
        }
        return result
    }

    fun List<Char>.generatePermutations(length: Int, currentString: String = "", result: MutableList<String>) {
        if (currentString.length == length) {
            result.add(currentString)
            return
        }

        forEach { char ->
            generatePermutations(length, currentString + char, result)
        }
    }

    fun List<Char>.possiblePermutations(length: Int): List<String> {
        val result = mutableListOf<String>()
        generatePermutations(length = length, result = result)
        return result
    }

    fun part1(input: List<String>): Long {
        val rules = readInputDay07(input)
        return rules.sumOf { (result, components) ->
            val operatorPermutations = listOf('+', '*').possiblePermutations(components.size - 1)
            operatorPermutations.firstOrNull { permutation ->
                var equationString = ""
                for (i in components.indices) {
                    if (i == components.size - 1) {
                        equationString += components[i]
                    } else {
                        equationString += components[i]
                        equationString += permutation[i]
                    }
                }
                equationString.calculate() == result
            }?.let { result } ?: 0
        }
    }

    fun part2(input: List<String>): Long {
        val rules = readInputDay07(input)
        return rules.sumOf { (result, components) ->
            val operatorPermutations = listOf('+', '*', '|').possiblePermutations(components.size - 1)
            operatorPermutations.firstOrNull { permutation ->
                var equationString = ""
                for (i in components.indices) {
                    if (i == components.size - 1) {
                        equationString += components[i]
                    } else {
                        equationString += components[i]
                        equationString += permutation[i]
                    }
                }
                equationString.calculate() == result
            }?.let { result } ?: 0
        }
    }

    // Read the input from the `src/Day07.txt` file.
    val input = readInput("Day07")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
