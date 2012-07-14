package solver

import model.Move

class RobotPath(val move: Move, val prev: RobotPath?) {
    fun toString() : String {
        return "${prev?.toString() ?: ""}${move.repr}"
    }
}