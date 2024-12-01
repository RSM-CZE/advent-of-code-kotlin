import kotlin.math.abs

fun main() {
    fun readInputDay01(input: List<String>): Pair<MutableList<Int>, MutableList<Int>> {
        val list1 = mutableListOf<Int>()
        val list2 = mutableListOf<Int>()

        input.forEach {
            it.split("   ").let {
                list1.add(it.first().toInt())
                list2.add(it[1].toInt())
            }
        }
        return list1 to list2
    }

    fun part1(input: List<String>): Int {
        val (list1, list2) = readInputDay01(input)

        return list1.sorted().zip(list2.sorted()).fold(0) { acc, (i, j) ->
            acc + abs(i - j)
        }
    }

    fun part2(input: List<String>): Int {
        val (list1, list2) = readInputDay01(input)

        // Lets not talk about performance here :D
        return list1.sumOf { curr -> (list2.count { it == curr } * curr) }
    }

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
