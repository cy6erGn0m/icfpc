package evaluator

import model.Mine
import java.io.FileInputStream
import io.readMine
import org.junit.Test as test
import org.junit.Assert.assertEquals
import model.Move
import model.RobotStatus
import java.io.InputStream
import java.util.ArrayList
import io.streamToLines
import junit.framework.TestCase
import model.Robot
import testUtil.UsefulTestCase
import solver.solverUpdate
import io.serialize
import testUtil.readMovesFromString

class MovesTest : UsefulTestCase() {

    class TestData(
        val startMine: Mine,
        val moves: List<Move>,
        val expectedStatus: RobotStatus,
        val expectedScore: Int,
        val expectedMine: Mine
    )

    fun loadMine(input: List<String>): Mine {
        return readMine(input.subList(0, input.indexOf("-END MINE")))
    }

    fun loadTestData(name: String): TestData {
        val lines = streamToLines(FileInputStream("mines/moves/$name.moves"))

        val moves = readMovesFromString(lines[0])

        val startMine = loadMine(lines.subList(1, lines.size))
        val scoreString =  lines[lines.indexOf("-END MINE") + 1]
        val statusString = lines[lines.indexOf("-END MINE") + 2]
        val expectedMine = loadMine(lines.subList(lines.indexOf("-END MINE") + 3, lines.size))
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

        var robot = Robot(testData.startMine, 0, 0, RobotStatus.LIVE, testData.startMine.waterproof)

        for (move in testData.moves) {
            robot = makeMove(move, robot, solverUpdate)
        }

        val expected = makeString(testData.expectedScore, testData.expectedStatus, testData.expectedMine)
        val actual = makeString(countScore(robot), robot.status, robot.mine)

        assertEquals(expected, actual)
    }

    fun makeString(score: Int, status: RobotStatus, mine: Mine) = "$score\n$status${mine.serialize()}"

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

    fun testRockKills() {
        doTest()
    }

    fun testSafelyDigUnderRocks() {
        doTest()
    }

    fun testMoveRockToPass() {
        doTest()
    }

    fun testCanPassUnderFallingRockLikeABoss() {
        doTest()
    }

    fun testFailToPassUnderFallingRockLikeABoss() {
        doTest()
    }

    fun testContest1Optimal() {
        doTest()
    }

    fun testContest9Random() {
        doTest()
    }

    fun testWaterKillsRobotWaits() {
        doTest()
    }

    fun testWaterAlmostKillsRobotWaits() {
        doTest()
    }

    fun testFlood2WaterDeath() {
        doTest()
    }

    fun testFlood2EscapesWater() {
        doTest()
    }

    fun testFlood2Wins() {
        doTest()
    }

    fun testFlood2AlmostWinsButWaits() {
        doTest()
    }

    fun testFlood2DivesIntoFloodAndDies() {
        doTest()
    }

    fun testFlood2DivesIntoFloodAndLives() {
        doTest()
    }

    fun testFlood3JumpsFromWaterAndSurvives() {
        doTest()
    }

    fun testFlood3JumpsFromWaterAndDiesBecauseTooSlow() {
        doTest()
    }

    fun testSimpleTrampoline() {
        doTest()
    }

    fun allTrampolinesDisappear() {
        doTest()
    }

    fun testTrampoline1EnterTheVault() {
        doTest()
    }

    fun testTrampoline1StealAllLambdasAndTheirWomen() {
        doTest()
    }

    fun testMultipleTrampolines() {
        doTest()
    }

    fun testHorockBecomesLambda() {
        doTest()
    }

    fun testHorocksBecomeLambdasWhenSlide() {
        doTest()
    }

    fun testHorocksCollide() {
        doTest()
    }

    fun testHorockKills() {
        doTest()
    }

    fun testHorockSlidesAndKills() {
        doTest()
    }

    fun testLiftWontOpenWhileHorocksAreThere() {
        doTest()
    }

    fun testLiftOpensWhenWeEatLambdasFromHorock() {
        doTest()
    }

    fun testHorock1Win() {
        doTest()
    }

    fun testBeard1RockFallsIntoGrowingBeardUpperRightCorner() {
        doTest()
    }

    fun testBeard1RockFallsIntoGrowingBeardCentralRight() {
        doTest()
    }

    fun testBeard1RockFallsIntoGrowingBeardLowerRightCorner() {
        doTest()
    }

    fun testBeard3RobotPushesRockIntoBeardToAnnihilate() {
        doTest()
    }
    
    fun testBeard2RockFallsIntoGrowingBeardUpperLeft() {
        doTest()
    }

    fun testHorock2JustAbort() {
        doTest()
    }

    fun testHorock2win() {
        doTest()
    }

    fun testBeard2LongRouteSuffocated() {
        doTest()
    }

    fun testPickUpRazor() {
        doTest()
    }

    fun testPickUpRazorAndUseItImmediately() {
        doTest()
    }

    fun testCantShaveWithoutRazor() {
        doTest()
    }

    fun testShavingAllCellsAround() {
        doTest()
    }

    fun testShavingBeardNotWall() {
        doTest()
    }

    fun testBeard1ShaveAndGrowBeardSimultaneously() {
        doTest()
    }

    fun testBeard5FromTwitter() {
        doTest()
    }

    fun testTrampoline1cantStepOnTargets() {
        doTest()
    }

    fun testShavingOnTheEdge() {
        doTest()
    }

    fun testTeleportsIntoWater() {
        doTest()
    }

    fun testSmashedByARockOverTarget() {
        doTest()
    }

    fun testEscapesRockIntoTrampoline() {
        doTest()
    }
}
