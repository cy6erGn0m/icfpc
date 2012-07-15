package model

import java.util.Collection
import java.util.HashMap
import java.util.Map
import java.util.Set
import util._assert
import java.util.HashSet
import io.serialize

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
        MineCell.TARGET,
        MineCell.BEARD,
        MineCell.RAZOR,
        MineCell.LAMBDA_ROCK

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
    INVALID: MineCell('?', 8)
    TRAMPOLINE: MineCell('T', 9)
    TARGET: MineCell('t', 10)
    BEARD: MineCell('W', 11)
    RAZOR: MineCell('!', 12)
    LAMBDA_ROCK: MineCell('@', 13)

    fun isPassable(): Boolean {
        return this == EARTH || this == EMPTY || this == LAMBDA || this == OPEN_LIFT || this == TRAMPOLINE
    }
    public fun isRock(): Boolean = this == ROCK || this == LAMBDA_ROCK

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

val trackedCells: (Int) -> Boolean = { i ->
    i == MineCell.LAMBDA.index || i == MineCell.LAMBDA_ROCK.index
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

    public var beardGrowthPeriod: Int = 25
    public var nextBeardGrowth: Int = 25
    public var razors: Int = 0

    fun shouldOpenLift(): Boolean {
        return matrix.positions(MineCell.LAMBDA).size() + matrix.positions(MineCell.LAMBDA_ROCK).size() == 0
    }

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

    public val robotPos: Point
        get() = Point(robotX, robotY)

    public fun get(x: Int, y: Int): MineCell {
        if (!inRange(x, y)) {
            return MineCell.INVALID
        }
        return matrix[x, y]
    }

    public fun get(pos: Point): MineCell = get(pos.x, pos.y)

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

    public fun set(pos: Point, v: MineCell): Unit = set(pos.x, pos.y, v)

    public fun moveRobot(oldPos: Point, newPos: Point) {
        if (matrix[oldPos] != MineCell.ROBOT) {
            throw IllegalStateException("No robot at $oldPos")
        }
        if (!matrix[newPos].isPassable()) {
            throw IllegalStateException("Map is not passable at $newPos")
        }
        matrix[oldPos] = MineCell.EMPTY
        //if robot enters lift it just disappears
        if (matrix[newPos] != MineCell.OPEN_LIFT) {
            matrix[newPos] = MineCell.ROBOT
        }
    }

    public fun tryMoveRock(rockX: Int, rockY: Int, left: Boolean, rockType: MineCell) {
        _assert(rockType.isRock())
        val behindTheRockX = rockX + (if (left) -1 else 1)
        if (matrix[behindTheRockX, rockY] == MineCell.EMPTY) {
            matrix[behindTheRockX, rockY] = rockType
            matrix[rockX, rockY] = MineCell.EMPTY
        }
    }

    private fun inRange(x: Int, y: Int) = x in 0..(width - 1) && y in 0..(height - 1)

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
        copy.beardGrowthPeriod = beardGrowthPeriod
        copy.nextBeardGrowth = nextBeardGrowth
        copy.razors = razors
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

    public fun toString(): String {
        return serialize()
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