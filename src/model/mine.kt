package model

import java.util.Collection
import java.util.HashMap
import java.util.Map
import java.util.Set
import util._assert
import java.util.HashSet

public val validCells: Set<MineCell> = hashSet(
        MineCell.ROBOT,
        MineCell.ROCK,
        MineCell.CLOSED_LIFT,
        MineCell.EARTH,
        MineCell.WALL,
        MineCell.LAMBDA,
        MineCell.OPEN_LIFT,
        MineCell.EMPTY,
        MineCell.TRAMPOLINE,
        MineCell.TARGET

)

val allCells: Set<MineCell> = run {
    val r = HashSet(validCells)
    r.add(MineCell.INVALID)
    r
}

val charToState: java.util.Map<Char, MineCell> = run {
    val map = HashMap<Char, MineCell>()
    for (cs in validCells) {
        map[cs.toChar()] = cs
    }
    map
}

val indexToCell = run {
    val r = Array<MineCell>(allCells.size) {
        i -> MineCell.INVALID
    }
    for (c in allCells) {
        r[c.index] = c
    }
    r
}

enum class MineCell(
        private val representation: Char,
        public val index: Int
) {
    ROBOT: MineCell('R', 0)
    ROCK: MineCell('*', 1)
    CLOSED_LIFT: MineCell('L', 2)
    EARTH: MineCell('.', 3)
    WALL: MineCell('#', 4)
    LAMBDA: MineCell('\\', 5)
    OPEN_LIFT: MineCell('O', 6)
    EMPTY: MineCell(' ', 7)
    TRAMPOLINE: MineCell('T', 9)
    TARGET: MineCell('t', 10)
    INVALID: MineCell('!', 8)

    fun isPassable(): Boolean {
        return this == EARTH || this == EMPTY || this == LAMBDA || this == OPEN_LIFT
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

val trackedCells: (Int) -> Boolean = {
    i ->
    i == MineCell.LAMBDA.index
}

public fun Mine(width: Int, height: Int, public val trampolinesMap: TrampolinesMap): Mine
        = Mine(ArrayCellMatrix(width, height, trackedCells), trampolinesMap)

public class Mine(private val matrix: CellMatrix, public val trampolinesMap: TrampolinesMap) {

    public val width: Int = matrix.width
    public val height: Int = matrix.height
    public val maxMoveCount: Int
        get() = width * height

    public var water: Int = 0
    public var floodPeriod: Int = 0
    public var nextFlood: Int = 0
    public var waterproof: Int = 10

    public val lambdaCount: Int
        get() = matrix.positions(MineCell.LAMBDA).size()

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
//            MineCell.LAMBDA -> lambdaCount++
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
        matrix[oldX, oldY] = MineCell.EMPTY
//        if (matrix[newX, newY] == MineCell.LAMBDA) {
//            lambdaCount--
//        }
        //if robot enters lift it just disappears
        if (matrix[newX, newY] != MineCell.OPEN_LIFT) {
            matrix[newX, newY] = MineCell.ROBOT
        }
    }

    public fun tryMoveRock(rockX: Int, rockY: Int, left: Boolean) {
        val behindTheRockX = rockX + (if (left) -1 else 1)
        if (matrix[behindTheRockX, rockY] == MineCell.EMPTY) {
            matrix[behindTheRockX, rockY] = MineCell.ROCK
            matrix[rockX, rockY] = MineCell.EMPTY
        }
    }

    private fun inRange(x: Int, y: Int) = x in 0..(width - 1) && y in 0..(height - 1)

    public fun toString(): String {
        val sb = StringBuilder()
        var unresolvedTrampolinesToLocations = HashMap<Char, Point>()
        for (yy in 0..height - 1) {
            val y = height - yy - 1
            for (x in 0..width - 1) {
                val cell = this[x, y]
                if (cell == MineCell.TRAMPOLINE || cell == MineCell.TARGET) {
                    val location = Point(x, y)
                    val id = trampolinesMap.getId(location)
                    if (cell == MineCell.TRAMPOLINE) {
                        unresolvedTrampolinesToLocations[id] = location
                    }
                    sb.append(id)
                }
                else {
                    sb.append(cell)
                }
            }
            sb.append("\n")
        }

        if (!(water == 0 && floodPeriod == 0 && waterproof == 10)) {
            sb.append("\n")
            sb.append("Water ${water + 1}\n")
            sb.append("Flooding $floodPeriod${ if (floodPeriod != nextFlood) "/" + nextFlood else "" }\n")
            sb.append("Waterproof $waterproof\n")
        }

        if (!unresolvedTrampolinesToLocations.isEmpty()) {
            sb.append("\n")
            for (trampolineId in 'A'..'I') {
                val trampolineLocation = unresolvedTrampolinesToLocations[trampolineId]
                if (trampolineLocation == null) continue
                val targetLocation = trampolinesMap.getTarget(trampolineLocation)
                val targetId = trampolinesMap.getId(targetLocation)
                sb.append("Trampoline $trampolineId targets $targetId\n")
            }
        }
        return sb.toString()!!
    }

    public fun copy(): Mine {
        val copy = Mine(width, height, trampolinesMap)
        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                copy[x, y] = this[x, y]
            }
        }
        copy.water = water
        copy.floodPeriod = floodPeriod
        copy.nextFlood = nextFlood
        copy.waterproof = waterproof
        return copy
    }

    public fun copyMapAsDeltaNoCountersSet(copyMatrix: (CellMatrix) -> CellMatrix): Mine {
        val result = Mine(copyMatrix(matrix), trampolinesMap)
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

    public fun getPointsOfType(mineCell: MineCell): Collection<Point> {
        return matrix.positions(mineCell)
    }
}

public fun Mine.equalsTo(other: Mine): Boolean {
    if (width != other.width) return false
    if (height != other.height) return false
    for (y in 0..height - 1) {
        for (x in 0..width - 1) {
            if (this[x, y] != other[x, y]) return false
        }
    }
    return true
}