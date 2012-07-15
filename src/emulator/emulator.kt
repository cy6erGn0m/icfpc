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
    val file = File("mines/default/horock/horock2.map")
    val initialMine = readMine(file)
    var resMine: Mine = initialMine
    var robot = Robot(initialMine, 0, 0, RobotStatus.LIVE, 10)
    println(initialMine.serialize())
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
                println(
                        """
TEST
$path
${initialMine.serialize()}
-END MINE
${countScore(robot)}
${robot.status}
${resMine.serialize()}
-END MINE
END TEST
""")
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