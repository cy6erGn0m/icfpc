package model

import java.util.HashMap
import util._assert
import sun.net.www.content.audio.x_aiff

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

enum class MineCell(val representation: Char) {
    ROBOT: MineCell('R')
    ROCK: MineCell('*')
    CLOSED_LIFT: MineCell('L')
    EARTH: MineCell('.')
    WALL: MineCell('#')
    LAMBDA: MineCell('\\')
    OPEN_LIFT: MineCell('O')
    EMPTY: MineCell(' ')
    INVALID: MineCell('!')

    fun isPassable(): Boolean {
        return this == EARTH || this == EMPTY || this == LAMBDA || this == OPEN_LIFT;
    }

    public fun toChar(): Char = representation
    public fun toString(): String = representation.toString()
}

// this is an extension function because enums can't have class object (KT-2410)
public fun Char.toMineCell(): MineCell {
    val cell = charToState[this]
    if (cell == null) {
        throw IllegalArgumentException("Unknown cell code: $this")
    }
    return cell
}

public fun Mine(width: Int, height: Int): Mine = Mine(ArrayCellMatrix(width, height))

public class Mine(private val matrix: CellMatrix) {

    public val width: Int = matrix.width
    public val height: Int = matrix.height

    public var water: Int = 0
    public var floodPeriod: Int = 0
    public var nextFlood: Int = 0
    public var waterproof: Int = 10

    public var lambdaCount: Int = 0
        private set

    public var robotX: Int = -1
        get() {
            _assert($robotX != -1, "Robot position read before initialized")
            return $robotX
        }
        private set(v) {
            _assert($robotX == -1, "Robot position already set")
            $robotX = v
        }

    public var robotY: Int = -1
        get() {
            _assert($robotY != -1, "Robot position read before initialized")
            return $robotY
        }
        private set(v) {
            _assert($robotY == -1, "Robot position already set")
            $robotY = v
        }

    public fun get(x: Int, y: Int): MineCell {
        if (!inRange(x, y)) {
            return MineCell.INVALID
        }
        return matrix[x, y]
    }

    public fun set(x: Int, y: Int, v: MineCell) {
        if (!inRange(x, y)) {
            throw IllegalArgumentException("Attempt to write $v outside the range: ($x, $y) is outside ($width, $height)")
        }
        if (v == MineCell.INVALID) {
            throw IllegalArgumentException("Attempt to write INVALID to ($x, $y)")
        }
        val oldValue = matrix[x, y]
        if (oldValue == v) return

//        if (oldValue != MineCell.INVALID) {
//            // We can write rocks over empties and other rocks
//            if (v != MineCell.ROCK || oldValue != MineCell.EMPTY && oldValue != MineCell.ROCK) {
//                throw IllegalArgumentException("The cell was not invalid before write: map[$x, $y] = ${oldValue}. When trying to write '$v'")
//            }
//        }
        when (v) {
            MineCell.LAMBDA -> lambdaCount++
            MineCell.ROBOT -> {
                robotX = x
                robotY = y
            }
            else -> {
            }
        }
        matrix[x, y] = v
    }

    public fun moveRobot(oldX: Int, oldY: Int, newX: Int, newY: Int) {
        if (matrix[oldX, oldY] != MineCell.ROBOT) {
            throw IllegalStateException("No robot at ($oldX, $oldY)")
        }
        if (!matrix[newX, newY].isPassable()) {
            throw IllegalStateException("Map is not passable at ($newX, $newY)")
        }
        map[oldX, oldY] = MineCell.EMPTY
        if (map[newX, newY] == MineCell.LAMBDA) {
            lambdaCount--
        }
        //if robot enters lift it just disappears
        if (map[newX, newY] != MineCell.OPEN_LIFT) {
            map[newX, newY] = MineCell.ROBOT
        }
    }

    public fun tryMoveRock(rockX: Int, rockY: Int, left: Boolean) {
        val behindTheRockX = rockX + (if (left) -1 else 1)
        if (map[behindTheRockX, rockY] == MineCell.EMPTY) {
            map[behindTheRockX, rockY] = MineCell.ROCK
            map[rockX, rockY] = MineCell.EMPTY
        }
    }

    private fun inRange(x: Int, y: Int) = x in 0..(width - 1) && y in 0..(height - 1)

    public fun toString(): String {
        val sb = StringBuilder()
        for (y in 0..height - 1) {
            for (x in 0..width - 1) {
                sb.append(this[x, height - y - 1])
            }
            sb.append("\n")
        }
        if (!(water == 0 && floodPeriod == 0 && waterproof == 10)) {
            sb.append("\n")
            sb.append("Water ${water + 1}\n")
            sb.append("Flooding $floodPeriod${ if (floodPeriod != nextFlood) "/" + nextFlood else "" }\n")
            sb.append("Waterproof $waterproof")
        }
        return sb.toString()!!
    }

    public fun copyMapAsDeltaNoCountersSet(): Mine {
        val result = Mine(width, height)
//        val result = Mine(DeltaCellMatrix.create(matrix))
//        result.robotX = $robotX
//        result.robotY = $robotY
//        result.water = water
//        result.floodPeriod = floodPeriod
//        result.nextFlood = nextFlood
//        result.waterproof = waterproof
//        result.lambdaCount = lambdaCount

        return result
    }
}
