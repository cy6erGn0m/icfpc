package evaluator

import model.Move
import model.Robot
import model.MineCell
import model.MineCell.ROBOT
import model.MineCell.ROCK
import model.MineCell.CLOSED_LIFT
import model.MineCell.EARTH
import model.MineCell.WALL
import model.MineCell.LAMBDA
import model.MineCell.OPEN_LIFT
import model.MineCell.EMPTY
import model.MineCell.INVALID
import model.RobotStatus
import util._assert
import model.Mine


val validTargetCells = arrayList(MineCell.EMPTY, MineCell.EARTH, MineCell.LAMBDA, MineCell.OPEN_LIFT)


fun makeMove(move: Move, robot: Robot, update: (Mine) -> Mine): Robot {
    if (robot.status != RobotStatus.LIVE) {
        throw IllegalArgumentException("Only live robots can move")
    }
    if (move == Move.ABORT) {
        return Robot(robot.mine, robot.moveCount, robot.collectedLambdas, RobotStatus.ABORTED, -1)
    }
    val oldMine = robot.mine
    var newPos = move.nextPosition(robot.pos)
    var lambdas = robot.collectedLambdas
    var resultingStatus: RobotStatus = RobotStatus.LIVE
    var shouldMove = true
    when (oldMine[newPos]) {
        WALL, CLOSED_LIFT, INVALID -> {
            //impassable
            newPos = robot.pos
            shouldMove = false
        }
        EARTH, EMPTY, ROBOT -> {
            //nothing to do here
        }
        LAMBDA -> {
            lambdas++
        }
        OPEN_LIFT -> {
            resultingStatus = RobotStatus.WON
        }
        ROCK -> {
            if (move == Move.LEFT || move == Move.RIGHT) {
                _assert(robot.y == newPos.y, "Move to the side only")
                oldMine.tryMoveRock(rockX = newPos.x, rockY = robot.y, left = (move == Move.LEFT))
            }
        }
        else -> throw IllegalStateException("Unknown cell: ${oldMine[newPos]}")
    }
    val newMoveCount = robot.moveCount + 1
    if (oldMine[newPos].isPassable() && shouldMove) {
        oldMine.moveRobot(robot.pos, newPos)
    }
    val newMine = update(oldMine)
    if (resultingStatus == RobotStatus.WON) {
        return Robot(newMine, newMoveCount, lambdas, RobotStatus.WON, -1, true)
    }
    val newOxygen = if (newPos.y <= oldMine.water) robot.oxygen - 1 else oldMine.waterproof
    if (isDead(robot, newMine, newOxygen)) {
        return Robot(newMine, newMoveCount, lambdas, RobotStatus.DEAD, -1)
    }
    return Robot(newMine, newMoveCount, lambdas, RobotStatus.LIVE, newOxygen)
}

fun isDead(robot: Robot, newMine: Mine, newOxygen: Int): Boolean {
    var isDead = false
    if (isRobotSmashedByRock(robot.mine, newMine)) {
        isDead = true
    }
    if (newOxygen < 0) {
        isDead = true
    }
    return isDead
}

fun isRobotSmashedByRock(oldMine: Mine, newMine: Mine): Boolean {
    val aboveNewRobotPos = newMine.robotPos.above()
    val isRockAbove = newMine[aboveNewRobotPos] == ROCK
    val wasRockAbove = oldMine[aboveNewRobotPos] == ROCK
    return isRockAbove && !wasRockAbove
}

fun countScore(robot: Robot): Int {
    return when (robot.status) {
        RobotStatus.DEAD, RobotStatus.LIVE -> 25 * robot.collectedLambdas - robot.moveCount
        RobotStatus.ABORTED -> 50 * robot.collectedLambdas - robot.moveCount
        RobotStatus.WON -> 75 * robot.collectedLambdas - robot.moveCount
        else -> throw IllegalStateException("Unknown state: ${robot.status}")
    }
}