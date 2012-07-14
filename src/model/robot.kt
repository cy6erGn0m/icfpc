package model


public enum class Move(val repr: Char, val deltaX: Int = 0, val deltaY: Int = 0) {
    WAIT: Move('W')
    ABORT: Move('A')
    UP: Move('U', 0, 1)
    DOWN: Move('D', 0 , -1)
    LEFT: Move('L', -1, 0)
    RIGHT: Move('R', 1, 0)
}

public enum class RobotStatus(val terminated: Boolean) {
    LIVE: RobotStatus(false)
    DEAD: RobotStatus(true)
    ABORTED: RobotStatus(true)
    WON: RobotStatus(true)
}

public class Robot(val mine: Mine, val moveCount: Int, val collectedLambdas: Int, val status: RobotStatus,
                   val oxygen: Int, won: Boolean = false) {
    val x: Int = if (!won) mine.robotX else -1
    val y: Int = if (!won) mine.robotY else -1
}