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
import model.MineCell
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

        var lambdas = state.robot.mine.getPointsOfType(MineCell.LAMBDA)
        var minDist = Integer.MAX_VALUE
        for (lambda in lambdas) {
            minDist = Math.min(minDist,
                    Math.abs(lambda.x - state.robot.x) + Math.abs(lambda.y - state.robot.y))
        }

        return ans - 100*minDist
    }

    fun updateAnswer(state: RobotState) {
        if (answer == null || score(state) > score(answer!!)) {
            answer = state
        }
    }

    fun isGoodEnough(state: RobotState?): Boolean {
        if (state == null)
            return false
        if (state.robot.status == RobotStatus.WON)
            return true
        if (state.robot.moveCount == state.robot.mine.maxMoveCount)
            return true
        return false
    }


    var iteration = 0

    fun processStates(queue: StateQueue, resultsLimit: Int, depth: Int?): BestRobotStates {
        val states = BestRobotStates({score(it)}, resultsLimit)
        val initialMoves = queue.peek().robot.moveCount
        while (!queue.isEmpty()) {
            val robotState = queue.pop()

            for (move in model.possibleMoves) {
                val newState = makeMove(robotState, move)
                updateAnswer(newState)
                if (newState.robot.status == RobotStatus.DEAD) {
                    continue
                }
                if (depth != null && newState.robot.moveCount == initialMoves + depth) {
                    continue
                }
                if (newState.robot.status == RobotStatus.WON) {
                    break
                }

                if (!queue.containsSimilar(newState)) {
                    if (depth != null && newState.robot.moveCount == initialMoves + depth - 1) {
                        states.add(newState)
                    }
                    //logger.logNewState(queue, newState, move)
                    queue.push(newState)
                }
                2 + 2
            }
        }

        logger.log("Best states ${++iteration}")
        for (state in states.bestStates) {
            logger.logNewState(queue, state.state, Move.ABORT)
        }
        return states
    }

    public fun start() {
        val startRobot = Robot(initialMine, 0, 0, RobotStatus.LIVE, initialMine.waterproof)

        var currentStates : List<RobotState> = arrayList(RobotState(startRobot, null))
        val queue = StateQueue()
        while (!isGoodEnough(answer) && !currentStates.isEmpty()) {
            logger.log("Current: ${currentStates.size()} Answer: ${answer?.path}")
            for (state in currentStates) {
                queue.push(state)
            }
            val robotStates = processStates(queue, RESULT_LIMIT, DEPTH)
            currentStates = robotStates.getBestStates()
            logger.log("Current states: ${currentStates.size}")
            queue.clearQueue()
        }
    }

    public fun interruptAndWriteResult() {
        workerThread.interrupt()
        println(answer?.path)
        logger.close()
    }
}
