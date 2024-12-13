import kotlin.time.measureTime

private data class GardenPoint(val x: Int, val y: Int) {
    fun directions(): List<GardenPoint> = listOf(left(), right(), up(), down())
    fun left() = GardenPoint(x - 1, y)
    fun right() = GardenPoint(x + 1, y)
    fun up() = GardenPoint(x, y - 1)
    fun down() = GardenPoint(x, y + 1)

}

private data class GardenPosition(val point: GardenPoint, val perimeter: Int) {
    fun getCornerCount(grid: List<List<Char>>): Int {
        var corners = 0
        val pairList = listOf(
            point.left() to point.up(),
            point.right() to point.up(),
            point.left() to point.down(),
            point.right() to point.down()
        )
        for ((first, second) in pairList) {
            try {
                val upDown = grid.getOrNull(second.y)?.getOrNull(second.x) ?: '-'
                val leftRight = grid.getOrNull(first.y)?.getOrNull(first.x) ?: '-'
                val diagonal = grid.getOrNull(second.y)?.getOrNull(first.x) ?: '-'
                // Outer corner first condition, Inner corner second condition
                if ((upDown != grid[point.y][point.x] && leftRight != grid[point.y][point.x]) || (grid[point.y][point.x] == upDown && grid[point.y][point.x] == leftRight && grid[point.y][point.x] != diagonal)) {
                    corners++
                }
            } catch (e: Exception) {
                corners++
                continue
            }
        }
        return corners
    }
}

private data class GardenArea(val positions: List<GardenPosition>) {
    val size: Int get() = positions.size
    val perimeter: Int get() = positions.sumOf { it.perimeter }

    fun getSideCount(grid: List<List<Char>>): Int {
        return positions.sumOf { it.getCornerCount(grid) }
    }
}

private data class GardenMap(val areas: Map<Char, List<GardenArea>>) {
    companion object {
        fun fromInput(input: List<List<Char>>): GardenMap {
            val map = mutableMapOf<Char, MutableList<GardenArea>>()
            val searchedPoints = mutableListOf<GardenPoint>()
            for (y in input.indices) {
                for (x in input[y].indices) {
                    if (searchedPoints.contains(GardenPoint(x, y))) continue
                    val char = input[y][x]
                    getAreaPointsRecursive(input, GardenPoint(x, y), char).let { gardenPositions ->
                        val gardenArea = GardenArea(gardenPositions.second)
                        searchedPoints.addAll(gardenPositions.second.map { it.point })
                        map[char] = map.getOrDefault(char, mutableListOf()).apply {
                            add(gardenArea)
                        }
                    }
                }
            }
            return GardenMap(map)
        }

        private fun getAreaPointsRecursive(
            input: List<List<Char>>,
            point: GardenPoint,
            char: Char
        ): Pair<MutableList<MutableList<Char>>, List<GardenPosition>> {
            var perimeter = 0
            val areaList = mutableListOf<GardenPosition>()
            var inputMap = input.map { it.toMutableList() }.toMutableList()
            inputMap[point.y][point.x] = '-'
            val directionsToCheck = mutableListOf<GardenPoint>()
            for (direction in point.directions()) {
                if (direction.x !in input.first().indices || direction.y !in input.indices) {
                    perimeter++
                    continue
                } else if (input[direction.y][direction.x] == char) {
                    directionsToCheck.add(direction)
                    inputMap[direction.y][direction.x] = '-'
                } else if (input[direction.y][direction.x] != '-') {
                    perimeter++
                }
            }
            for (direction in directionsToCheck) {
                val areas = getAreaPointsRecursive(inputMap, direction, char)
                inputMap = areas.first
                areaList.addAll(areas.second)
            }
            areaList.add(GardenPosition(point, perimeter))
            return inputMap to areaList
        }
    }
}

fun main() {
    fun readInputForDay(input: List<String>): List<List<Char>> {
        return input.map { it.toList() }
    }

    fun part1(input: List<String>): Long {
        val map = GardenMap.fromInput(readInputForDay(input))
        return map.areas.values.sumOf { it.sumOf { it.size * it.perimeter } }.toLong()
    }

    fun part2(input: List<String>): Long {
        val dayInput = readInputForDay(input)
        val map = GardenMap.fromInput(dayInput)
        return map.areas.values.sumOf {
            it.sumOf {
                val sideCount = it.getSideCount(dayInput)
                it.size * sideCount
            }
        }.toLong()
    }

    // Read the input from the `src/Day12.txt` file.
    val input = readInput("Day12")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
