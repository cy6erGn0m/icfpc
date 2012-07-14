package evaluator.incremental

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
import evaluator.mineUpdate

public fun mineUpdateWithIncrementalCopy(mine: Mine): Mine {
    return mineUpdate(mine) {
        matrix ->
        DeltaCellMatrix.create(matrix)
    }
}