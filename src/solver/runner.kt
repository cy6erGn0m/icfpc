package solver.runner

import solver.Solver
import io.readMine
import score.CollectedLambdasScorer
import score.CollectedLambdasScorerWithDistToLambdas

fun main(args: Array<String>) {
    val solver = Solver(readMine(System.`in`), CollectedLambdasScorerWithDistToLambdas())

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        public override fun run() {
            solver.interruptAndWriteResult()
        }
    })

    solver.start()
    solver.interruptAndWriteResult()
    System.exit(0)
}
