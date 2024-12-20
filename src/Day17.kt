import kotlin.math.pow
import kotlin.time.measureTime

sealed interface Register {
    var value: Long

    class A(override var value: Long) : Register
    class B(override var value: Long) : Register
    class C(override var value: Long) : Register
}

class Computer(registerAValue: Long, registerBValue: Long, registerCValue: Long, val program: List<Int>) {

    private enum class RegisterType { A, B, C }

    private val programList: MutableList<Int> = program.toMutableList()

    private val registers = listOf(
        Register.A(registerAValue),
        Register.B(registerBValue),
        Register.C(registerCValue)
    )

    fun run(): List<Long> {
        val output = mutableListOf<Long>().apply {
            while (programList.isNotEmpty()) {
                val instruction = programList.first()
                val operand = programList[1]
                performOperation(instruction, operand)?.let { add(it) }
            }
        }
        return output
    }

    private fun performOperation(instruction: Int, operand: Int): Long? {
        when (instruction) {
            0 -> adv(getComboOperand(operand), findRegister(RegisterType.A)).also { instructionJump() }
            1 -> bxl(operand.toLong()).also { instructionJump() }
            2 -> bst(getComboOperand(operand)).also { instructionJump() }
            3 -> jnz(operand)?.let {
                programList.clear()
                programList.addAll(program.subList(it, program.size))
            }
                ?: run { instructionJump() }

            4 -> bxc(operand).also { instructionJump() }
            5 -> return out(getComboOperand(operand)).also { instructionJump() }
            6 -> bdv(getComboOperand(operand)).also { instructionJump() }
            7 -> cdv(getComboOperand(operand)).also { instructionJump() }
            else -> throw IllegalStateException("Invalid instruction: $instruction")
        }
        return null
    }

    private fun instructionJump() {
        repeat(2) {
            if (programList.isNotEmpty()) programList.removeFirst()
        }
    }

    private fun bdv(value: Long) = adv(value, findRegister(RegisterType.B))

    private fun cdv(value: Long) = adv(value, findRegister(RegisterType.C))

    private fun adv(value: Long, register: Register) {
        val denominator = 2.0.pow(value.toInt())
        val result = findRegister(RegisterType.A).value / denominator
        register.value = result.toLong()
    }

    private fun bxl(value: Long) {
        findRegister(RegisterType.B).value = findRegister(RegisterType.B).value xor value.toLong()
    }

    private fun bst(value: Long) {
        findRegister(RegisterType.B).value = value % 8L
    }

    private fun jnz(value: Int): Int? {
        if (findRegister(RegisterType.A).value == 0L) return null
        return value
    }

    private fun bxc(value: Int) = bxl(findRegister(RegisterType.C).value)

    private fun out(value: Long): Long {
        return value % 8
    }

    private fun findRegister(type: RegisterType): Register {
        return when (type) {
            RegisterType.A -> registers.find { it is Register.A }!!
            RegisterType.B -> registers.find { it is Register.B }!!
            RegisterType.C -> registers.find { it is Register.C }!!
        }
    }

    private fun getComboOperand(value: Int): Long {
        return when (value) {
            in 0..3 -> value.toLong()
            in 4..6 -> findRegister(RegisterType.entries[value - 4]).value
            else -> throw IllegalStateException("Invalid combo operand value: $value")
        }
    }
}

fun main() {
    fun readInputForDay(input: List<String>): Computer {
        val registerACount = input.first().substringAfter("A: ").toLong()
        val registerBCount = input[1].substringAfter("B: ").toLong()
        val registerCCount = input[2].substringAfter("C: ").toLong()
        val program = input[4].substringAfter(": ").split(",").map { it.toInt() }

        return Computer(registerACount, registerBCount, registerCCount, program)
    }

    fun part1(input: List<String>) {
        val computer = readInputForDay(input)
        computer.run().joinToString(",").println()
    }

    fun part2(input: List<String>): Long {
        val initialComputer = readInputForDay(input)
        val desiredOutput = initialComputer.program

        var a = 0L
        var iteration = 1

        while (true) {
            a--
            var output: List<Long>

            do {
                a++
                val computer = Computer(a, 0, 0, desiredOutput)
                output = computer.run()
                output.println()
            } while (desiredOutput.takeLast(iteration) != output.map { it.toInt() })

            if (desiredOutput == output.map { it.toInt() }) break

            a *= 8
            iteration++
        }

        return a
    }

// Read the input from the `src/Day17.txt` file.
    val input = readInput("Day17")
    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}
