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
import java.util.Collection
import java.util.List

public class Solver(val initialMine: Mine) {
    private val workerThread = Thread.currentThread()!!;

    public var answer: RobotState? = null

    val logFile = PrintWriter(FileWriter("log"))

    fun log(s: String) {
        logFile.println(s)
    }
    fun log() = log("")


    fun makeMove(state: RobotState, move: Move): RobotState {
        val robot = state.robot
        val copy = Robot(robot.mine.copy(), robot.moveCount, robot.collectedLambdas, robot.status, robot.oxygen)
        val newRobot = makeMove(move, copy)
        val newPath = RobotPath(move, state.path)
        return RobotState(newRobot, newPath)
    }

    fun score(state: RobotState): Int {
        var ans = 50 * state.robot.collectedLambdas - state.robot.moveCount
        if (state.robot.status == RobotStatus.WON) {
            ans += 25 * state.robot.collectedLambdas
        }
        return ans
    }

    fun logNewState(queue: StateQueue, newState: RobotState, move: Move) {
        log("path: ${newState.path}")
        log("visited: ${queue.visited.size()}")
        log("status: ${newState.robot.status}")
        log("move: ${move.repr}")
        log("hash: ${RobotHash.calculate(newState.robot)}")
        log(newState.robot.mine.toString())
        log()
    }


    fun processStates(queue: StateQueue, resultsLimit: Int): BestRobotStates {
        val states = BestRobotStates({score(it)}, resultsLimit)
        while (!queue.isEmpty() && answer == null) {
            val robotState = queue.pop()

            for (move in model.possibleMoves) {
                val newState = makeMove(robotState, move)
                states.add(newState)
                if (newState.robot.status == RobotStatus.WON) {
                    answer = newState
                    break
                }
                if (newState.robot.status == RobotStatus.DEAD) {
                    continue
                }

                if (!queue.containsSimilar(newState)) {
                    // logNewState(queue, newState, move)
                    queue.push(newState)
                }
                2 + 2
            }
        }
        return states
    }

    public fun start() {
        val startRobot = Robot(initialMine, 0, 0, RobotStatus.LIVE, initialMine.waterproof)
        val queue = StateQueue()

        queue.push(RobotState(startRobot, null))

        answer = processStates(queue, 1).getBestState()
        logFile.close()
    }

    public fun interruptAndWriteResult() {
        workerThread.interrupt()
        println(answer?.path)
    }
}
