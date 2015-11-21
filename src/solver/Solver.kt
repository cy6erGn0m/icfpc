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
import util._assert
import util.Logger
import model.MineCell
import evaluator.mineUpdateWithFullCopy
import score.Scorer
import model.Point
import evaluator.countScore
import java.util.ArrayList

public val solverUpdate: (Mine) -> Mine = {m -> mineUpdateWithFullCopy(m)}

interface SolverFramework {
    val needToTerminateFlag: Boolean
    val logger: Logger
    val iteration: Int
    fun iterateQueue(queue: StateQueue, depth: Int, acceptor: StateAcceptor)
}

public class Solver(val initialMine: Mine, val scorer: Scorer, val highScore: Int? = null) : SolverFramework {
//    private val workerThread = Thread.currentThread()!!;
    override val logger = Logger("process_log", false)
    private val depth = Math.max(10 - initialMine.maxMoveCount / 1000, 2)
    private val resultLimit = 20

    public @Volatile var answer: RobotState? = null
    override @Volatile var needToTerminateFlag: Boolean = false
    override var iteration = 0


    private val shortTermExplorer: StateExplorer = SimpleStateExplorer(this)

    fun makeMove(state: RobotState, move: Move): RobotState {
        val robot = state.robot
        val copy = Robot(robot.mine.copy(), robot.moveCount, robot.collectedLambdas, robot.status, robot.oxygen)
        val newRobot = makeMove(move, copy, solverUpdate)
        val newPath = RobotPath(move, state.path)
        return RobotState(newRobot, newPath, scorer)
    }

    fun isValidMove(state: RobotState, move: Move) : Boolean {
        val mine = state.robot.mine

        if (move == Move.DOWN || move == Move.UP || move == Move.LEFT || move == Move.RIGHT) {
            val nextPos = move.nextPosition(state.robot.pos)
            if (mine[nextPos].isPassable()) {
                return true
            }

            val nextPosCell = mine[nextPos]
            if (nextPosCell == MineCell.WALL || nextPosCell == MineCell.CLOSED_LIFT) {
                return false
            }

            // Don't check rocks
            return true
        }

        if (move == Move.SHAVE) {
            val robotX = state.robot.pos.x
            val robotY = state.robot.pos.y
            if (mine.get(robotX - 1, robotY - 1) == MineCell.BEARD) return true
            if (mine.get(robotX - 1, robotY) == MineCell.BEARD) return true
            if (mine.get(robotX - 1, robotY + 1) == MineCell.BEARD) return true
            if (mine.get(robotX, robotY - 1) == MineCell.BEARD) return true
            if (mine.get(robotX, robotY + 1) == MineCell.BEARD) return true
            if (mine.get(robotX + 1, robotY - 1) == MineCell.BEARD) return true
            if (mine.get(robotX + 1, robotY) == MineCell.BEARD) return true
            if (mine.get(robotX + 1, robotY + 1) == MineCell.BEARD) return true

            return false
        }

        return true
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
        if (needToTerminateFlag) {
            logger.log("terminated because of terminateFlag")
            System.err?.println("terminated because of terminateFlag")
            return true
        }
        if (answer == null)
            return false
        if (answer!!.robot.moveCount == answer!!.robot.mine.maxMoveCount) {
            logger.log("terminated because of move count: " + answer!!.robot.moveCount)
            System.err?.println("terminated because of move count: " + answer!!.robot.moveCount)
            return true
        }
        if (highScore != null && countScore(answer!!.robot) >= highScore) {
            logger.log("terminated because of high score: " + countScore(answer!!.robot))
            System.err?.println("terminated because of high score: " + countScore(answer!!.robot))
            return true
        }
        return false
    }

    override fun iterateQueue(queue: StateQueue, depth: Int, acceptor: StateAcceptor) {
        val initialMoves = queue.peek().robot.moveCount
        while (!queue.isEmpty()) {
            if (needToTerminateFlag) {
                logger.log("exit on terminated flag")
                return
            }
            val robotState = queue.pop()

            if (robotState.robot.status.terminated) continue

            val moves = model.possibleMoves.toList()
//            Collections.shuffle(moves)
            for (move in moves) {
                if (!isValidMove(robotState, move)) {
                    continue
                }

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
                    logger.log("WON!!!")
                    logger.log("Count score: ${countScore(newState.robot)}")
                    logger.log("Score: ${newState.score}")
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
        try {
            val startRobot = Robot(initialMine, 0, 0, RobotStatus.LIVE, initialMine.waterproof)
            val startState = RobotState(startRobot, null, scorer)
            updateAnswer(startState)
            var currentStates : Collection<RobotState> = listOf(startState)
            val queue = StateQueue()
            while (!needToTerminate() && !currentStates.isEmpty()) {
                logger.log("Current: ${currentStates.size} Answer: ${answer?.path}")
    //            for (state in currentStates) {
    //                queue.push(state)
    //            }
                val robotStates = shortTermExplorer.processStates(currentStates, queue, resultLimit, depth)
                iteration++
                currentStates = robotStates.getBestStates()
                logger.log("Current states: ${currentStates.size}")
                queue.clearQueue()
            }

            logger.log("Answer: ")
            logger.logNewState(queue, answer!!)
        }
        catch (e: Throwable) {
            interruptAndWriteResult()
        }
    }

    private @Volatile var answerWritten = false
    public fun interruptAndWriteResult() {
        synchronized (this) {
            if (!answerWritten) {
            //        workerThread.interrupt()
                println(answer?.path)
                logger.close()
                answerWritten = true
            }
        }
    }
}
