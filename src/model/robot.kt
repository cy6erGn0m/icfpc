package model

import java.util.Arrays

public enum class Move(val repr: Char, val deltaX: Int, val deltaY: Int) {
    WAIT('W', 0, 0),
    ABORT('A', 0, 0),
    SHAVE('S', 0, 0),
    UP('U', 0, 1),
    DOWN('D', 0, -1),
    LEFT('L', -1, 0),
    RIGHT('R', 1, 0);

    public fun nextPosition(curPos: Point): Point = if (this != ABORT) Point(curPos.x + deltaX, curPos.y + deltaY)
                                                    else throw IllegalStateException("Can't call nextPosition for ABORT command")

    override fun toString(): String = "${repr}"
}

val possibleMoves = arrayOf(
        Move.DOWN,
        Move.LEFT,
        Move.RIGHT,
        Move.WAIT,
        Move.UP,
        Move.SHAVE
)

public enum class RobotStatus(val terminated: Boolean) {
    LIVE(false),
    DEAD(true),
    ABORTED(true),
    WON(true);

    override fun toString(): String = name
}

public class Robot(val mine: Mine, val moveCount: Int, val collectedLambdas: Int, val status: RobotStatus,
                   val oxygen: Int, won: Boolean = false) {
    val x: Int = if (!won) mine.robotX else -1
    val y: Int = if (!won) mine.robotY else -1

    public val pos: Point
        get() = Point(x, y)

    override fun toString(): String {
        return "Robot[moveCount=${moveCount},collectedLambdas=${collectedLambdas},status=${status},oxygen=${oxygen}]"
    }
}
