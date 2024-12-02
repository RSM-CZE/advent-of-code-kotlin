import kotlin.math.abs

fun main() {
    fun readInputDay02(input: List<String>): MutableList<List<Int>> {
        val rows = mutableListOf<List<Int>>()

        input.forEach {
            rows.add(it.split(" ").map { it.toInt() })
        }
        return rows
    }

    fun matches(input: List<Int>): Boolean {
        val sorted = input.sortedDescending()
        val correctOrder = sorted == input || sorted == input.reversed()
        if (!correctOrder) return false
        sorted.zipWithNext { acc, s ->
            if (acc - s > 3 || acc - s < 1) return false
            s
        }
        return true
    }


    fun part1(input: List<String>): Int {
        // The levels are either all increasing or all decreasing.
        // Any two adjacent levels differ by at least one and at most three.
        val reports = readInputDay02(input)
        return reports.filter {
            matches(it)
        }.size
    }

    fun part2(input: List<String>): Int {
        val reports = readInputDay02(input)
        val variants = mutableListOf<Pair<Int, List<Int>>>().apply {
            reports.forEachIndexed { index, ints ->
                for (i in ints.indices) {
                    val copy = ints.toMutableList()
                    copy.removeAt(i)
                    add(index to copy)
                }
            }
        }
        return variants.filter {
            matches(it.second)
        }.map { it.first }.distinct().size
    }

    // Read the input from the `src/Day02.txt` file.
    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
