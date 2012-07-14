package evaluator

import model.Mine
import java.io.FileInputStream
import io.readMine
import org.junit.Assert.assertEquals
import junit.framework.TestCase

class MineTest : TestCase() {

    fun testRobotAtZero() {
        val robotAtZero = readMine("R")
        assertEquals(0, robotAtZero.robotX)
        assertEquals(0, robotAtZero.robotY)
    }

    fun testRobotSomewhereElse() {
        val robotAtZero = readMine("""
.....
..R..
....#
        """)
        assertEquals(2, robotAtZero.robotX)
        assertEquals(2, robotAtZero.robotY)
    }
}