package solver

import model.Robot

class RobotHash(val hash: Int) {
    class object {
        fun calculate(robot: Robot): RobotHash {
            val mine = robot.mine
            var ans = robot.x * 239 + robot.y
            for (y in 0..mine.height - 1) {
                for (x in 0..mine.width - 1) {
                    ans = ans * 239 + mine[x,y].toChar()
                }
            }
            return RobotHash(ans)
        }
    }

    fun hashCode() = hash

    fun equals(o: Any?) = o is RobotHash && hash == o.hash
}
