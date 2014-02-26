package solver

import model.Move

class RobotPath(val move: Move, val prev: RobotPath?) {
    override fun toString(): String = "${prev?.toString() ?: ""}${move.repr}"
}
