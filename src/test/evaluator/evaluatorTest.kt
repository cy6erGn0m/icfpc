package evaluator

import io.readMine
import io.serialize
import java.io.FileInputStream
import model.Mine
import org.junit.Assert.assertEquals
import testUtil.UsefulTestCase

class EvaluatorTest : UsefulTestCase() {

    fun loadMine(fileName: String): Mine {
        return readMine(FileInputStream(fileName))
    }

    fun doTest(inFile: String, outFile: String) {
        val input = loadMine(inFile)
        val expected = loadMine(outFile)
        val actual = mineUpdateWithFullCopy(input)
        assertEquals(expected.serialize(), actual.serialize())
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

    fun testWaterFloodsSimple() {
        doEvaluatorTest()
    }

    fun testWaterFloodsQuickly() {
        doEvaluatorTest()
    }

    fun testWaterFloodsLevelIncreases() {
        doEvaluatorTest()
    }
}