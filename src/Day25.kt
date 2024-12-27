import kotlin.time.measureTime

abstract class KeyLock(val data: List<List<Boolean>>) {
    val pinHeights = List(data.first().size) {
        var count = 0
        for (row in data) {
            if (row[it]) {
                count++
            }
        }
        count
    }

    val pinHeight = data.size
}

class Key(data: List<List<Boolean>>) : KeyLock(data)
class Lock(data: List<List<Boolean>>) : KeyLock(data)

fun main() {
    fun readInputForDay(input: List<String>): Pair<List<Key>, List<Lock>> {
        val groups = input.joinToString("\n").split("\n\n")
        return groups.map {
            it.split("\n").map { line ->
                line.map { char -> char == '#' }
            }
        }.partition { it.first().all { !it } }.let { (keys, locks) ->
            keys.map { Key(it) } to locks.map { Lock(it) }
        }
    }

    fun part1(input: List<String>): Int {
        val (keys, locks) = readInputForDay(input)
        return keys.sumOf { key ->
            locks.count { lock ->
                for (pin in key.pinHeights.indices) {
                    if (key.pinHeights[pin] + lock.pinHeights[pin] > key.pinHeight) {
                        return@count false
                    }
                }
                return@count true
            }
        }
    }

    fun part2(input: List<String>): Long {
        return 0
    }

// Read the input from the `src/Day25.txt` file.
    val input = readInput("Day25")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
