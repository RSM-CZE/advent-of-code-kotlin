import kotlin.time.measureTime

private data class Vector(val x: Int, val y: Int) {
    operator fun unaryMinus() = Vector(-x, -y)
}

private data class Position(val x: Int, val y: Int) {
    fun diff(to: Position): Vector {
        return Vector(this.x - to.x, this.y - to.y)
    }

    fun add(vector: Vector): Position {
        return Position(x + vector.x, y + vector.y)
    }

    fun isIn(size: Size): Boolean =
        x >= 0 && x < size.width && y >= 0 && y < size.height
}

private data class Size(val width: Int, val height: Int)

private data class AntennaPosition(val char: Char) {
    constructor(char: Char, firstPosition: Position) : this(char) {
        _positions = mutableListOf(firstPosition)
    }

    private var _positions: MutableList<Position> = mutableListOf()
    val positions: List<Position> get() = _positions
    fun addPosition(position: Position) {
        _positions.add(position)
    }

    fun getExtendedAntinodePositions(gridSize: Size): List<Position> {
        return mutableListOf<Position>().apply {
            positions.forEachIndexed { index, position ->
                for (i in index + 1 until positions.size) {
                    val diffVector = positions[i].diff(position)
                    var upperPosition = position.add(-diffVector)
                    var lowerPosition = positions[i].add(diffVector)
                    while (upperPosition.isIn(gridSize)) {
                        add(upperPosition)
                        upperPosition = upperPosition.add(-diffVector)
                    }
                    while (lowerPosition.isIn(gridSize)) {
                        add(lowerPosition)
                        lowerPosition = lowerPosition.add(diffVector)
                    }
                }
            }
        }
    }

    fun getAntinodePositions(gridSize: Size): List<Position> {
        return mutableListOf<Position>().apply {
            positions.forEachIndexed { index, position ->
                for (i in index + 1 until positions.size) {
                    val upperPosition = position
                    val lowerPosition = positions[i]
                    val diffVector = lowerPosition.diff(upperPosition)
                    upperPosition.add(-diffVector).takeIf { it.isIn(gridSize) }?.let { add(it) }
                    lowerPosition.add(diffVector).takeIf { it.isIn(gridSize) }?.let { add(it) }
                }
            }
        }
    }
}

private data class AntennaMap(
    val antennaPositions: List<AntennaPosition>,
    private val emptyPositions: List<Position>,
    val size: Size
) {
    fun getAntinodePositions(): List<Position> {
        return mutableListOf<Position>().apply {
            for (antenna in antennaPositions) {
                addAll(antenna.getAntinodePositions(this@AntennaMap.size))
            }
        }.distinct()
    }

    fun getExtendedAntinodePositions(): List<Position> {
        return mutableListOf<Position>().apply {
            for (antenna in antennaPositions) {
                addAll(antenna.getExtendedAntinodePositions(this@AntennaMap.size))
            }
        }.distinct()
    }

    companion object {
        fun scanFromGrid(grid: List<String>): AntennaMap {
            val antennaPositions = mutableListOf<AntennaPosition>()
            val emptyPositions = mutableListOf<Position>()
            for (i in grid.indices) {
                for (j in grid[i].indices) {
                    if (grid[i][j] == '.') emptyPositions.add(Position(j, i))
                    else antennaPositions.firstOrNull { it.char == grid[i][j] }?.addPosition(Position(j, i)) ?: run {
                        antennaPositions.add(AntennaPosition(grid[i][j], Position(j, i)))
                    }
                }
            }
            return AntennaMap(antennaPositions, emptyPositions, Size(grid.size, grid.first().length))
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val antennaMap = AntennaMap.scanFromGrid(input)
        return antennaMap.getAntinodePositions()
            .size
    }

    fun part2(input: List<String>): Int {
        val antennaMap = AntennaMap.scanFromGrid(input)
        return (antennaMap.getExtendedAntinodePositions() + antennaMap.antennaPositions.flatMap { it.positions })
            .distinct()
            .size
    }

    // Read the input from the `src/Day08.txt` file.
    val input = readInput("Day08")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
