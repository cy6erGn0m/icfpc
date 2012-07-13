package model

val validStates = hashSet(
                    CellState.ROBOT,
                    CellState.ROCK,
                    CellState.CLOSED_LIFT,
                    CellState.EARTH,
                    CellState.WALL,
                    CellState.LAMBDA,
                    CellState.OPEN_LIFT,
                    CellState.EMPTY
                )

val allStates = validStates + CellState.INVALID

enum class CellState(val representation : String) {
    ROBOT: CellState("R")
    ROCK: CellState("*")
    CLOSED_LIFT: CellState("L")
    EARTH: CellState(".")
    WALL: CellState("#")
    LAMBDA: CellState("\\")
    OPEN_LIFT: CellState("O")
    EMPTY: CellState(" ")
    INVALID: CellState("!")

    public fun fromString(s: String) : CellState {

    }

    public fun toString(): String = representation
}

// m lines
// n columns
class Map(val width: Int, val height: Int) {
    private val map: Array<Array<CellState>> = Array(width) {
        Array<CellState>(height) { CellState.INVALID }
    }

    public fun get(x: Int, y: Int) : CellState {
        return map[x][y]
    }

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