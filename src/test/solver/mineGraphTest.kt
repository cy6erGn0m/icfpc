package test.solver

import junit.framework.TestCase
import io.readMine
import solver.MineGraph
import model.Point
import java.util.Collections
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MineGraphTest : TestCase() {

    private fun createSimpleGraph() = MineGraph(readMine(listOf(
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
        assertEquals(23, graph.edges.size)
    }

    fun testFindPathLengths() {
        val graph = createSimpleGraph()
        assertEquals(Point(4, 1), graph.mine.robotPos)
        val dist = graph.findPathLengths(graph.mine.robotPos)
        assertEquals(0, dist[graph.mine.robotPos])
        assertEquals(1, dist[Point(3, 1)])
        assertEquals(3, dist[Point(2, 2)])
        assertEquals(2, dist[Point(5, 2)])
        assertEquals(7, dist[Point(2, 4)])
        assertEquals(9, dist[Point(2, 6)])
        assertEquals(10, dist[Point(6, 5)])
    }

    fun testFindPathLengthsToLambda() {
        val graph = createSimpleGraph()
        val dist = graph.findPathLengthsToLambdaAndOpenLift(graph.mine.robotPos)
        assertEquals(3, dist.size())
        assertTrue(3 in dist)
        assertTrue(5 in dist)
        assertTrue(9 in dist)
    }

    fun testMinPathToLambda() {
        val graph = createSimpleGraph()
        assertEquals(3, graph.findMinPathToLambdaOrOpenLift(graph.mine.robotPos))
    }



    private fun createGraphWithRocks() = MineGraph(readMine(listOf(
            """##########""",
            """#..*...#.#""",
            """#..*...###""",
            """#.####\..#""",
            """#L#.\#**.#""",
            """###..#...#""",
            """#.R..*\ .#""",
            """##########"""
    )))


    fun testDijkstra() {
        val graph = createGraphWithRocks()
        assertEquals(Point(2, 1), graph.mine.robotPos)
        val dist = graph.findPathLengths(graph.mine.robotPos)
        assertEquals(0, dist[graph.mine.robotPos])
        assertEquals(1, dist[Point(1, 1)])
        assertEquals(3 + MineGraph.ROCK_COST, dist[Point(5, 1)])
        val firstLambda = 4 + 2*MineGraph.ROCK_COST
        assertEquals(firstLambda, dist[Point(6, 1)])
        val secondLambda = firstLambda + 1 + Math.min(6, 2 + 2*MineGraph.ROCK_COST)
        assertEquals(secondLambda, dist[Point(6, 4)])
        assertEquals(secondLambda + 7 + 2*MineGraph.ROCK_COST, dist[Point(1, 4)])
        assertFalse(dist.containsKey(Point(8, 6)))
    }


    fun testTrampolineDijkstra() {
        val graph = MineGraph(readMine(listOf(
                """#R.A#1.O#""",
                """""",
                """Trampoline A targets 1"""
        )))
        val dist = graph.findPathLengths(graph.mine.robotPos)
        assertEquals(3, dist[Point(6, 0)])
    }

}
