package solver.runner

import solver.Solver
import io.readMine
import score.CollectedLambdasScorer

fun main(args: Array<String>) {
    val solver = Solver(readMine(System.`in`), CollectedLambdasScorer())

    Runtime.getRuntime()!!.addShutdownHook(object : Thread() {
        public override fun run() {
            solver.terminate()
        }
    })

    solver.start()
    solver.interruptAndWriteResult()
}
