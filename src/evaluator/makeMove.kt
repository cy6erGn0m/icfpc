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


val validTargetCells = arrayList(MineCell.EMPTY, MineCell.EARTH, MineCell.LAMBDA, MineCell.OPEN_LIFT)


fun makeMove(move: Move, robot: Robot): Robot {
    if (robot.status != RobotStatus.LIVE) {
        throw IllegalArgumentException("Only live robots can move")
    }
    if (move == Move.ABORT) {
        return Robot(robot.mine, robot.moveCount, robot.collectedLambdas, RobotStatus.ABORTED, -1)
    }
    val oldMine = robot.mine
    var newX = robot.x + move.deltaX
    var newY = robot.y + move.deltaY
    var lambdas = robot.collectedLambdas
    var resultingStatus: RobotStatus = RobotStatus.LIVE
    var shouldMove = true
    when (oldMine[newX, newY]) {
        WALL, CLOSED_LIFT, INVALID -> {
            //impassable
            newX = robot.x
            newY = robot.y
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
                _assert(robot.y == newY, "Move to the side only")
               oldMine.tryMoveRock(rockX = newX, rockY = robot.y, left = (move == Move.LEFT))
            }
        }
        else -> throw IllegalStateException("Unknown cell: ${oldMine[newX, newY]}")
    }
    val newMoveCount = robot.moveCount + 1
    if (oldMine[newX, newY].isPassable() && shouldMove) {
        oldMine.moveRobot(robot.x, robot.y, newX, newY)
    }
    val newMine = mineUpdate(oldMine)
    if (resultingStatus == RobotStatus.WON) {
        return Robot(newMine, newMoveCount, lambdas, RobotStatus.WON, -1, true)
    }
    val isRockAbove = newMine[newX, newY + 1] == ROCK
    val wasRockAbove = oldMine[newX, newY + 1] == ROCK
    val newOxygen = if (newY <= oldMine.water) robot.oxygen - 1 else oldMine.waterproof
    val severelySmashedByRock = isRockAbove && !wasRockAbove
    if (severelySmashedByRock || newOxygen < 0) {
        return Robot(newMine, newMoveCount, lambdas, RobotStatus.DEAD, -1)
    }
    return Robot(newMine, newMoveCount, lambdas, RobotStatus.LIVE, newOxygen)
}

fun countScore(robot: Robot): Int {
    return when (robot.status) {
        RobotStatus.DEAD, RobotStatus.LIVE -> 25 * robot.collectedLambdas - robot.moveCount
        RobotStatus.ABORTED -> 50 * robot.collectedLambdas - robot.moveCount
        RobotStatus.WON -> 75 * robot.collectedLambdas - robot.moveCount
        else -> throw IllegalStateException("Unknown state: ${robot.status}")
    }
}