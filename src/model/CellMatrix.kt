package model

import java.util.HashMap
import util._assert
import java.util.HashSet
import util.DumbSet
import java.util.ArrayList

class Point(val x: Int, val y: Int) {
    override fun equals(other: Any?): Boolean {
        return other is Point && x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        return x + 13 * y
    }

    public fun above(): Point {
        return Point(x, y + 1)
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}

public abstract class CellMatrix(
        val width: Int,
        val height: Int,
        val cellIndicesToTrack: (Int) -> Boolean
) {

    public abstract operator fun get(x: Int, y: Int): MineCell
    public abstract fun positions(cell: MineCell): Collection<Point>
    protected abstract fun replace(x: Int, y: Int, oldValue: MineCell, newValue: MineCell)

    public operator fun set(x: Int, y: Int, v: MineCell) {
        val oldValue = get(x, y)
        replace(x, y, oldValue, v)
    }

    public operator fun get(pos: Point): MineCell = get(pos.x, pos.y)
    public operator fun set(pos: Point, v: MineCell): Unit = set(pos.x, pos.y, v)
}

public abstract class AbstractCellTrackingMatrix(
        width: Int, height: Int,
        cellIndicesToTrack: (Int) -> Boolean,
        initialPositions: (Int) -> MutableSet<Point> = {HashSet<Point>()}
    ) : CellMatrix(width, height, cellIndicesToTrack) {

    private val positions = Array<MutableSet<Point>>(allCells.size) {
        cellIndex ->
        if (cellIndicesToTrack(cellIndex)) {
            initialPositions(cellIndex)
        }
        else {
            DumbSet
        }
    }

    protected final override fun replace(x: Int, y: Int, oldValue: MineCell, newValue: MineCell) {
        val p = Point(x, y)
        positions[oldValue.index].remove(p)
        positions[newValue.index].add(p)
        doSet(x, y, newValue)
    }

    protected abstract fun doSet(x: Int, y: Int, newValue: MineCell)

    public override fun positions(cell: MineCell): Collection<Point> {
        _assert(cellIndicesToTrack(cell.index), "Not tracked: $cell")
        return positions[cell.index]
    }
}

//public abstract class AbstractCellMatrixWithStupidCellTracking(
//        width: Int, height: Int,
//        cellIndicesToTrack: (Int) -> Boolean
//    ) : CellMatrix(width, height, cellIndicesToTrack) {
//
//    protected final override fun replace(x: Int, y: Int, oldValue: MineCell, newValue: MineCell) {
//        doSet(x, y, newValue)
//    }
//
//    protected abstract fun doSet(x: Int, y: Int, newValue: MineCell)
//
//    public override fun positions(cell: MineCell): Collection<Point> {
//        _assert(cellIndicesToTrack(cell.index), "Not tracked: $cell")
//        val list = ArrayList<Point>()
//        for (y in 0..height - 1)
//            for (x in 0..width - 1)
//                if (this[x, y] == cell) {
//                    list.add(Point(x, y))
//                }
//        return list
//    }
//}

public class ArrayCellMatrix(
        width: Int, height: Int,
        cellIndicesToTrack: (Int) -> Boolean
    ) : AbstractCellTrackingMatrix(width, height, cellIndicesToTrack) {

    private val map = Array<MineCell>(width * height) { MineCell.INVALID }

    public override fun get(x: Int, y: Int): MineCell {
        return map[x + y * width]
    }

    protected override fun doSet(x: Int, y: Int, newValue: MineCell) {
        map[x + y * width] = newValue
    }
}

public class DeltaCellMatrix internal constructor(
        private val baseline: CellMatrix
//    ) : AbstractCellMatrixWithStupidCellTracking(baseline.width, baseline.height, baseline.cellIndicesToTrack) {
    ) : AbstractCellTrackingMatrix(baseline.width, baseline.height, baseline.cellIndicesToTrack, {
            index ->
            if (baseline.cellIndicesToTrack(index))
                HashSet<Point>(baseline.positions(indexToCell[index]))
            else
                DumbSet
    }) {

    companion object {
        fun create(baseline: CellMatrix): DeltaCellMatrix {
            if (baseline is DeltaCellMatrix) {
                val effectiveBaseline = baseline.baseline
                val result = DeltaCellMatrix(effectiveBaseline)
                result.map.putAll(baseline.map)
                return result
            }
            return DeltaCellMatrix(baseline)
        }
    }

    private val map = HashMap<Point, MineCell>()

    public val deltaSize: Int
        get() = map.size

    public override fun get(x: Int, y: Int): MineCell {
        return map[Point(x, y)] ?: baseline[x, y]
    }

    protected override fun doSet(x: Int, y: Int, v: MineCell) {
        if (this[x, y] != v) {
//            println("($x, $y) [${this[x, y]}]= $v")
            map[Point(x, y)] = v
        }
    }
}

public operator fun CellMatrix.contains(cell: MineCell): Boolean {
//    for (px in 0..width - 1) {
//        for (py in 0..height - 1) {
//            if (this[px, py] == cell) return true
//        }
//    }
//    return false
    return !positions(cell).isEmpty()
}

public fun CellMatrix.count(cell: MineCell): Int {
//    var result = 0
//    for (px in 0..width - 1) {
//        for (py in 0..height - 1) {
//            if (this[px, py] == cell) result++
//        }
//    }
//    return result
    return positions(cell).size
}
