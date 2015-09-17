package test.solver

import junit.framework.TestCase
import solver.BestRobotStates
import solver.RobotState
import model.Robot
import model.Mine
import io.readMine
import model.RobotStatus
import score.Scorer
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BestRobotStatesTest : TestCase() {

    val dummyScorer = object : Scorer() {
        override fun score(state: RobotState): Double {
            return state.robot.moveCount.toDouble()
        }
    }

    fun makeDummyState(moveCount: Int) = RobotState(Robot(readMine("RO"), moveCount, 10, RobotStatus.LIVE, 10, false), null, dummyScorer)

    fun testOneState() {
        val s = BestRobotStates(1)
        val state = makeDummyState(10)
        s.add(state)
        assertEquals(state, s.getBestState())
    }

    fun testTwoStatesLimitOne() {
        val s = BestRobotStates(1)
        val state1 = makeDummyState(10)
        val state2 = makeDummyState(20)
        s.add(state1)
        s.add(state2)
        assertEquals(state2, s.getBestState())
    }

    fun testTwoStatesLimitTwo() {
        val s = BestRobotStates(2)
        val state1 = makeDummyState(10)
        val state2 = makeDummyState(20)
        s.add(state1)
        s.add(state2)
        assertEquals(state2, s.getBestState())
    }

    fun testManyStatesLimitOne() {
        val s = BestRobotStates(1)
        val states = listOf(
                makeDummyState(20),
                makeDummyState(15),
                makeDummyState(25),
                makeDummyState(40),
                makeDummyState(10))
        for (state in states) {
            s.add(state)
        }
        assertEquals(states[3], s.getBestState())
    }

    fun testManyStatesLimitThree() {
        val s = BestRobotStates(3)
        val states = listOf(
                makeDummyState(20),
                makeDummyState(15),
                makeDummyState(25),
                makeDummyState(40),
                makeDummyState(10))
        for (state in states) {
            s.add(state)
        }

        val bestStates = s.getBestStates()
        assertTrue(bestStates.contains(states[0]))
        assertTrue(bestStates.contains(states[2]))
        assertTrue(bestStates.contains(states[3]))
    }
}