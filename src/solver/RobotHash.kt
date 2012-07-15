package solver

import model.Robot
import util._assert
import model.MineCell

class RobotHash(val hash1: Int, val hash2: Int) {
    class object {
        fun calculate(robot: Robot): RobotHash {
            val mine = robot.mine
            var hash1 = 0
            var hash2 = 0
            for (y in 0..mine.height - 1) {
                for (x in 0..mine.width - 1) {
                    var cell = mine[x,y]
                    if (cell == MineCell.EMPTY && (!mine[x - 1,y].isRock()) && (!mine[x + 1,y].isRock()) && (!mine[x,y + 1].isRock())) {
                        cell = MineCell.EARTH
                    }
                    hash1 = hash1 * 239 + cell.toChar()
                    hash2 = hash2 * 366239 + cell.toChar()
                }
            }
            return RobotHash(hash1, hash2)
        }
    }

    fun hashCode() = hash1 + hash2

    fun equals(o: Any?) = o is RobotHash && hash1 == o.hash1 && hash2 == o.hash2

    fun toString() = "${hash1},${hash2}"
}
