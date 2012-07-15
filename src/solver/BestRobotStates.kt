package solver

import java.util.List
import java.util.TreeSet
import java.util.ArrayList
import java.util.Comparator
import java.util.Collection

class BestRobotStates(val limit: Int) {
    val bestStates = TreeSet<RobotState>(object : Comparator<RobotState> {
        public override fun equals(obj: Any?): Boolean {
            return obj == this
        }

        public override fun compare(r1: RobotState?, r2: RobotState?): Int {
            val score1 = r1!!.score
            val score2 = r2!!.score
            if (score1 < score2) return 1
            if (score1 > score2) return -1
            return 0
        }

    })

    fun add(state: RobotState) {
        val score = state.score
        if (bestStates.size() > limit && bestStates.last()!!.score >= score) return
        bestStates.add(state)
        if (bestStates.size > limit) {
            bestStates.pollLast()
        }
    }

    fun getBestStates() : Collection<RobotState> {
        return bestStates
    }

    fun getBestState() = bestStates.first()!!
}