package model


enum class Move(val repr: Char, val deltaX: Int = 0, val deltaY: Int = 0) {
    WAIT: Move('W')
    ABORT: Move('A')
    UP: Move('U', 0, 1)
    DOWN: Move('D', 0 , -1)
    LEFT: Move('L', -1, 0)
    RIGHT: Move('R', 1, 0)
}

enum class RobotStatus(val terminated: Boolean) {
    LIVE: RobotStatus(false)
    DEAD: RobotStatus(true)
    ABORTED: RobotStatus(true)
    WON: RobotStatus(true)
}

class Robot(val mine: Mine, val moveCount: Int, val lambdas: Int, val status: RobotStatus) {
    val x: Int = mine.robotX
    val y: Int = mine.robotY
}