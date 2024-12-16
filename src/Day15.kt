import kotlin.io.path.Path
import kotlin.io.path.appendText
import kotlin.time.measureTime

private enum class RoboMove(val char: Char) {
    UP('^'),
    DOWN('v'),
    LEFT('<'),
    RIGHT('>');

    companion object {
        fun fromChar(char: Char): RoboMove {
            return entries.first { it.char == char }
        }
    }

    fun getVector(): RoboVector {
        return when (this) {
            UP -> RoboVector(0, -1)
            DOWN -> RoboVector(0, 1)
            LEFT -> RoboVector(-1, 0)
            RIGHT -> RoboVector(1, 0)
        }
    }
}

private data class RoboVector(val x: Int, val y: Int)

private data class RoboCoords(val x: Int, val y: Int) {
    operator fun plus(vector: RoboVector): RoboCoords {
        return RoboCoords(x + vector.x, y + vector.y)
    }
}

private data class RoboGrid(val grid: List<List<Char>>) {
    private var gridMutable = grid.map { it.toMutableList() }.toMutableList()

    private val yLists get() = spinList(gridMutable)

    private fun spinList(list: List<List<Char>>): MutableList<MutableList<Char>> {
        return mutableListOf<MutableList<Char>>().apply {
            repeat(list.first().size) { iteration ->
                add(list.map { it[iteration] }.toMutableList())
            }
        }
    }

    fun partTwoisize(): RoboGrid {
        return RoboGrid(mutableListOf<List<Char>>().apply {
            for (y in grid.indices) {
                add(y, mutableListOf<Char>().apply {
                    for (x in grid[y].indices) {
                        if (grid[y][x] == '@') {
                            add(grid[y][x])
                            add('.')
                        } else if (grid[y][x] == 'O') {
                            add('[')
                            add(']')
                        } else {
                            add(grid[y][x])
                            add(grid[y][x])
                        }
                    }
                })
            }
        })
    }

    fun makeMoves(list: List<RoboMove>): RoboGrid {
        var roboPosition = grid.mapIndexed { index, item ->
            item.indexOfFirst { it == '@' } to index
        }.first { it.first != -1 }.let { RoboCoords(it.first, it.second) }
        for (move in list) {
            var iteratorPos = roboPosition + move.getVector()
            while (true) {
                if (gridMutable[iteratorPos.y][iteratorPos.x] == '#') {
                    break
                } else if (gridMutable[iteratorPos.y][iteratorPos.x] == '.') {
                    if (move == RoboMove.LEFT || move == RoboMove.RIGHT) {
                        gridMutable[iteratorPos.y].removeAt(iteratorPos.x)
                        gridMutable[roboPosition.y].add(roboPosition.x, '.')
                    } else {
                        val modifiedYList = yLists
                        modifiedYList[iteratorPos.x].removeAt(iteratorPos.y)
                        modifiedYList[roboPosition.x].add(roboPosition.y, '.')
                        gridMutable = spinList(modifiedYList)
                    }
                    roboPosition += move.getVector()
                    break
                }
                iteratorPos += move.getVector()
            }
        }
        return RoboGrid(gridMutable)
    }

    fun makeMovesPart2(list: List<RoboMove>): RoboGrid {
        var roboPosition = grid.mapIndexed { index, item ->
            item.indexOfFirst { it == '@' } to index
        }.first { it.first != -1 }.let { RoboCoords(it.first, it.second) }
        for (move in list) {
            var iteratorPos = roboPosition + move.getVector()
            while (true) {
                if (gridMutable[iteratorPos.y][iteratorPos.x] == '#') {
                    break
                } else if (gridMutable[iteratorPos.y][iteratorPos.x] == '.') {
                    if (move == RoboMove.LEFT || move == RoboMove.RIGHT) {
                        gridMutable[iteratorPos.y].removeAt(iteratorPos.x)
                        gridMutable[roboPosition.y].add(roboPosition.x, '.')
                    } else {
                        // swapping in stead of shifting, since points can be between two brackets that are moving or not moving
                        try {
                            val modifiedYList = yLists
                            val bracketCoords = if (move == RoboMove.UP) getBracketCoords(
                                roboPosition,
                                move
                            ).sortedBy { it.y } else getBracketCoords(roboPosition, move).sortedByDescending { it.y }
                            for (coord in bracketCoords) {
                                val moveDirection = coord + move.getVector()
                                modifiedYList[coord.x][coord.y] = modifiedYList[moveDirection.x][moveDirection.y].also {
                                    modifiedYList[moveDirection.x][moveDirection.y] = modifiedYList[coord.x][coord.y]
                                }
                            }
                            gridMutable = spinList(modifiedYList)
                        } catch (e: IllegalArgumentException) {
                            break
                        }
                    }
                    roboPosition += move.getVector()
                    break
                }
                iteratorPos += move.getVector()
            }
        }
        return RoboGrid(gridMutable)
    }

    fun getBracketCoords(currPosition: RoboCoords, move: RoboMove): List<RoboCoords> {
        val nextPosition = currPosition + move.getVector()
        val xToModify = mutableListOf<RoboCoords>()
        val additionalBracketPosition: RoboCoords
        if (gridMutable[nextPosition.y][nextPosition.x] == ']') {
            additionalBracketPosition = nextPosition + RoboMove.LEFT.getVector()
        } else if (gridMutable[nextPosition.y][nextPosition.x] == '[') {
            additionalBracketPosition = nextPosition + RoboMove.RIGHT.getVector()
        } else if (gridMutable[nextPosition.y][nextPosition.x] == '#') {
            throw IllegalArgumentException("Invalid move")
        } else {
            return listOf(currPosition)
        }
        xToModify.add(currPosition)
        xToModify.addAll(getBracketCoords(additionalBracketPosition, move))
        xToModify.addAll(getBracketCoords(nextPosition, move))
        return xToModify.distinct()
    }
}

fun main() {
    fun readInputForDay(input: List<String>): Pair<RoboGrid, List<RoboMove>> {
        val grid = RoboGrid(
            grid = input.takeWhile { it.isNotEmpty() }.map { line ->
                line.toList()
            }
        )
        val moves = input.takeLastWhile { it.isNotEmpty() }.map { line ->
            line.toList().map { RoboMove.fromChar(it) }
        }.flatten()

        return grid to moves
    }

    fun part1(input: List<String>): Long {
        val (grid, moves) = readInputForDay(input)
        return grid.makeMoves(moves).grid.mapIndexed { yIndex, yList ->
            yList.mapIndexed { xIndex, x -> if (x == 'O') xIndex + 100 * yIndex else 0 }.sum()
        }.sum().toLong()
    }

    fun part2(input: List<String>): Long {
        val (grid, moves) = readInputForDay(input)
        return grid.partTwoisize().makeMovesPart2(moves).grid.mapIndexed { yIndex, yList ->
            yList.mapIndexed { xIndex, x -> if (x == '[') xIndex + 100 * yIndex else 0 }.sum()
        }.sum().toLong()
    }

    // Read the input from the `src/Day15.txt` file.
    val input = readInput("Day15")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
