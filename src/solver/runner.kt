package solver.runner

import solver.Solver

fun main(args: Array<String>) {
    val solver = Solver()

    Runtime.getRuntime()!!.addShutdownHook(object : Thread() {
        public override fun run() {
            solver.interruptAndWriteResult()
        }
    })

    solver.start()
}