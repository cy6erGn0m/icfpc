package model

import java.util.HashMap
import util._assert

val validStates = hashSet(
                    MineCell.ROBOT,
                    MineCell.ROCK,
                    MineCell.CLOSED_LIFT,
                    MineCell.EARTH,
                    MineCell.WALL,
                    MineCell.LAMBDA,
                    MineCell.OPEN_LIFT,
                    MineCell.EMPTY
                )

val allStates = validStates + MineCell.INVALID

val charToState: java.util.Map<Char, MineCell> = run {
    val map = HashMap<Char, MineCell>()
    for (cs in validStates) {
        map[cs.representation] = cs
    }
    map
}

enum class MineCell(val representation : Char) {
    ROBOT: MineCell('R')
    ROCK: MineCell('*')
    CLOSED_LIFT: MineCell('L')
    EARTH: MineCell('.')
    WALL: MineCell('#')
    LAMBDA: MineCell('\\')
    OPEN_LIFT: MineCell('O')
    EMPTY: MineCell(' ')
    INVALID: MineCell('!')

    public fun toChar(): Char = representation
    public fun toString(): String = representation.toString()
}

// this is an extension function because enums can't have class object (KT-2410)
public fun Char.toMineCell() : MineCell {
    val cell = charToState[this]
    if (cell == null) {
        throw IllegalArgumentException("Unknown cell code: $this")
    }
    return cell
}

// m lines
// n columns
public class Mine(val width: Int, val height: Int) {
    private val map: Array<Array<MineCell>> = Array(width) {
        Array<MineCell>(height) { MineCell.INVALID }
    }

    public var lambdaCount: Int = 0
        private set

    public fun get(x: Int, y: Int) : MineCell {
        if (inRange(x, y)) {
            return MineCell.INVALID
        }
        return map[x][y]
    }

    public fun set(x: Int, y: Int, v: MineCell) {
        if (!inRange(x, y)) {
            throw IllegalArgumentException("Attempt to write $v outside the range: ($x, $y) is outside ($width, $height)")
        }
        if (v == MineCell.INVALID) {
            throw IllegalArgumentException("Attempt to write INVALID to ($x, $y)")
        }
        val oldValue = map[x][y]
        if (oldValue != MineCell.INVALID) {
            // We can write rocks over empties and other rocks
            if (v != MineCell.ROCK || oldValue != MineCell.EMPTY && oldValue != MineCell.ROCK) {
                throw IllegalArgumentException("The cell was not invalid before write: map[$x, $y] = ${oldValue}. When trying to write $v")
            }
        }
        if (v == MineCell.LAMBDA) {
            lambdaCount++
        }
        map[x][y] = v
    }

    private fun inRange(x: Int, y: Int) = x !in 0..(width - 1) || y !in 0..(height - 1)

    public fun toString(): String {
        val sb = StringBuilder()
        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                sb.append(this[x, y])
            }
            sb.append("\n")
        }
        return sb.toString()!!
    }
}

fun mineDiff(a: Mine, b: Mine): #(Int, Int)? {
    _assert(a.width == b.width, "Widths don't match: ${a.width} != ${b.width}")
    _assert(a.height == b.height, "Heights don't match: ${a.height} != ${b.height}")
    for (y in 0..a.height - 1) {
        for (x in 0..a.width - 1) {
            if (a[x, y] != b[x, y]) {
                return #(x, y)
            }
        }
    }
    return null
}
