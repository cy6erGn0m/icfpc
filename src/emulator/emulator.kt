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
    val pathToMap = if (args.isEmpty()) {
        print("Enter path to map: ")
        System.`in`.reader().buffered().readLine()!!.trim()
    } else args[0]

    val file = File(pathToMap)
    val initialMine = readMine(file)
    var resMine: Mine = initialMine
    var robot = Robot(initialMine.copy(), 0, 0, RobotStatus.LIVE, 10)
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
        val moves = try {
            readMovesFromString(line)
        } catch (e: IllegalArgumentException) {
            println(e.message)
            continue
        }
        for (move in moves) {
            robot = makeMove(move, robot, solverUpdate)
            resMine = robot.mine
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
        }
        println(resMine.serialize())
    }
}