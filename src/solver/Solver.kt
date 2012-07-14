package solver

import io.readMine
import java.lang.Thread.currentThread
import model.Mine
import java.util.LinkedList
import model.Robot
import model.RobotStatus
import model.Move
import evaluator.makeMove
import java.util.HashSet
import java.io.PrintWriter
import java.io.FileWriter

public class Solver(val initialMine: Mine) {
    private val workerThread = Thread.currentThread()!!;

    public var answer: RobotState? = null

    val logFile = PrintWriter(FileWriter("log"))

    private fun log(s: String) {
        logFile.println(s)
    }
    private fun log() = log("")

    public fun start() {
        val queue = LinkedList<RobotState>()
        val visited = HashSet<RobotHash>()

        val startRobot = Robot(initialMine, 0, 0, RobotStatus.LIVE, initialMine.waterproof)

        queue.push(RobotState(startRobot, null))
        visited.add(RobotHash.calculate(startRobot))

        while (!queue.isEmpty() && answer == null) {
            val robotState = queue.poll()!!
            val robot = robotState.robot;

            for (move in model.possibleMoves) {
                val copy = Robot(robot.mine.copy(), robot.moveCount, robot.collectedLambdas, robot.status, robot.oxygen)

                val newRobot = makeMove(move, copy)
                val newPath = RobotPath(move, robotState.path)
                val newState = RobotState(newRobot, newPath)
                val newHash = RobotHash.calculate(newRobot)
                if (newRobot.status == RobotStatus.WON) {
                    answer = newState
                    break
                }
                if (newRobot.status == RobotStatus.DEAD) {
                    continue
                }


                if (!visited.contains(newHash)) {
                    log("path: ${newPath}")
                    log("visited: ${visited.size()}")
                    log("status: ${newRobot.status}")
                    log("move: ${move.repr}")
                    log("hash: ${newHash.toString()}")

                    log(newRobot.mine.toString())
                    log()

                    queue.offer(newState)
                    visited.add(newHash)
                }
                2 + 2
            }
        }
        logFile.close()

    }

    public fun interruptAndWriteResult() {
        workerThread.interrupt()
        println(answer?.path)
    }
}
