package solver.runner

import solver.Solver
import io.readMine
import score.OwnLambdasScorer

fun main(args: Array<String>) {
    val solver = Solver(readMine(System.`in`), OwnLambdasScorer())

    Runtime.getRuntime()!!.addShutdownHook(object : Thread() {
        public override fun run() {
            solver.needToTerminateFlag = true
        }
    })

    solver.start()
    solver.interruptAndWriteResult()
}
