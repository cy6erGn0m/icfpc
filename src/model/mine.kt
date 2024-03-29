package model

import java.util.HashMap
import util._assert
import java.util.HashSet
import io.serialize

public val validCells: Set<MineCell> = hashSetOf(
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

val allCells: Set<MineCell> = validCells + MineCell.INVALID

val charToState: Map<Char, MineCell> = validCells.toMapBy { it.toChar() }

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
    ROBOT('R', 0),
    ROCK('*', 1),
    CLOSED_LIFT('L', 2),
    EARTH('.', 3),
    WALL('#', 4),
    LAMBDA('\\', 5),
    OPEN_LIFT('O', 6),
    EMPTY(' ', 7),
    INVALID('?', 8),
    TRAMPOLINE('T', 9),
    TARGET('t', 10),
    BEARD('W', 11),
    RAZOR('!', 12),
    LAMBDA_ROCK('@', 13);

    fun isPassable(): Boolean {
        return this == EARTH || this == EMPTY || this == LAMBDA || this == OPEN_LIFT || this == TRAMPOLINE || this == RAZOR
    }
    public fun isRock(): Boolean = this == ROCK || this == LAMBDA_ROCK

    public fun isLift(): Boolean = this == CLOSED_LIFT || this == OPEN_LIFT

    public fun toChar(): Char = representation
    override fun toString(): String = representation.toString()
}

public fun MineCell(representation: Char): MineCell =
        charToState[representation] ?: throw IllegalArgumentException("Unknown cell code: $representation")

val trackedCells: (Int) -> Boolean = { i ->
    i == MineCell.LAMBDA.index || i == MineCell.LAMBDA_ROCK.index || i == MineCell.TRAMPOLINE.index ||
    i == MineCell.OPEN_LIFT.index || i == MineCell.CLOSED_LIFT.index
}

public fun Mine(width: Int, height: Int, trampolinesMap: TrampolinesMap): Mine
        = Mine(ArrayCellMatrix(width, height, trackedCells), trampolinesMap)

public class Mine(private val matrix: CellMatrix, public val trampolinesMap: TrampolinesMap) {

    public val width: Int = matrix.width
    public val height: Int = matrix.height
    public val maxMoveCount: Int
        get() = width * height

    public var water: Int = -1
    public var floodPeriod: Int = 0
    public var nextFlood: Int = 0
    public var waterproof: Int = 10

    public var beardGrowthPeriod: Int = 25
    public var nextBeardGrowth: Int = 25
    public var razors: Int = 0

    fun shouldOpenLift(): Boolean {
        return matrix.positions(MineCell.LAMBDA).size + matrix.positions(MineCell.LAMBDA_ROCK).size == 0
    }

    public var robotX: Int = -1
        get() {
            _assert(field != -1, "Robot position read before initialized")
            return field
        }
        private set(v) {
            _assert(field == -1, "Robot position already set")
            field = v
        }

    public var robotY: Int = -1
        get() {
            _assert(field != -1, "Robot position read before initialized")
            return field
        }
        private set(v) {
            _assert(field == -1, "Robot position already set")
            field = v
        }

    public val robotPos: Point
        get() = Point(robotX, robotY)

    public val liftPos: Point
        get() {
            val closedLift = getPointsOfType(MineCell.CLOSED_LIFT)
            if (!closedLift.isEmpty()) {
                return closedLift.iterator().next()
            }
            val openLift = getPointsOfType(MineCell.OPEN_LIFT)
            if (openLift.isEmpty()) {
                throw IllegalStateException("No lift on the map")
            }
            return openLift.iterator().next()
        }

    public operator fun get(x: Int, y: Int): MineCell {
        if (!inRange(x, y)) {
            return MineCell.INVALID
        }
        return matrix[x, y]
    }

    public operator fun get(pos: Point): MineCell = get(pos.x, pos.y)

    public operator fun set(x: Int, y: Int, v: MineCell) {
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

    public operator fun set(pos: Point, v: MineCell): Unit = set(pos.x, pos.y, v)

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

    override fun toString(): String = serialize()
}

public infix fun Mine.equalsTo(other: Mine): Boolean {
    if (width != other.width) return false
    if (height != other.height) return false
    for (y in 0..height - 1) {
        for (x in 0..width - 1) {
            if (this[x, y] != other[x, y]) return false
        }
    }
    return true
}
