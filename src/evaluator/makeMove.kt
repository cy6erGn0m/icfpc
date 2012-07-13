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
    if (move == Move.ABORT) {
        return Robot(robot.mine, robot.points, robot.lambdas, RobotStatus.ABORTED)
    }
    val oldMine = robot.mine
    var newX = robot.x + move.deltaX
    var newY = robot.y + move.deltaY
    var lambdas = robot.lambdas
    var resultingStatus: RobotStatus = RobotStatus.LIVE
    when (oldMine[newX, newY]) {
        WALL, CLOSED_LIFT, INVALID -> {
            //impassable
            newX = robot.x
            newY = robot.y
        }
        EARTH, EMPTY -> {
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
                val behindTheRockX = newX + move.deltaX
                _assert(robot.y == newY, "Move to the side only")
                if (oldMine[behindTheRockX, robot.y] == EMPTY) {
                    oldMine[behindTheRockX, robot.y] = ROCK
                }
            }
        }
        else -> throw IllegalStateException("Unknown move: $move")
    }
    val points = robot.points - 1
    if (resultingStatus == RobotStatus.WON) {
        return Robot(oldMine, points, lambdas, RobotStatus.WON)
    }
    val newMine = mineUpdate(oldMine)
    val isRockAbove = newMine[newX, newY + 1] == ROCK
    val wasRockAbove = oldMine[newX, newY + 1] == ROCK
    if (isRockAbove && !wasRockAbove) {
        return Robot(newMine, points, lambdas, RobotStatus.DEAD)
    }
    return Robot(newMine, points, lambdas, RobotStatus.LIVE)
}