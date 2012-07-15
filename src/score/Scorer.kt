package score

import solver.RobotState
import model.RobotStatus
import model.MineCell
import model.Mine

abstract class Scorer {

    abstract fun score(state: RobotState): Double

    open fun initialize(mine: Mine) {}
}

class OwnLambdasScorer: Scorer() {
    override fun score(state: RobotState): Double {
        var ans = 50 * state.robot.collectedLambdas - state.robot.moveCount
        if (state.robot.status == RobotStatus.WON) {
            ans += 25 * state.robot.collectedLambdas
        }

        var lambdas = state.robot.mine.getPointsOfType(MineCell.LAMBDA)
        var minDist = Integer.MAX_VALUE
        for (lambda in lambdas) {
            minDist = Math.min(minDist,
                    Math.abs(lambda.x - state.robot.x) + Math.abs(lambda.y - state.robot.y))
        }

        return ans.toDouble() - 10 * minDist
    }
}