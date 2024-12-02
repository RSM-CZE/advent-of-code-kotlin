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
        var prev = sorted.first()
        for (i in 1 until sorted.size) {
            val curr = sorted[i]
            if (prev - curr > 3 || prev - curr < 1) return false
            prev = sorted[i]
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
        val variants = reports.map { report ->
            mutableListOf<List<Int>>().apply {
                for (i in report.indices) {
                    add(report.toMutableList().apply { removeAt(i) })
                }
            }
        }
        return variants.filter { variant ->
            variant.any { matches(it) }
        }.size
    }

// Read the input from the `src/Day02.txt` file.
    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
