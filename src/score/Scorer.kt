package score

import solver.RobotState
import model.RobotStatus
import model.MineCell
import model.Mine
import solver.MineGraph

val MOVE_COUNT_WEIGHT = 30
val MIN_DIST_WEIGHT = 30

abstract class Scorer {

    abstract fun score(state: RobotState): Double

    open fun initialize(mine: Mine) {
    }
}

class CollectedLambdasScorer: Scorer() {
    override fun score(state: RobotState): Double {
        var ans = 50.0 * state.robot.collectedLambdas - state.robot.moveCount
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

        return 100 * ans.toDouble() - minDist
//
//        if (graph.mine.getPointsOfType(MineCell.LAMBDA).size() <= 1) {
//            ans += 50 * state.robot.collectedLambdas
//            return ans.toDouble() - MIN_DIST_WEIGHT * minDist!!
//        }
//
//        return 100 * 50.0 * state.robot.collectedLambdas - state.robot.moveCount - MIN_DIST_WEIGHT * minDist!!
    }
}

class CollectedLambdasScorerWithDistToLambdas: Scorer() {
    val GUARANTEED_SCORE_COEFFICIENT = 1
    val NEW_LAMBDAS_COEFFICIENT = 0.8
    val LIFT_IS_REACHABLE_COEFFICIENT = 0.8

    override fun score(state: RobotState): Double {

        if (state.robot.mine.get(state.robot.pos) == MineCell.INVALID) {
            return java.lang.Double.MIN_VALUE
        }

        var total: Double = 0.toDouble()

        // Guaranteed score
        var guaranteedScore = 50 * state.robot.collectedLambdas - state.robot.moveCount

        total += GUARANTEED_SCORE_COEFFICIENT * guaranteedScore

        val graph = MineGraph(state.robot.mine)
        val allDistLengths = graph.findPathLengths(state.robot.pos)

        val lambdaPoints = graph.mine.getPointsOfType(MineCell.LAMBDA)
        var weightedMovesToOtherLambdas: Double = 0.toDouble()

        for (lambdaPoint in lambdaPoints) {
            val distToLambda = allDistLengths.get(lambdaPoint)
            if (distToLambda != null) {
                weightedMovesToOtherLambdas = weightedMovesToOtherLambdas + (50 - distToLambda.toDouble())
            }
        }

        total += weightedMovesToOtherLambdas * NEW_LAMBDAS_COEFFICIENT

        val distToLift = allDistLengths.get(graph.mine.liftPos)

        if (distToLift != null) {
            val liftScore = 25 * (state.robot.collectedLambdas + (1 - NEW_LAMBDAS_COEFFICIENT) * lambdaPoints.size) - distToLift
            total += liftScore * LIFT_IS_REACHABLE_COEFFICIENT
        }

        return total
    }
}
