package evaluator

import model.Mine
import java.io.FileInputStream
import io.readMine
import org.junit.Test as test
import org.junit.Assert.assertEquals
import model.Move
import java.util.List
import model.RobotStatus
import java.io.InputStream
import java.util.ArrayList
import io.streamToLines
import junit.framework.TestCase
import model.Robot

class MovesTest : TestCase() {

    class TestData(
        val startMine: Mine,
        val moves: List<Move>,
        val expectedStatus: RobotStatus,
        val expectedScore: Int,
        val expectedMine: Mine
    )

    fun loadMine(input: List<String>): Mine {
        val lines = ArrayList<String>()
        for (line in input) {
            if ("-END MINE" == line) break
            lines.add(line)
        }
        return readMine(lines)
    }

    fun loadTestData(name: String): TestData {
        val lines = streamToLines(FileInputStream("mines/moves/$name.moves"))

        val moves = ArrayList<Move>()
        for (c in lines[0]) {
            moves.add(when(c) {
                'A' -> Move.ABORT
                'W' -> Move.WAIT
                'L' -> Move.LEFT
                'R' -> Move.RIGHT
                'U' -> Move.UP
                'D' -> Move.DOWN
                else -> throw IllegalArgumentException("Unknown character: $c")
            })
        }

        val startMine = loadMine(lines.subList(1, lines.size()))
        val scoreString =  lines[startMine.height + 2]
        val statusString = lines[startMine.height + 3]
        val expectedMine = loadMine(lines.subList(startMine.height + 4, lines.size()))
        return TestData(startMine, moves, statusString.toStatus(), Integer.parseInt(scoreString), expectedMine)

    }

    fun String.toStatus() = when (this) {
        "ABORTED" -> RobotStatus.ABORTED
        "WON" -> RobotStatus.WON
        "DEAD" -> RobotStatus.DEAD
        "LIVE" -> RobotStatus.LIVE
        else -> throw IllegalArgumentException("Unknown status: $this")
    }

    fun doTest() {
        val name = getTestName()
        val testData = loadTestData(name)

        var robot = Robot(testData.startMine, 0, 0, RobotStatus.LIVE)

        for (move in testData.moves) {
            robot = makeMove(move, robot)
        }

        assertEquals(testData.expectedScore, countScore(robot))
        assertEquals(testData.expectedStatus, robot.status)
        assertEquals(testData.expectedMine.toString(), robot.mine.toString())
    }

    fun getTestName(): String {
        val rawName = getName()!!
        val nameWithoutTestPrefix = rawName.substring(4)
        return nameWithoutTestPrefix.decapitalize()
    }

    fun testSimple() {
        doTest()
    }

    fun testMoveRight() {
        doTest()
    }

    fun testExcavateEarth() {
        doTest()
    }

    fun testCantMoveThroughWall() {
        doTest()
    }

    fun testCantMoveThroughWallForSeveralTimes() {
        doTest()
    }

    fun testNavigatingThroughLabyrinth() {
        doTest()
    }

    fun testNavigatingThereAndBack() {
        doTest()
    }

    fun testEatLambda() {
        doTest()
    }

    fun testEatLambdaAndAbort() {
        doTest()
    }

    fun testNavigatingDrunk() {
        doTest()
    }

    fun testNavigatingWithLambdas() {
        doTest()
    }

    fun testWin() {
        doTest()
    }

    fun testEatLambdaAndWin() {
        doTest()
    }
}
