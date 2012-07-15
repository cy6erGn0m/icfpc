package solver

import model.Robot
import score.Scorer

class RobotState(val robot: Robot, val path: RobotPath?, val scorer: Scorer) {
    private var _score : Double? = null

    public val score: Double
       get() {
           if (_score == null) {
               _score = scorer.score(this)
           }
           return _score!!
       }

}
