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
import model.DeltaCellMatrix
import model.ArrayCellMatrix
import model.CellMatrix

fun mineUpdateWithFullCopy(mine: Mine): Mine {
    return mineUpdate(mine) {
    matrix ->
        ArrayCellMatrix(matrix.width, matrix.height, matrix.cellIndicesToTrack)
    }
}

fun mineUpdate(mine: Mine, copyMatrix: (CellMatrix) -> CellMatrix): Mine {
    val r = mine.copyMapAsDeltaNoCountersSet(copyMatrix)
    for (y in 0..mine.height - 1) {
        for (x in 0..mine.width - 1) {
            mapUpdateAt(mine, x, y, r)
        }
    }
    updateFlood(mine, r)
    return r
}

fun updateFlood(mine: Mine, r: Mine) {
    r.floodPeriod = mine.floodPeriod
    r.waterproof = mine.waterproof
    if (mine.nextFlood == 1) {
        r.water = mine.water + 1
        r.nextFlood = mine.floodPeriod
    }
    else {
        r.water = mine.water
        r.nextFlood = mine.nextFlood - 1
    }
}

fun mapUpdateAt(cur: Mine, x: Int, y: Int, res: Mine) {
    val curCell = cur[x, y]
    val bottomCell = cur[x, y - 1]
    val leftCell = cur[x - 1, y]
    val rightCell = cur[x + 1, y]
    val bottomLeftCell = cur[x - 1, y - 1]
    val bottomRightCell = cur[x + 1, y - 1]

    val atRock = curCell.isRock()
    val rockOverRock = atRock && bottomCell.isRock()
    val canSlideRight = rightCell == EMPTY && bottomRightCell == EMPTY
    val canSlideLeft = leftCell == EMPTY && bottomLeftCell == EMPTY

    when {
        atRock && bottomCell == EMPTY
        -> {
            res[x, y] = EMPTY
            res[x, y - 1] = curCell
        }

        rockOverRock && canSlideRight
        -> {
            res[x, y] = EMPTY
            res[x + 1, y - 1] = curCell
        }

        rockOverRock && canSlideLeft
        // we know we can't slide right
        -> {
            _assert(!canSlideRight, "when is not working")
            res[x, y] = EMPTY
            res[x - 1, y - 1] = curCell
        }

        atRock
        && bottomCell == LAMBDA
        && canSlideRight
        -> {
            res[x, y] = EMPTY
            res[x + 1, y - 1] = curCell
        }

        curCell == CLOSED_LIFT
        && cur.lambdaCount == 0
        -> {
            res[x, y] = OPEN_LIFT
        }

        else -> {
            res[x, y] = curCell
        }
    }
}