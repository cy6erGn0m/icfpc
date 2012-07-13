package model


enum class Move(val repr: Char) {
    WAIT: Move('W')
    ABORT: Move('A')
    UP: Move('U')
    DOWN: Move('D')
    LEFT: Move('L')
    RIGHT: Move('R')
}

class Robot(val mine: Mine, val points: Int, val lambdas: Int) {
    val x: Int = mine.robotX
    val y: Int = mine.robotY
}