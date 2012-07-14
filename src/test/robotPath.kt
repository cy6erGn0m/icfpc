package test

import solver.RobotPath
import model.Move

fun main(args: Array<String>) {
    val p1 = RobotPath(Move.DOWN, null)
    val p2 = RobotPath(Move.ABORT, p1)
    val p3 = RobotPath(Move.UP, p2)

    println(p1)
    println(p2)
    println(p3)
}