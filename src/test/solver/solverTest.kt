package test.solver

import junit.framework.TestCase
import solver.Solver
import io.readMine
import java.io.FileInputStream
import kotlin.test.assertEquals

class SolverTest : TestCase() {

    fun doTest(filename: String): String {
        val solver = Solver(readMine(FileInputStream(filename)))

        val startTime = System.nanoTime()
        solver.start()
        val endTime = System.nanoTime()
        println("Filename: ${filename} Time: ${(endTime - startTime) / 1e9}")

        return solver.answer!!.path.toString()
    }

    fun doSolverTest(testname: String, expected: String) {
        val ans = doTest("mines/solver/${testname}.map");
        assertEquals(expected, ans)
    }
    fun doContestTest(n: Int, expected: String) {
        val ans = doTest("mines/default/contest${n}.map")
        assertEquals(expected, ans)
    }
    fun doFloodTest(n: Int, expected: String) {
        val ans = doTest("mines/default/flood/flood${n}.map")
        assertEquals(expected, ans)
    }

    fun testUp() = doSolverTest("up", "U")
    fun testOneLambda() = doSolverTest("oneLambda", "RRRR")
    fun testNoLambda() = doSolverTest("noLambda", "DDRRR")

    fun testContest1() = doContestTest(1, "DLLDDRRLULLDL")
    fun testContest2() = doContestTest(2, "RRRRDLRULURULLLDDLDL")
    fun testContest3() = doContestTest(3, "LDDDRRRRDDLLLDLLURRRRRUUR")
    fun testContest4() = doContestTest(4, "DUURDDDDRDRRRLUURUUULUDRR")
    // fun testContest5() = doContestTest(5, "")

    fun testFlood2() = doFloodTest(2, "RRRRDLRULURULLLDDLDL")
}