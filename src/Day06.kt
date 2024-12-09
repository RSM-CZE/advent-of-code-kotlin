import kotlin.time.measureTime

// FIXME: Make more efficient
enum class Direction(val char: Char) {
    UP('^'), DOWN('v'), LEFT('<'), RIGHT('>');

    fun step(): Pair<Int, Int> {
        return when (this) {
            UP -> -1 to 0
            DOWN -> 1 to 0
            LEFT -> 0 to -1
            RIGHT -> 0 to 1
        }
    }

    fun turn(): Char {
        return when (this) {
            UP -> RIGHT.char
            DOWN -> LEFT.char
            LEFT -> UP.char
            RIGHT -> DOWN.char
        }
    }

    companion object {
        fun byChar(char: Char): Direction {
            return when (char) {
                UP.char -> UP
                DOWN.char -> DOWN
                LEFT.char -> LEFT
                RIGHT.char -> RIGHT
                else -> throw IllegalArgumentException("Invalid char")
            }
        }
    }
}

fun main() {

    fun readInputDay06(input: List<String>): MutableList<MutableList<Char>> {
        return input.map { it.toMutableList() }.toMutableList()
    }

    fun List<List<Char>>.findPosition(currChar: Char): Pair<Int, Int> {
        forEachIndexed { i, list ->
            list.forEachIndexed { j, char ->
                if (char == currChar) return i to j
            }
        }
        // not gonna happen
        return -1 to -1
    }

    fun List<List<Char>>.findCurrChar(predicate: (Char) -> Boolean): Char {
        forEach { list ->
            list.forEach { char ->
                if (predicate(char)) return char
            }
        }
        throw IllegalArgumentException("Char not found")
    }

    fun MutableList<MutableList<Char>>.round(): Boolean {
        val currChar =
            findCurrChar { it == Direction.UP.char || it == Direction.DOWN.char || it == Direction.LEFT.char || it == Direction.RIGHT.char }
        val positionOfChar = findPosition(currChar)
        val direction = Direction.byChar(currChar)
        val move = direction.step()
        val nextPosition = positionOfChar.first + move.first to positionOfChar.second + move.second
        try {
            if (this[nextPosition.first][nextPosition.second] == '#') {
                this[positionOfChar.first][positionOfChar.second] = direction.turn()
            } else {
                this[positionOfChar.first][positionOfChar.second] = 'X'
                this[nextPosition.first][nextPosition.second] = currChar
            }
        } catch (e: IndexOutOfBoundsException) {
            this[positionOfChar.first][positionOfChar.second] = 'X'
            return true
        }
        return false
    }

    // Idea to make this good: if direction UP/DOWN and char | -> LOOP
    // if direction LEFT/RIGHT and char - -> LOOP
    // if char + -> LOOP

    // This is not needed, it just makes the grid look better to see the final step path :D
    fun MutableList<MutableList<Char>>.round2(): Boolean {
        val currChar =
            findCurrChar { it == Direction.UP.char || it == Direction.DOWN.char || it == Direction.LEFT.char || it == Direction.RIGHT.char }
        val positionOfChar = findPosition(currChar)
        val direction = Direction.byChar(currChar)
        val move = direction.step()
        val nextPosition = positionOfChar.first + move.first to positionOfChar.second + move.second
        try {
            if (this[nextPosition.first][nextPosition.second] == '#') {
                this[positionOfChar.first][positionOfChar.second] = direction.turn()
            } else if (this[positionOfChar.first][positionOfChar.second] == '|' || this[positionOfChar.first][positionOfChar.second] == '-') {
                this[positionOfChar.first][positionOfChar.second] = '+'
                this[nextPosition.first][nextPosition.second] = currChar
            } else {
                this[positionOfChar.first][positionOfChar.second] =
                    if (direction == Direction.LEFT || direction == Direction.RIGHT) '-' else '|'
                this[nextPosition.first][nextPosition.second] = currChar
            }
        } catch (e: IndexOutOfBoundsException) {
            this[positionOfChar.first][positionOfChar.second] =
                if (this[positionOfChar.first][positionOfChar.second] == '|' || this[positionOfChar.first][positionOfChar.second] == '-') {
                    '+'
                } else {
                    if (direction == Direction.LEFT || direction == Direction.RIGHT) '-' else '|'
                }
            return true
        }
        return false
    }

    fun part1(input: List<String>): Int {
        val grid = readInputDay06(input)
        do {
            val isFinished = grid.round()
        } while (!isFinished)
        return grid.sumOf { row -> row.count { it == 'X' } }
    }

    fun part2(input: List<String>): Int {
        var grid = readInputDay06(input)
        var count = 0
        for (i in 0 until grid.size) {
            for (j in 0 until grid[i].size) {
                if (grid[i][j] == Direction.UP.char || grid[i][j] == Direction.DOWN.char || grid[i][j] == Direction.RIGHT.char || grid[i][j] == Direction.LEFT.char) continue
                grid[i][j] = '#'
                // The most shit approach you can take :D
                var iteration = grid.size * grid.first().size
                do {
                    val isFinished = grid.round()
                    iteration--
                    if (iteration == 0) break
                } while (!isFinished)
                grid = readInputDay06(input)
                if (iteration == 0) count++
            }
        }
        return count
    }

    // Read the input from the `src/Day06.txt` file.
    val input = readInput("Day06")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
