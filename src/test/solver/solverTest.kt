package test.solver

import junit.framework.TestCase
import solver.Solver
import solver as solverPackage
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
import java.util.Properties
import kotlin.test.fail
import kotlin.nullable.forEach
import java.io.FileOutputStream
import java.io.File
import java.io.OutputStream
import model.MineCell
import score.CollectedLambdasScorer
import java.util.LinkedHashMap
import java.util.HashMap
import java.util.Timer
import java.util.TimerTask


object SolverTestData {
    val updateExpectedResults = false

    val testScores = LinkedHashMap<String, Int>()

    var expectedResultsWasRead = false
    val expectedScores: HashMap<String, Int> = hashMap()
        get() {
            if (!expectedResultsWasRead) readExpectedResults()
            return $expectedScores
        }

    val propertiesFile = "src/test/solver/expectedResults.properties"
    val logger = Logger("test_log")

    val delay = 30000.toLong()
}

class SolverTest : TestCase() {

    fun doSolverTest(testName: String, expected: String, highScore: Int, ourExpectedScore: Int) {
        doTest("mines/solver/${testName}.map", testName, expected, highScore, ourExpectedScore)
    }

    fun doContestTest(n: Int, expected: String, highScore: Int, ourExpectedScore: Int) {
        doTest("mines/default/contest${n}.map", "contest${n}", expected, highScore, ourExpectedScore)
    }

    fun doFloodTest(n: Int, expected: String, highScore: Int, ourExpectedScore: Int) {
        doTest("mines/default/flood/flood${n}.map", "flood${n}", expected, highScore, ourExpectedScore)
    }

    fun doRandomTest() {
        doTest("mines/random/mine199_100x100.map", "mine199_100x100", "", 0, 3000)
    }

    fun testUp() = doSolverTest("up", "UU", 73, 73)
    fun testOneLambda() = doSolverTest("oneLambda", "RRRR", 71, 71)
    fun testOneMoreLambda() = doSolverTest("oneMoreLambda", "DDRRR", 70, 70)

    fun testContest1() = doContestTest(1, "DLLDDRRLULLDL", 212, 200)
    fun testContest2() = doContestTest(2, "RRRRDLRULURULLLDDLDL", 281, 250)
    fun testContest3() = doContestTest(3, "LDDDRRRRDDLLLDLLURRRRRUUR", 275, 250)
    fun testContest4() = doContestTest(4, "DUURDDDDRDRRRLUURUUULUDRR", 575, 550)
    fun testContest5() = doContestTest(5, "LLUURUURULUURRRRRDDRLLLLDRDRRRRDDDLLRRUUULLDLLDDD", 1303, 800)
    fun testContest6() = doContestTest(6, "", 1177, 700)
    fun testContest7() = doContestTest(7, "RDRRRDDLRDRDRRRLLLULULLDLDLLRRURR*", 869, 700)
    fun testContest8() = doContestTest(8, "", 1973, 1000)
    fun testContest9() = doContestTest(9, "", 3093, 1500)
    fun testContest10() = doContestTest(10, "", 3634, 1500)
    fun testFlood2() = doFloodTest(2, "RRRRDLRULURULLLDDLDL", 281, 120)

//    fun testRandom199() = doRandomTest()

    fun testProgress() {
        var currentSum = 0
        var expectedSum = 0
        for (testName in SolverTestData.testScores.keySet()) {
            currentSum += SolverTestData.testScores.get(testName)!!
            if (!SolverTestData.expectedScores.containsKey(testName)) {
                println("Old ${SolverTestData.propertiesFile} Doesn't contain ${testName}")
            }
            expectedSum += SolverTestData.expectedScores.get(testName) ?: 0
        }
        SolverTestData.logger.log("\nCurrent sum: ${currentSum} Expected sum: ${expectedSum} \n")

        if (SolverTestData.updateExpectedResults) {
            writeExpectedResults()
        }

        if (currentSum.toDouble() / expectedSum < 0.8) {
            val message = StringBuilder()
            for (entry in SolverTestData.testScores.entrySet()) {
                val testName = entry.getKey()
                message.append("test: ${testName} expected: ${SolverTestData.expectedScores.get(testName)} actual: ${entry.getValue()}\n")
            }
            fail("Progress is getting worse. Current sum: ${currentSum} Expected sum: ${expectedSum} \n${message}")
        }
    }

    fun doTest(fileName: String, testName: String, expected: String, highScore: Int, ourExpectedScore: Int) {
        val solver = Solver(readMine(FileInputStream(fileName)), CollectedLambdasScorer())

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            public override fun run() {
                solver.terminate()
            }
        }, SolverTestData.delay)
        val startTime = System.nanoTime()
        solver.start()
        val endTime = System.nanoTime()
        val time = (endTime - startTime) / 1e9

        val answer = solver.answer!!

        val path = answer.path.toString()
        var robot = answer.robot
        if (answer.robot.status != RobotStatus.WON && answer.robot.status != RobotStatus.ABORTED) {
            robot = makeMove(Move.ABORT, answer.robot, solverPackage.solverUpdate)
        }
        val ourScore = countScore(robot)

        SolverTestData.testScores.put(testName, ourScore)
        SolverTestData.logger.log("\nTest: ${testName} time: ${time}")
        SolverTestData.logger.log("Expected: (${expected.length()}) ${expected} \nActual:   (${path.length()}) ${path}")
        SolverTestData.logger.log("High score: ${highScore} Previous expected score: ${SolverTestData.expectedScores.get(testName)} Current score: ${ourScore}")

        assertTrue(ourExpectedScore <= ourScore, "The actual score is too small, expected at least: ${ourExpectedScore} actual: ${ourScore}")
    }
}

fun readExpectedResults() {
    SolverTestData.expectedResultsWasRead = true
    val properties = Properties()
    val fileInputStream = FileInputStream(SolverTestData.propertiesFile)
    properties.load(fileInputStream)
    fileInputStream.close()
    for (testName in properties.keySet()) {
        val score = properties.getProperty(testName.toString())
        SolverTestData.expectedScores.put(testName.toString(), Integer.valueOf(score)!!)
    }
}

fun writeExpectedResults() {
    val properties = Properties()
    val fileOutputStream = FileOutputStream(SolverTestData.propertiesFile)
    for (entry in SolverTestData.testScores.entrySet()) {
        properties.put(entry.getKey(), entry.getValue().toString())
    }
    properties.store(fileOutputStream, null)
    fileOutputStream.close()
}
