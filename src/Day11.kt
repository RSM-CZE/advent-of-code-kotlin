import kotlin.time.measureTime

fun main() {

    fun Map<Long, Long>.blink(): Map<Long, Long> {
        return hashMapOf<Long, Long>().apply {
            for (stone in this@blink) {
                if (stone.key == 0L) {
                    set(1, getOrDefault(1, 0) + stone.value)
                } else if (stone.key.toString().length % 2 == 0) {
                    val keyString = stone.key.toString()
                    val half = keyString.length / 2
                    val firstHalfKey = keyString.take(half).toLong()
                    val lastHalfKey = keyString.takeLast(half).toLong()
                    set(firstHalfKey, getOrDefault(firstHalfKey, 0) + stone.value)
                    set(lastHalfKey, getOrDefault(lastHalfKey, 0) + stone.value)
                } else {
                    val key = stone.key * 2024
                    set(key, getOrDefault(key, 0) + stone.value)
                }
            }
        }
    }

    fun readInputForDay(input: List<String>): Map<Long, Long> {
        return input.first().split(" ").associate { it.toLong() to 1 }
    }

    fun part1(input: List<String>): Long {
        var map = readInputForDay(input)
        repeat(25) {
            map = map.blink()
        }
        return map.values.sum()
    }

    fun part2(input: List<String>): Long {
        var map = readInputForDay(input)
        repeat(75) {
            map = map.blink()
        }
        return map.values.sum()
    }

// Read the input from the `src/Day11.txt` file.
    val input = readInput("Day11")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
