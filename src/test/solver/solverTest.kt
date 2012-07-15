package test.solver

import junit.framework.TestCase
import solver.Solver
import io.readMine
import java.io.FileInputStream
import kotlin.test.assertEquals
import solver.RobotState
import evaluator.countScore
import kotlin.test.assertTrue
import util.Logger
import evaluator.makeMove
import model.Move
import model.RobotStatus
import score.CollectedLambdasScorer

val logger = Logger("test_log")

class SolverTest : TestCase() {

    protected override fun tearDown() {
        super<TestCase>.tearDown()
        logger.flush()
    }

    fun doTest(filename: String): RobotState {
        val solver = Solver(readMine(FileInputStream(filename)), CollectedLambdasScorer())

        val startTime = System.nanoTime()
        solver.start()
        val endTime = System.nanoTime()
        logger.log("Filename: ${filename} Time: ${(endTime - startTime) / 1e9}")

        return solver.answer!!
    }

    fun assertSolutionIsNotWorse(expected: String, answer: RobotState, highScore: Int, ourExpectedScore: Int, testName: String) {
        val path = answer.path.toString()
        var robot = answer.robot
        if (answer.robot.status != RobotStatus.WON && answer.robot.status != RobotStatus.ABORTED) {
            robot = makeMove(Move.ABORT, answer.robot, solver.solverUpdate)
        }
        val ourScore = countScore(robot)
        logger.log("\nTest: " + testName)
        logger.log("Expected: (${expected.length()}) ${expected} \nActual:   (${path.length()}) ${path}\n High score: ${highScore} Our score: ${ourScore}")
        assertTrue(ourExpectedScore <= ourScore, "Our score decreased, expected: ${ourExpectedScore} actual: ${ourScore}")
    }

    fun doSolverTest(testname: String, expected: String, highScore: Int, ourExpectedScore: Int) {
        val ans = doTest("mines/solver/${testname}.map")
        assertSolutionIsNotWorse(expected, ans, highScore, ourExpectedScore, testname)
    }

    fun doContestTest(n: Int, expected: String, highScore: Int, ourExpectedScore: Int) {
        val ans = doTest("mines/default/contest${n}.map")
        assertSolutionIsNotWorse(expected, ans, highScore, ourExpectedScore, "contest${n}")
    }

    fun doFloodTest(n: Int, expected: String, highScore: Int, ourExpectedScore: Int) {
        val ans = doTest("mines/default/flood/flood${n}.map")
        assertSolutionIsNotWorse(expected, ans, highScore, ourExpectedScore, "flood${n}")
    }

    fun testUp() = doSolverTest("up", "UU", 73, 73)
    fun testOneLambda() = doSolverTest("oneLambda", "RRRR", 71, 71)
    fun testOneMoreLambda() = doSolverTest("oneMoreLambda", "DDRRR", 70, 70)

/*
    fun testContest1() = doContestTest(1, "DLLDDRRLULLDL", 212, 212)
    fun testContest2() = doContestTest(2, "RRRRDLRULURULLLDDLDL", 281, 277)
    fun testContest3() = doContestTest(3, "LDDDRRRRDDLLLDLLURRRRRUUR", 275, 273)
    fun testContest4() = doContestTest(4, "DUURDDDDRDRRRLUURUUULUDRR", 575, 573)
    fun testContest5() = doContestTest(5, "LLUURUURULUURRRRRDDRLLLLDRDRRRRDDDLLRRUUULLDLLDDD*", 1303, 1303)
    fun testContest6() = doContestTest(6, "", 1177, 1177)
    fun testContest7() = doContestTest(7, "RDRRRDDLRDRDRRRLLLULULLDLDLLRRURR*", 869, 867)
    fun testContest8() = doContestTest(8, "", 0, 0)
    fun testContest9() = doContestTest(9, "", 0, 0)
    fun testContest10() = doContestTest(10, "", 0, 0)
    fun testFlood2() = doFloodTest(2, "RRRRDLRULURULLLDDLDL", 281, 276)
*/

}