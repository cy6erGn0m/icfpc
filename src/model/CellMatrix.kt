package model

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