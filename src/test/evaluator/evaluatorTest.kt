package evaluator

import model.Mine
import java.io.FileInputStream
import io.readMine
import org.junit.Assert.assertEquals
import junit.framework.TestCase

class EvaluatorTest : TestCase() {

    fun loadMine(fileName: String): Mine {
        return readMine(FileInputStream(fileName))
    }

    fun doTest(inFile: String, outFile: String) {
        val input = loadMine(inFile)
        val expected = loadMine(outFile)
        val actual = mineUpdate(input)
        assertEquals(expected.toString(), actual.toString())
    }

    fun doEvaluatorTest(name: String) {
        doTest("mines/evaluator/$name/in.map", "mines/evaluator/$name/out.map")
    }

    fun testCase1() {
        doEvaluatorTest("case1")
    }

    fun testRockSlidesLambda() {
        doEvaluatorTest("rockSlidesLambda")
    }

    fun testRockSlidesLambdaToTheRight() {
        doEvaluatorTest("rockSlidesLambdaToTheRight")
    }

    fun testRockDoesntSlideLambdaToTheLeft() {
        doEvaluatorTest("rockDoesntSlideLambdaToTheLeft")
    }

    fun testRockFallsDown() {
        doEvaluatorTest("rockFallsDown")
    }

    fun testThreeRocksFallDownSideBySide() {
        doEvaluatorTest("threeRocksFallDownSideBySide")
    }

    fun testThreeRocksFallDownOnTopOfEachOtherAndSlideToTheRight() {
        doEvaluatorTest("threeRocksFallDownOnTopOfEachOtherAndSlideToTheRight")
    }

    fun testThreeRocksFallDownOnTopOfEachOtherAndSlideToTheLeft() {
        doEvaluatorTest("threeRocksFallDownOnTopOfEachOtherAndSlideToTheLeft")
    }

    fun testManyRocksFallDownOnTopOfEachOtherStraight() {
        doEvaluatorTest("manyRocksFallDownOnTopOfEachOtherStraight")
    }

    fun testRockDoesntSlideFromARobot() {
        doEvaluatorTest("rockDoesntSlideFromARobot")
    }

    fun testRockDoesntSlideFromNotRock() {
        doEvaluatorTest("rockDoesntSlideFromNotRock")
    }

    fun testComlexCasesOfRocksNotSliding() {
        doEvaluatorTest("comlexCasesOfRocksNotSliding")
    }

    fun testLiftOpensWhenNoLambdasPresent() {
        doEvaluatorTest("liftOpensWhenNoLambdasPresent")
    }

    fun testLiftDoesntOpenWhenLambdasPresent() {
        doEvaluatorTest("liftDoesntOpenWhenLambdasPresent")
    }

}