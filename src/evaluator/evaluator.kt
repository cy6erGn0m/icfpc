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

fun mapUpdate(mine: Mine): Mine {
    val r = Mine(mine.width, mine.height)
    for (y in 1..mine.height) {
        for (x in 1..mine.width) {

            val current = mine[x, y]




        }
    }
    return mine
}

fun mapUpdateAt(cur: Mine, x: Int, y: Int, res: Mine){
    when {
        cur[x, y] == ROCK && cur[x, y - 1] == EMPTY -> {
            res[x, y] = EMPTY
            res[x, y - 1] = ROCK
        }

        cur[x, y] == ROCK
            && cur[x, y - 1] == ROCK
            && cur[x + 1, y] == EMPTY
            && cur[x + 1, y - 1] == EMPTY
            -> {
            res[x, y] = EMPTY
            res[x + 1, y - 1] = ROCK
        }

        else -> {
            res[x, y] = cur[x, y]
        }
    }
}