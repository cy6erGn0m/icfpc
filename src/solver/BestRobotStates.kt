package solver

import java.util.TreeSet
import java.util.ArrayList
import java.util.Comparator
import java.util.PriorityQueue

interface StateAcceptor {
    fun add(state: RobotState)
}

class BestRobotStates(val limit: Int) : StateAcceptor {
    val WORST_SCORE_FIRST_OUT = object : Comparator<RobotState> {
        public override fun equals(other: Any?): Boolean {
            return other == this
        }

        public override fun compare(o1: RobotState, o2: RobotState): Int {
            // Invert comparator for removing from the end
            val score1 = o1.score
            val score2 = o2.score
            if (score1 < score2) return -1
            if (score1 > score2) return 1
            return 0
        }
    }

    val bestStates = PriorityQueue<RobotState>(limit, WORST_SCORE_FIRST_OUT)

    override fun add(state: RobotState) {
        val score = state.score
        if (bestStates.size > limit && bestStates.peek()!!.score >= score) {
            return
        }
        bestStates.add(state)
        if (bestStates.size > limit) {
            bestStates.poll()
        }
    }

    fun getBestStates() : Collection<RobotState> {
        return bestStates
    }

    fun getBestState() : RobotState = bestStates.sortedWith(WORST_SCORE_FIRST_OUT).last()
}
