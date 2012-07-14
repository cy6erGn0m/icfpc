package test.solver

import junit.framework.TestCase
import solver.Solver
import io.readMine
import java.io.FileInputStream
import kotlin.test.assertEquals

class SolverTest : TestCase() {

    fun doTest(filename: String, expected: String) {
        val solver = Solver(readMine(FileInputStream(filename)))

        solver.start()

        assertEquals(expected, solver.answer!!.path.toString())
    }

    fun doSolverTest(testname: String, expected: String) = doTest("mines/solver/${testname}.map", expected)
    fun doContestTest(n: Int, expected: String) = doTest("mines/default/contest${n}.map", expected)

    fun testUp() = doSolverTest("up", "U")
    fun testOneLambda() = doSolverTest("oneLambda", "RRRR")
    fun testNoLambda() = doSolverTest("noLambda", "RDDDRRU")

    fun testContest1() = doContestTest(1, "")
}