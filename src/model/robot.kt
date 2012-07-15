package model

import java.util.Arrays

public enum class Move(val repr: Char, val deltaX: Int = 0, val deltaY: Int = 0) {
    WAIT: Move('W')
    ABORT: Move('A')
    SHAVE: Move('S')
    UP: Move('U', 0, 1)
    DOWN: Move('D', 0, -1)
    LEFT: Move('L', -1, 0)
    RIGHT: Move('R', 1, 0)

    public fun toString(): String = "${repr}"
    public fun nextPosition(curPos: Point): Point = if (this != ABORT) Point(curPos.x + deltaX, curPos.y + deltaY)
                                                    else throw IllegalStateException("Can't call nextPosition for ABORT command")
}

val possibleMoves = array(
        Move.DOWN,
        Move.LEFT,
        Move.RIGHT,
        Move.WAIT,
        Move.UP,
        Move.SHAVE
)

public enum class RobotStatus(val terminated: Boolean, val name: String) {
    LIVE: RobotStatus(false, "LIVE")
    DEAD: RobotStatus(true, "DEAD")
    ABORTED: RobotStatus(true, "ABORTED")
    WON: RobotStatus(true, "WON")

    public fun toString(): String = name
}

public class Robot(val mine: Mine, val moveCount: Int, val collectedLambdas: Int, val status: RobotStatus,
                   val oxygen: Int, won: Boolean = false) {
    val x: Int = if (!won) mine.robotX else -1
    val y: Int = if (!won) mine.robotY else -1

    public fun toString(): String {
        return "Robot[moveCount=${moveCount},collectedLambdas=${collectedLambdas},status=${status},oxygen=${oxygen}]"
    }

    public val pos: Point
        get() = Point(x, y)
}