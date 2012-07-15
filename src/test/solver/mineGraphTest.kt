package test.solver

import junit.framework.TestCase
import io.readMine
import solver.MineGraph
import kotlin.test.assertEquals
import model.Point
import java.util.Collections
import kotlin.test.assertTrue

class MineGraphTest : TestCase() {

    private fun createSimpleGraph() = MineGraph(readMine(arrayList(
            """########""",
            """#L....\#""",
            """####.#.#""",
            """#.....##""",
            """#\###\.#""",
            """#...#..#""",
            """###.R.##""",
            """########"""
    )))

    fun testCreation() {
        val graph = createSimpleGraph()
        assertEquals(graph.vertices.size, graph.edges.size)
        assertEquals(23, graph.vertices.size)
    }

    fun testFindPathLengths() {
        val graph = createSimpleGraph()
        val robot = Point(graph.mine.robotX, graph.mine.robotY)
        assertEquals(Point(4, 1), robot)
        val lengths = graph.findPathLengths(robot)
        assertEquals(0, lengths[robot])
        assertEquals(1, lengths[Point(3, 1)])
        assertEquals(3, lengths[Point(2, 2)])
        assertEquals(2, lengths[Point(5, 2)])
        assertEquals(7, lengths[Point(2, 4)])
        assertEquals(9, lengths[Point(2, 6)])
        assertEquals(10, lengths[Point(6, 5)])
    }

    fun testFindPathLengthsToLambda() {
        val graph = createSimpleGraph()
        val robot = Point(graph.mine.robotX, graph.mine.robotY)
        val lengths = graph.findPathLengthsToLambda(robot)
        assertEquals(3, lengths.size)
        assertTrue(3 in lengths)
        assertTrue(5 in lengths)
        assertTrue(9 in lengths)
    }

    fun testMinPathToLambda() {
        val graph = createSimpleGraph()
        val robot = Point(graph.mine.robotX, graph.mine.robotY)
        assertEquals(3, graph.findMinPathToLambda(robot))
    }
}
