package evaluator

import io.readMine
import junit.framework.TestCase
import model.MineCell
import model.Point
import org.junit.Assert.assertEquals

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
        assertEquals(1, robotAtZero.robotY)
    }

    fun testGetPointsOfType() {
        val mine = readMine("""
\....L
R.....
\.\...""")
        val lambdas = mine.getPointsOfType(MineCell.LAMBDA).toTypedArray()
        assertEquals(3, lambdas.size)
        assertEquals(Point(0, 0), lambdas[0])
        assertEquals(Point(2, 0), lambdas[1])
        assertEquals(Point(0, 2), lambdas[2])
    }
}