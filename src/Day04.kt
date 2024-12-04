import java.lang.Exception
import kotlin.time.measureTime

fun main() {
    fun countOccurrences(str: String, searchStr: String): Int {
        var count = 0
        var startIndex = 0

        while (startIndex < str.length) {
            val index = str.indexOf(searchStr, startIndex)
            if (index >= 0) {
                count++
                startIndex = index + searchStr.length
            } else {
                break
            }
        }

        return count
    }


    fun part1(input: List<String>): Int {
        val variants = mutableListOf<String>()
        // horizontal
        variants.addAll(input + input.map { it.reversed() })
        // vertical
        for (i in input.first().indices) {
            val vertical = input.map { it[i] }.joinToString("")
            variants.add(vertical)
            variants.add(vertical.reversed())
        }

        // diagonal
        for (i in input.indices) {
            for (j in input[i].indices) {
                var rightStr = input[i][j].toString()
                var leftStr = input[i][j].toString()
                var right = i + 1 to j + 1
                var left = i + 1 to j - 1
                while (leftStr.length < 4) {
                    try {
                        leftStr += input[left.first][left.second]
                        left = left.first + 1 to left.second - 1
                    } catch (e: Exception) {
                        break
                    }
                }
                while (rightStr.length < 4) {
                    try {
                        rightStr += input[right.first][right.second]
                        right = right.first + 1 to right.second + 1
                    } catch (e: Exception) {
                        break
                    }
                }
                if (rightStr.length == 4) variants.add(rightStr + rightStr.reversed())
                if (leftStr.length == 4) variants.add(leftStr + leftStr.reversed())
            }
        }
        return variants.sumOf { countOccurrences(it, "XMAS") }
    }

    fun part2(input: List<String>): Int {
        var count = 0
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (input[i][j] == 'A') {
                    try {
                        if (((input[i - 1][j - 1] == 'M' && input[i + 1][j + 1] == 'S')
                                    || (input[i - 1][j - 1] == 'S' && input[i + 1][j + 1] == 'M'))
                            && ((input[i - 1][j + 1] == 'M' && input[i + 1][j - 1] == 'S')
                                    || (input[i - 1][j + 1] == 'S' && input[i + 1][j - 1] == 'M'))
                        )
                            count++
                    } catch (e: Exception) { continue }
                }
            }
        }
        return count
    }

// Read the input from the `src/Day04.txt` file.
    val input = readInput("Day04")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
