package test.solver

import junit.framework.TestCase
import solver.BestRobotStates
import solver.RobotState
import model.Robot
import model.Mine
import io.readMine
import model.RobotStatus
import kotlin.test.assertEquals

class BestRobotStatesTest : TestCase() {

    val dummyScoring = { (state : RobotState) : Int -> state.robot.moveCount }
    fun makeDummyState(moveCount: Int) = RobotState(Robot(readMine("RO"), moveCount, 10, RobotStatus.LIVE, 10, false), null)

    fun testOneState() {
        val s = BestRobotStates(dummyScoring, 1)
        val state = makeDummyState(10)
        s.add(state)
        assertEquals(state, s.getBestState())
    }

    fun testTwoStatesLimitOne() {
        val s = BestRobotStates(dummyScoring, 1)
        val state1 = makeDummyState(10)
        val state2 = makeDummyState(20)
        s.add(state1)
        s.add(state2)
        assertEquals(state2, s.getBestState())
    }

    fun testTwoStatesLimitTwo() {
        val s = BestRobotStates(dummyScoring, 2)
        val state1 = makeDummyState(10)
        val state2 = makeDummyState(20)
        s.add(state1)
        s.add(state2)
        assertEquals(state2, s.getBestState())
    }

    fun testManyStatesLimitOne() {
        val s = BestRobotStates(dummyScoring, 1)
        val states = arrayList(
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
        val s = BestRobotStates(dummyScoring, 3)
        val states = arrayList(
                makeDummyState(20),
                makeDummyState(15),
                makeDummyState(25),
                makeDummyState(40),
                makeDummyState(10))
        for (state in states) {
            s.add(state)
        }
        assertEquals(arrayList(states[3], states[2], states[0]), s.getBestStates())
    }
}