package model

import java.util.HashMap

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

    public fun fromChar(c: Char) : MineCell {
        val cell = charToState[c]
        if (cell == null) {
            throw IllegalArgumentException("Unknown cell code: $c")
        }
        return cell
    }

    public fun toChar(): Char = representation
    public fun toString(): String = representation.toString()
}

// m lines
// n columns
public class Mine(val width: Int, val height: Int) {
    private val map: Array<Array<MineCell>> = Array(width) {
        Array<MineCell>(height) { MineCell.INVALID }
    }

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
        map[x][y] = v
    }

    private fun inRange(x: Int, y: Int) = x !in 1..width || y !in 1..height

    public fun toString(): String {
        val sb = StringBuilder()
        for (x in 1..width) {
            for (y in 1..height) {
                sb.append(this[x, y])
            }
            sb.append("\n")
        }
        return sb.toString()!!
    }
}