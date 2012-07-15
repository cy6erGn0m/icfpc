package emulator


import java.io.File
import io.readMine
import io.serialize
import testUtil.readMovesFromString
import evaluator.makeMove
import model.Robot
import solver.RobotState
import model.RobotStatus
import solver.solverUpdate
import model.Mine
import evaluator.countScore

fun main(args: Array<String>) {
    val file = File("mines/emulator/map.map")
    val mine = readMine(file)
    var resMine: Mine = mine
    var robot = Robot(mine, 0, 0, RobotStatus.LIVE, 10)
    println(mine.serialize())
    val path = StringBuilder()
    while (true) {
        print("> ")
        val line = readLine()
        if (line == null) {
            println("line == null")
            return
        }
        path.append(line)
        val moves = readMovesFromString(line)
        for (move in moves) {
            robot = makeMove(move, robot, solverUpdate)
            if (robot.status != RobotStatus.LIVE) {
                println(resMine.serialize())
                println("Robot is ${robot.status}")
                println("Score: ${countScore(robot)}")
                println("Path: $path")
                return
            }
            resMine = robot.mine
        }
        println(resMine.serialize())
    }
}