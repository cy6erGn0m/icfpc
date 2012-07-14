package solver

import java.util.HashSet
import java.util.LinkedList

class StateQueue {
    val queue = LinkedList<RobotState>()
    val visited = HashSet<RobotHash>()

    fun push(state: RobotState) {
        queue.offer(state)
        visited.add(RobotHash.calculate(state.robot))
    }

    fun pop(): RobotState {
        return queue.poll()!!
    }

    fun isEmpty(): Boolean {
        return queue.isEmpty()
    }

    fun containsSimilar(state: RobotState): Boolean {
        return visited.contains(RobotHash.calculate(state.robot))
    }
}