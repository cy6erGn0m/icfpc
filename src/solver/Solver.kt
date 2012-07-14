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

public class Solver(val initialMine: Mine) {
    private val workerThread = Thread.currentThread()!!;

    public var answer: RobotState? = null

    public fun start() {
        val queue = LinkedList<RobotState>()
        val visited = HashSet<RobotHash>()

        val startRobot = Robot(initialMine, 0, 0, RobotStatus.LIVE)

        queue.push(RobotState(startRobot, null))
        visited.add(RobotHash.calculate(startRobot))

        while (!queue.isEmpty()) {
            val robotState = queue.poll()!!
            val robot = robotState.robot;

            for (move in model.possibleMoves) {
                val copy = Robot(robot.mine.copy(), robot.moveCount, robot.collectedLambdas, robot.status)

                println("path: ${robotState.path}")
                println("visited: ${visited.size()}")
                println("status: ${robot.status}")
                println("move: ${move.repr}")
                println("hash: ${RobotHash.calculate(robot).hash}")
                println(robot.mine)
                println()

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
                    queue.push(newState)
                    visited.add(newHash)
                }
                System.err.println()
            }
        }
    }

    public fun interruptAndWriteResult() {
        workerThread.interrupt()
        println(answer?.path)
    }
}
