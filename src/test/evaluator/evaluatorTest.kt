package evaluator

import model.Mine
import java.io.FileInputStream
import io.readMine
import org.junit.Assert.assertEquals
import junit.framework.TestCase
import testUtil.UsefulTestCase

class EvaluatorTest : UsefulTestCase() {

    fun loadMine(fileName: String): Mine {
        return readMine(FileInputStream(fileName))
    }

    fun doTest(inFile: String, outFile: String) {
        val input = loadMine(inFile)
        val expected = loadMine(outFile)
        val actual = mineUpdate(input)
        assertEquals(expected.toString(), actual.toString())
    }

    private fun doEvaluatorTest() {
        val name = getTestName()
        doTest("mines/evaluator/$name/in.map", "mines/evaluator/$name/out.map")
    }

    fun testCase1() {
        doEvaluatorTest()
    }

    fun testRockSlidesLambda() {
        doEvaluatorTest()
    }

    fun testRockSlidesLambdaToTheRight() {
        doEvaluatorTest()
    }

    fun testRockDoesntSlideLambdaToTheLeft() {
        doEvaluatorTest()
    }

    fun testRockFallsDown() {
        doEvaluatorTest()
    }

    fun testThreeRocksFallDownSideBySide() {
        doEvaluatorTest()
    }

    fun testThreeRocksFallDownOnTopOfEachOtherAndSlideToTheRight() {
        doEvaluatorTest()
    }

    fun testThreeRocksFallDownOnTopOfEachOtherAndSlideToTheLeft() {
        doEvaluatorTest()
    }

    fun testManyRocksFallDownOnTopOfEachOtherStraight() {
        doEvaluatorTest()
    }

    fun testRockDoesntSlideFromARobot() {
        doEvaluatorTest()
    }

    fun testRockDoesntSlideFromNotRock() {
        doEvaluatorTest()
    }

    fun testComlexCasesOfRocksNotSliding() {
        doEvaluatorTest()
    }

    fun testLiftOpensWhenNoLambdasPresent() {
        doEvaluatorTest()
    }

    fun testLiftDoesntOpenWhenLambdasPresent() {
        doEvaluatorTest()
    }
}