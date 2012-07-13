package evaluator

import model.Mine
import model.MineCell
import model.MineCell.ROBOT
import model.MineCell.ROCK
import model.MineCell.CLOSED_LIFT
import model.MineCell.EARTH
import model.MineCell.WALL
import model.MineCell.LAMBDA
import model.MineCell.OPEN_LIFT
import model.MineCell.EMPTY
import util._assert

fun mineUpdate(mine: Mine): Mine {
    val r = Mine(mine.width, mine.height)
    for (y in 0..mine.height - 1) {
        for (x in 0..mine.width - 1) {
            mapUpdateAt(mine, x, y, r)
        }
    }
    return r
}

fun mapUpdateAt(cur: Mine, x: Int, y: Int, res: Mine){
    fun at(cell: MineCell) = cur[x, y] == cell
    val atRock = at(ROCK)
    val rockOverRock = atRock && cur[x, y - 1] == ROCK
    val canSlideRight = cur[x + 1, y] == EMPTY && cur[x + 1, y - 1] == EMPTY
    val canSlideLeft = cur[x - 1, y] == EMPTY && cur[x - 1, y - 1] == EMPTY

    when {
        atRock
            && cur[x, y - 1] == EMPTY
        -> {
            res[x, y] = EMPTY
            res[x, y - 1] = ROCK
        }

        rockOverRock
            && canSlideRight
        -> {
            res[x, y] = EMPTY
            res[x + 1, y - 1] = ROCK
        }

        rockOverRock
            // we know we can't slide right
            && canSlideLeft
        -> {
            _assert(!canSlideRight, "when is not working")
            res[x, y] = EMPTY
            res[x - 1, y - 1] = ROCK
        }

        atRock
            && cur[x, y - 1] == LAMBDA
            && canSlideRight
        -> {
            res[x, y] = EMPTY
            res[x + 1, y - 1] = ROCK
        }

        at(CLOSED_LIFT)
            && cur.lambdaCount == 0
        -> {
            res[x, y] = OPEN_LIFT
        }

        else -> {
            res[x, y] = cur[x, y]
        }
    }
}