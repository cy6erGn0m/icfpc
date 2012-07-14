package model

import java.util.Map
import java.util.HashMap

class Point(val x: Int, val y: Int) {
    public fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other.javaClass == javaClass<Point>()) return false
        val otherPoint = other as Point
        return x == otherPoint.x && y == otherPoint.y
    }

    public fun hashCode(): Int {
        return x + 13 * y;
    }

    public fun toString(): String {
        return "($x, $y)"
    }
}

public abstract class CellMatrix(val width: Int, val height: Int) {
    public abstract fun get(x: Int, y: Int): MineCell
    public abstract fun set(x: Int, y: Int, v: MineCell)
}

public class ArrayCellMatrix(width: Int, height: Int) : CellMatrix(width, height) {
    private val map: Array<MineCell> = Array(width * height) { MineCell.INVALID }

    public override fun get(x: Int, y: Int): MineCell {
        return map[x + y * width]
    }

    public override fun set(x: Int, y: Int, v: MineCell) {
        map[x + y * width] = v
    }
}

public class DeltaCellMatrix(private val baseline: CellMatrix) : CellMatrix(baseline.width, baseline.height) {
    private val map = HashMap<Point, MineCell>()

    public val deltaSize: Int
        get() = map.size()

    public override fun get(x: Int, y: Int): MineCell {
        val value = map.get(Point(x, y))
        if (value == null) {
            return baseline[x, y]
        }
        return value
    }

    public override fun set(x: Int, y: Int, v: MineCell) {
        if (this[x, y] != v) {
            map[Point(x, y)] = v
        }
    }
}