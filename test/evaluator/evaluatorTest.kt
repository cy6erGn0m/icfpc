package evaluator

import model.Mine
import java.io.FileInputStream
import io.readMine
import org.junit.Test as test
import org.junit.Assert.assertEquals

class EvaluatorTest {

    fun loadMine(fileName: String): Mine {
        return readMine(FileInputStream(fileName))
    }

    fun doTest(inFile: String, outFile: String) {
        val input = loadMine(inFile)
        val expected = loadMine(outFile)
        val actual = mapUpdate(input)
        assertEquals(expected.toString(), actual.toString())
    }

    fun doEvaluatorTest(name: String) {
        doTest("mines/evaluator/$name/in.map", "mines/evaluator/$name/out.map")
    }

    test fun case1() {
        doEvaluatorTest("case1")
    }

    test fun rockSlidesLambda() {
        doEvaluatorTest("rockSlidesLambda")
    }

    test fun rockSlidesLambdaToTheRight() {
        doEvaluatorTest("rockSlidesLambdaToTheRight")
    }

    test fun rockDoesntSlideLambdaToTheLeft() {
        doEvaluatorTest("rockDoesntSlideLambdaToTheLeft")
    }

    test fun rockFallsDown() {
        doEvaluatorTest("rockFallsDown")
    }

    test fun threeRocksFallDownSideBySide() {
        doEvaluatorTest("threeRocksFallDownSideBySide")
    }

    test fun threeRocksFallDownOnTopOfEachOtherAndSlideToTheRight() {
        doEvaluatorTest("threeRocksFallDownOnTopOfEachOtherAndSlideToTheRight")
    }

    test fun threeRocksFallDownOnTopOfEachOtherAndSlideToTheLeft() {
        doEvaluatorTest("threeRocksFallDownOnTopOfEachOtherAndSlideToTheLeft")
    }

    test fun manyRocksFallDownOnTopOfEachOtherStraight() {
        doEvaluatorTest("manyRocksFallDownOnTopOfEachOtherStraight")
    }

    test fun rockDoesntSlideFromARobot() {
        doEvaluatorTest("rockDoesntSlideFromARobot")
    }

    test fun rockDoesntSlideFromNotRock() {
        doEvaluatorTest("rockDoesntSlideFromNotRock")
    }

}