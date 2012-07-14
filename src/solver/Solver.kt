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
import util._assert
import util.Logger
import evaluator.mineUpdateWithFullCopy

public val solverUpdate: (Mine) -> Mine = {m -> mineUpdateWithFullCopy(m)}

private val RESULT_LIMIT = 20
private val DEPTH = 15

public class Solver(val initialMine: Mine) {
    private val workerThread = Thread.currentThread()!!;
    public var answer: RobotState? = null
    private val logger = Logger("process_log")

    fun makeMove(state: RobotState, move: Move): RobotState {
        val robot = state.robot
        val copy = Robot(robot.mine.copy(), robot.moveCount, robot.collectedLambdas, robot.status, robot.oxygen)
        val newRobot = makeMove(move, copy, solverUpdate)
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

    fun processStates(queue: StateQueue, resultsLimit: Int, depth: Int?): BestRobotStates {
        val states = BestRobotStates({score(it)}, resultsLimit)
        val initialMoves = queue.peek().robot.moveCount
        while (!queue.isEmpty()) {
            val robotState = queue.pop()

            for (move in model.possibleMoves) {
                val newState = makeMove(robotState, move)
                if (newState.robot.status == RobotStatus.DEAD) {
                    continue
                }
                if (depth != null && newState.robot.moveCount == initialMoves + depth) {
                    continue
                }
                if (newState.robot.status == RobotStatus.WON) {
                    answer = newState
                    break
                }

                if (!queue.containsSimilar(newState)) {
                    states.add(newState)
                    logger.logNewState(queue, newState, move)
                    queue.push(newState)
                }
                2 + 2
            }
        }
        return states
    }

    public fun start() {
        val startRobot = Robot(initialMine, 0, 0, RobotStatus.LIVE, initialMine.waterproof)

        var currentStates : List<RobotState> = arrayList(RobotState(startRobot, null))
        val queue = StateQueue()
        while (answer == null) {
            for (state in currentStates) {
                queue.push(state)
            }
            val robotStates = processStates(queue, RESULT_LIMIT, DEPTH)
            currentStates = robotStates.getBestStates()
            queue.clearQueue()
        }
    }

    public fun interruptAndWriteResult() {
        workerThread.interrupt()
        println(answer?.path)
        logger.close()
    }
}
