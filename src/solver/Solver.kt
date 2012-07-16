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
import evaluator.countScore
import java.util.ArrayList

public val solverUpdate: (Mine) -> Mine = {m -> mineUpdateWithFullCopy(m)}

private val RESULT_LIMIT = 20
private val DEPTH = 10

trait SolverFramework {
    val needToTerminateFlag: Boolean
    val logger: Logger
    val iteration: Int
    fun iterateQueue(queue: StateQueue, depth: Int, acceptor: StateAcceptor)
}

public class Solver(val initialMine: Mine, val scorer: Scorer, val highScore: Int? = null) : SolverFramework {
    private val workerThread = Thread.currentThread()!!;
    override val logger = Logger("process_log", false)

    public var answer: RobotState? = null
    override volatile var needToTerminateFlag: Boolean = false
    override var iteration = 0


    private val shortTermExplorer: StateExplorer = SimpleStateExplorer(this)

    fun makeMove(state: RobotState, move: Move): RobotState {
        val robot = state.robot
        val copy = Robot(robot.mine.copy(), robot.moveCount, robot.collectedLambdas, robot.status, robot.oxygen)
        val newRobot = makeMove(move, copy, solverUpdate)
        val newPath = RobotPath(move, state.path)
        return RobotState(newRobot, newPath, scorer)
    }


    fun updateAnswer(state: RobotState) {
        var newState = state
        if (state.robot.status == RobotStatus.LIVE) {
            newState = makeMove(state, Move.ABORT)
        }
        if (answer == null || countScore(newState.robot) > countScore(answer!!.robot)) {
            answer = newState
        }
    }

    fun terminate() {
        needToTerminateFlag = true
    }

    fun needToTerminate(): Boolean {
        if (needToTerminateFlag)
            return true
        if (answer == null)
            return false
        if (answer!!.robot.moveCount == answer!!.robot.mine.maxMoveCount)
            return true
        if (highScore != null && countScore(answer!!.robot) >= highScore) {
            return true
        }
        return false
    }

    override fun iterateQueue(queue: StateQueue, depth: Int, acceptor: StateAcceptor) {
        val initialMoves = queue.peek().robot.moveCount
        while (!queue.isEmpty()) {
            if (needToTerminateFlag) {
                // Doesn't mater waht to return
                return
            }
            val robotState = queue.pop()

            if (robotState.robot.status.terminated) continue

            for (move in model.possibleMoves) {
                val newState = makeMove(robotState, move)
                updateAnswer(newState)
                if (newState.robot.status == RobotStatus.DEAD) {
                    continue
                }
                if (newState.robot.moveCount == initialMoves + depth) {
                    acceptor.add(newState)
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
        _assert(queue.isEmpty(), "...")
    }

    public fun start() {
        val startRobot = Robot(initialMine, 0, 0, RobotStatus.LIVE, initialMine.waterproof)

        var currentStates : Collection<RobotState> = arrayList(RobotState(startRobot, null, scorer))
        val queue = StateQueue()
        while (!needToTerminate() && !currentStates.isEmpty()) {
            logger.log("Current: ${currentStates.size()} Answer: ${answer?.path}")
//            for (state in currentStates) {
//                queue.push(state)
//            }
            val robotStates = shortTermExplorer.processStates(currentStates, queue, RESULT_LIMIT, DEPTH)
            iteration++
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
