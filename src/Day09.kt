import kotlin.time.measureTime

sealed interface StorageSlot {
    val size: Int

    data class File(val id: Int, override val size: Int) : StorageSlot
    data class Empty(override val size: Int) : StorageSlot
}

private data class Disk(val slots: List<StorageSlot>) {
    override fun toString(): String {
        slots.flatMap { slot ->
            mutableListOf<String>().apply {
                for (i in 0 until slot.size) {
                    if (slot is StorageSlot.File)
                        add("${slot.id}+")
                    else add(".")
                }
            }
        }.let {
            return it.joinToString("")
        }
    }

    // very inefficient, definitely could be improved with map lookup of empty spaces and their corresponding sizes
    fun optimizeUnfragmented(): Disk {
        val optimizedSlots = this.slots.toMutableList()
        do {
            var modified = false
            for (i in optimizedSlots.indices.reversed()) {
                if (optimizedSlots[i] is StorageSlot.Empty) continue
                val size = optimizedSlots[i].size
                optimizedSlots.indexOfFirst { it is StorageSlot.Empty && it.size >= size && optimizedSlots.indexOf(it) < i }.let { index ->
                    if (index == -1) return@let
                    val emptySlot = optimizedSlots[index]
                    modified = true
                    optimizedSlots[index] = optimizedSlots[i].also {
                        if(emptySlot.size - size == 0) {
                            optimizedSlots[i] = StorageSlot.Empty(it.size)
                        } else {
                            emptySlot as StorageSlot.Empty
                            optimizedSlots[i] = emptySlot.copy(size = size)
                            optimizedSlots.add(index + 1, emptySlot.copy(size = emptySlot.size - size))
                        }
                    }
                }
            }
        } while (modified)
        return Disk(optimizedSlots)
    }

    fun toList(): List<String> {
        return slots.flatMap {
            mutableListOf<String>().apply {
                for (i in 0 until it.size) {
                    if (it is StorageSlot.File)
                        add("${it.id}")
                    else add(".")
                }
            }
        }
    }

    companion object {
        fun fromInput(input: List<String>): Disk {
            var id = 0
            val storage = mutableListOf<StorageSlot>()
            input.forEach { inputRow ->
                val intRows = inputRow.toList().map { it.digitToInt() }
                for (j in intRows.indices) {
                    if (j % 2 == 0) {
                        storage.add(StorageSlot.File(id, intRows[j]))
                        id++
                    } else {
                        storage.add(StorageSlot.Empty(intRows[j]))
                    }
                }
            }
            return Disk(storage)
        }
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        val optimizedDisk = Disk.fromInput(input).toList().let {
            val mutatedList = it.toMutableList()
            for (i in mutatedList.indices.reversed()) {
                if (mutatedList.indexOfFirst { it == "." } > mutatedList.indexOfLast { it != "." }) break
                mutatedList[mutatedList.indexOfFirst { it == "." }] = mutatedList[i].also {
                    mutatedList[i] = "."
                }
            }
            mutatedList
        }
        // It's already ordered, so we can ignore the points at the end
        val intList = optimizedDisk.mapNotNull { it.toIntOrNull()?.toLong() }
        return intList.reduceIndexed { index, acc, i ->
            acc + (index * i)
        }
    }

    fun part2(input: List<String>): Long {
        val optimizedDisk = Disk.fromInput(input).optimizeUnfragmented().toList()
        val intList = optimizedDisk.map { it.toIntOrNull()?.toLong() }
        return intList.reduceIndexed { index, acc, i ->
            i?.let { acc!! + (index * it) } ?: acc
        }!!
    }

    // Read the input from the `src/Day09.txt` file.
    val input = readInput("Day09")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
