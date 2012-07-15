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
import score.Scorer
import model.Point

public val solverUpdate: (Mine) -> Mine = {m -> mineUpdateWithFullCopy(m)}

private val RESULT_LIMIT = 20
private val DEPTH = 10

public class Solver(val initialMine: Mine, val scorer: Scorer) {
    private val workerThread = Thread.currentThread()!!;
    private val logger = Logger("process_log")

    public var answer: RobotState? = null
    public volatile var needToTerminateFlag: Boolean = false

    fun makeMove(state: RobotState, move: Move): RobotState {
        val robot = state.robot
        val copy = Robot(robot.mine.copy(), robot.moveCount, robot.collectedLambdas, robot.status, robot.oxygen)
        val newRobot = makeMove(move, copy, solverUpdate)
        val newPath = RobotPath(move, state.path)
        return RobotState(newRobot, newPath, scorer)
    }




    fun updateAnswer(state: RobotState) {
        if (answer == null || state.score > answer!!.score) {
            answer = state
        }
    }

    fun needToTerminate(): Boolean {
        if (needToTerminateFlag)
            return true
        if (answer == null)
            return false
        if (answer!!.robot.status == RobotStatus.WON)
            return true
        if (answer!!.robot.moveCount == answer!!.robot.mine.maxMoveCount)
            return true
        return false
    }


    var iteration = 0

    fun processStates(queue: StateQueue, resultsLimit: Int, depth: Int?): BestRobotStates {
        val states = BestRobotStates(resultsLimit)
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
                    states.add(newState)
                    continue
                }
                if (newState.robot.status == RobotStatus.WON) {
                    break
                }

                if (!queue.containsSimilar(newState)) {
                    //logger.logNewState(queue, newState, move)
                    queue.push(newState)
                }
                2 + 2
            }
        }

        logger.log("Best states ${++iteration}")
        for (state in states.bestStates) {
            logger.logNewState(queue, state, Move.ABORT)
        }
        return states
    }

    public fun start() {
        val startRobot = Robot(initialMine, 0, 0, RobotStatus.LIVE, initialMine.waterproof)

        var currentStates : Collection<RobotState> = arrayList(RobotState(startRobot, null, scorer))
        val queue = StateQueue()
        while (!needToTerminate() && !currentStates.isEmpty()) {
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
