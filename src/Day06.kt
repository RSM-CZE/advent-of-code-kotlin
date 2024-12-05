import kotlin.time.measureTime

fun main() {
    fun readInputDay06(input: List<String>): Pair<MutableList<Int>, MutableList<Int>> {
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
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // Read the input from the `src/Day06.txt` file.
    val input = readInput("Day06")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
