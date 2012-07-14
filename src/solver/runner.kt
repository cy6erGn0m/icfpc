package solver.runner

import solver.Solver
import io.readMine

fun main(args: Array<String>) {
    val solver = Solver(readMine(System.`in`))

    Runtime.getRuntime()!!.addShutdownHook(object : Thread() {
        public override fun run() {
            solver.interruptAndWriteResult()
        }
    })

    solver.start()
    solver.interruptAndWriteResult()
}
