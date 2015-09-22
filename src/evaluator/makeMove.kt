package evaluator

import model.Mine
import model.MineCell
import model.Move
import model.Robot
import model.MineCell.ROBOT
import model.MineCell.ROCK
import model.MineCell.CLOSED_LIFT
import model.MineCell.EARTH
import model.MineCell.WALL
import model.MineCell.LAMBDA
import model.MineCell.LAMBDA_ROCK
import model.MineCell.OPEN_LIFT
import model.MineCell.EMPTY
import model.MineCell.INVALID
import model.MineCell.TARGET
import model.MineCell.TRAMPOLINE
import model.MineCell.BEARD
import model.MineCell.RAZOR
import model.RobotStatus
import util._assert
import model.Point

val validTargetCells = listOf(MineCell.EMPTY, MineCell.EARTH, MineCell.LAMBDA, MineCell.OPEN_LIFT)

fun shavePoint(mine: Mine, x: Int, y: Int) {
    if (mine[x, y] == MineCell.BEARD) {
        mine[x, y] = MineCell.EMPTY
    }
}

fun shaveAround(mine: Mine, point: Point) {
    shavePoint(mine, point.x - 1, point.y - 1)
    shavePoint(mine, point.x - 1, point.y)
    shavePoint(mine, point.x - 1, point.y + 1)

    shavePoint(mine, point.x, point.y - 1)
    shavePoint(mine, point.x, point.y + 1)

    shavePoint(mine, point.x + 1, point.y - 1)
    shavePoint(mine, point.x + 1, point.y)
    shavePoint(mine, point.x + 1, point.y + 1)
}

fun makeMove(move: Move, robot: Robot, update: (Mine) -> Mine): Robot {
    if (robot.status != RobotStatus.LIVE) {
        throw IllegalArgumentException("Only live robots can move: robot is ${robot.status}")
    }
    if (move == Move.ABORT) {
        return Robot(robot.mine, robot.moveCount, robot.collectedLambdas, RobotStatus.ABORTED, -1)
    }
    val oldMine = robot.mine
    var razors = oldMine.razors
    if (move == Move.SHAVE && razors > 0) {
        shaveAround(oldMine, robot.pos)
        razors--
    }
    var newPos = move.nextPosition(robot.pos)
    var lambdas = robot.collectedLambdas
    var resultingStatus: RobotStatus = RobotStatus.LIVE
    when (oldMine[newPos]) {
        TRAMPOLINE -> {
            val trampolinesMap = robot.mine.trampolinesMap
            val target = trampolinesMap.getTarget(newPos)
            for (trampoline in trampolinesMap.getTrampolines(target)) {
                oldMine[trampoline] = EMPTY
            }
            //make it passable
            oldMine[target] = EMPTY
            newPos = target
        }
        WALL, CLOSED_LIFT, INVALID, TARGET, EARTH, EMPTY, ROBOT, BEARD -> {
            //nothing to do here
        }
        LAMBDA -> {
            lambdas++
        }
        RAZOR -> {
            razors++
        }
        OPEN_LIFT -> {
            resultingStatus = RobotStatus.WON
        }
        ROCK, LAMBDA_ROCK -> {
            if (move == Move.LEFT || move == Move.RIGHT) {
                _assert(robot.y == newPos.y, "Move to the side only")
                oldMine.tryMoveRock(rockX = newPos.x, rockY = robot.y, left = (move == Move.LEFT), rockType = oldMine[newPos])
            }
        }
        else -> throw IllegalStateException("Unknown cell: ${oldMine[newPos]}")
    }
    val newMoveCount = robot.moveCount + 1
    if ((oldMine[newPos].isPassable())) {
        oldMine.moveRobot(robot.pos, newPos)
    }
    val newMine = update(oldMine)
    newMine.razors = razors
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
    val isRockAbove = newMine[aboveNewRobotPos].isRock()
    val isHorockAbove = newMine[aboveNewRobotPos] == LAMBDA && oldMine[aboveNewRobotPos] != LAMBDA
    val wasRockAbove = oldMine[aboveNewRobotPos].isRock()
    return (isRockAbove || isHorockAbove) && !wasRockAbove
}

fun countScore(robot: Robot): Int {
    return when (robot.status) {
        RobotStatus.DEAD, RobotStatus.LIVE -> 25 * robot.collectedLambdas - robot.moveCount
        RobotStatus.ABORTED -> 50 * robot.collectedLambdas - robot.moveCount
        RobotStatus.WON -> 75 * robot.collectedLambdas - robot.moveCount
        else -> throw IllegalStateException("Unknown state: ${robot.status}")
    }
}