package score

import solver.RobotState
import model.RobotStatus
import model.MineCell
import model.Mine
import solver.MineGraph

abstract class Scorer {

    abstract fun score(state: RobotState): Double

    open fun initialize(mine: Mine) {}
}

class CollectedLambdasScorer: Scorer() {
    override fun score(state: RobotState): Double {
        var ans = 50 * state.robot.collectedLambdas - state.robot.moveCount
        if (state.robot.status == RobotStatus.WON) {
            ans += 25 * state.robot.collectedLambdas
            return ans + 1e9
        }

        /*
        var lambdas = state.robot.mine.getPointsOfType(MineCell.LAMBDA)
        var minDist = Integer.MAX_VALUE
        for (lambda in lambdas) {
            minDist = Math.min(minDist,
                    Math.abs(lambda.x - state.robot.x) + Math.abs(lambda.y - state.robot.y))
        }
        */
        val graph = MineGraph(state.robot.mine)
        var minDist = graph.findMinPathToLambdaOrOpenLift(state.robot.pos)
        if (minDist == null) {
            minDist = 1e9.toInt()
        }

        if (graph.mine.shouldOpenLift()) {
            ans += 1e7
        }

        return 100 * ans.toDouble() - minDist!!
    }
}