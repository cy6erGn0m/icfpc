package solver

import java.util.List
import java.util.TreeSet
import java.util.ArrayList

class BestRobotStates(val scoring: (RobotState) -> Int, val limit: Int) {
    class RobotStateCandidate(val score: Int, val state: RobotState) : Comparable<RobotStateCandidate> {
        public override fun compareTo(other: RobotStateCandidate): Int {
            if (score < other.score) return 1
            if (score > other.score) return -1
            return 0
        }
    }
    val bestStates = TreeSet<RobotStateCandidate>()

    fun add(state: RobotState) {
        val score = scoring(state)
        if (bestStates.size() > limit && bestStates.last()!!.score >= score) return
        bestStates.add(RobotStateCandidate(score, state))
        if (bestStates.size > limit) {
            bestStates.pollLast()
        }
    }

    fun getBestStates() : List<RobotState> {
        return bestStates.map { it.state }
    }

    fun getBestState() = bestStates.first()!!.state
}