import kotlin.time.measureTime

private data class MachineCoordinate(val x: Long, val y: Long)

private data class MachineVector(val x: Long, val y: Long)

private data class Button(val dir: MachineVector, val price: Int)

private data class Machine(val aButton: Button, val bButton: Button, val priceCoord: MachineCoordinate) {
    // iterative approach
    fun getMinimalPrice(): Long {
        val aList = mutableMapOf<MachineCoordinate, Long>()
        val bList = mutableMapOf<MachineCoordinate, Long>()
        var currIt = 0L
        var currX = 0L
        var currY = 0L
        while (currIt <= 100 && (currX <= priceCoord.x || currY <= priceCoord.y)) {
            currX = currIt * aButton.dir.x
            currY = currIt * aButton.dir.y
            aList[MachineCoordinate(currX, currY)] = currIt
            currIt++
        }
        currIt = 0
        currX = 0
        currY = 0
        while (currIt <= 100 && (currX <= priceCoord.x || currY <= priceCoord.y)) {
            currX = currIt * bButton.dir.x
            currY = currIt * bButton.dir.y
            bList[MachineCoordinate(currX, currY)] = currIt
            currIt++
        }
        return mutableListOf<Pair<Long, Long>>().apply {
            aList.forEach { (key, value) ->
                val diffCoord = MachineCoordinate(priceCoord.x - key.x, priceCoord.y - key.y)
                bList[diffCoord]?.let { bValue ->
                    add(value to bValue)
                }
            }
        }.minByOrNull { (it.first * aButton.price) + (it.second * bButton.price) }
            ?.let { (it.first * aButton.price) + (it.second * bButton.price) } ?: 0
    }

    fun calculateMinimalPrice(): Long {
        // system of equations XD, not pretty but fast and works
        val aCount =
            ((priceCoord.x * bButton.dir.y) - (priceCoord.y * bButton.dir.x)) / ((aButton.dir.x * bButton.dir.y) - (bButton.dir.x * aButton.dir.y)).toDouble()
        val bCount =
            ((aButton.dir.x * priceCoord.y) - (aButton.dir.y * priceCoord.x)) / ((aButton.dir.x * bButton.dir.y) - (bButton.dir.x * aButton.dir.y)).toDouble()
        return if (aCount.rem(1).equals(0.0) && (bCount.rem(1).equals(0.0))) {
            (aCount.toLong() * aButton.price) + (bCount.toLong() * bButton.price)
        } else 0L
    }
}

fun main() {
    fun readInputForDay(input: List<String>, pricePlus: Long = 0): List<Machine> {
        fun getXYFromLine(line: String, splitSymbol: Char): Pair<Long, Long> {
            val lineSeparated = line.split(", ")
            val x = lineSeparated.first().substringAfter("X$splitSymbol").toLong()
            val y = lineSeparated[1].substringAfter("Y$splitSymbol").toLong()
            return x to y
        }

        var currIndex = 0
        return mutableListOf<Machine>().apply {
            while (currIndex < input.size) {
                val aButtonXY = getXYFromLine(input[currIndex], '+')
                val aButton = Button(MachineVector(aButtonXY.first, aButtonXY.second), 3)
                val bButtonXY = getXYFromLine(input[currIndex + 1], '+')
                val bButton = Button(MachineVector(bButtonXY.first, bButtonXY.second), 1)
                val priceXY = getXYFromLine(input[currIndex + 2], '=')
                val priceCoord = MachineCoordinate(priceXY.first + pricePlus, priceXY.second + pricePlus)
                currIndex += 4
                add(Machine(aButton, bButton, priceCoord))
            }
        }
    }

    fun part1(input: List<String>): Long {
        val machines = readInputForDay(input)
        return machines.sumOf { machine ->
            machine.getMinimalPrice()
        }
    }

    fun part2(input: List<String>): Long {
        val machines = readInputForDay(input, 10000000000000)
        return machines.sumOf { machine ->
            machine.calculateMinimalPrice()
        }
    }

    // Read the input from the `src/Day13.txt` file.
    val input = readInput("Day13")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
