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

    fun pop() = queue.poll()!!
    fun peek() = queue.peekFirst()!!
    fun isEmpty() = queue.isEmpty()

    fun clearQueue() = queue.clear()

    fun containsSimilar(state: RobotState) =
        visited.contains(RobotHash.calculate(state.robot))

    fun iterator(): MutableIterator<RobotState> = queue.iterator()
}