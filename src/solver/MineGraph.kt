package solver

import model.Point
import model.Mine
import java.util.ArrayList
import java.util.HashMap
import model.MineCell
import java.util.HashSet
import java.util.LinkedList
import util._assert
import java.util.AbstractQueue
import java.util.PriorityQueue
import java.util.Comparator

class Edge(
        val begin: Point,
        val end: Point,
        val length: Int
)

private operator fun Mine.iterator() = object : Iterator<Point> {
    var x = 0
    var y = 0
    override fun next(): Point {
        if (!hasNext())
            throw IllegalStateException("Mine has no more cells")
        val ans = Point(x, y)
        if (++x == width) {
            x = 0
            y++
        }
        return ans
    }

    override fun hasNext(): Boolean {
        return y < height
    }
}

private val DX = arrayOf(1, 0, -1, 0)
private val DY = arrayOf(0, 1, 0, -1)

private class PathSearchQueue(val mine: Mine, val initialCapacity: Int = 10) {
    val queue = PriorityQueue<Point>(initialCapacity, object : Comparator<Point> {
        public override fun compare(o1: Point, o2: Point): Int = distanceTo(o1) - distanceTo(o2)
        public override fun equals(other: Any?): Boolean = this === other
    })
    val distance = PointMap<Int>(mine)

    fun add(point: Point, dist: Int) {
        val d = distance[point]
        if (d == null) {
            distance[point] = dist
            queue.offer(point)
        } else if (d > dist) {
            distance[point] = dist
        }
    }

    fun pop() = queue.poll()!!
    fun isEmpty() = queue.isEmpty()
    fun isVisited(point: Point) = distance[point] != null
    fun distanceTo(point: Point) = distance[point]!!
}


fun edgeCost(begin: MineCell, end: MineCell): Int {
    var ans = 1
    if (begin.isRock())
        ans += MineGraph.ROCK_COST
    if (end.isRock())
        ans += MineGraph.ROCK_COST
    if (begin == MineCell.BEARD)
        ans += MineGraph.BEARD_COST
    if (end == MineCell.BEARD)
        ans += MineGraph.BEARD_COST
    return ans
}

fun isRoughlyPassable(cell: MineCell) = cell.isPassable() || cell.isRock() || cell == MineCell.ROBOT || cell == MineCell.TARGET || cell == MineCell.BEARD

class MineGraph(
    val mine: Mine,
    val passableCells: (MineCell) -> Boolean = { cell -> isRoughlyPassable(cell) }
) {
    val edges = PointMap<List<Edge>>(mine);

    init {
        for (point in mine) {
            if (isRoughlyPassable(mine[point])) {
                edges[point] = getNeighbors(point)
            }
        }
        // println("edges:")
        // for (entry in edges) for (e in entry.value) println("${entry.key} -> ${e.end}")
    }

    companion object {
        val ROCK_COST = 3
        val BEARD_COST = 30
    }

    private fun getNeighbors(point: Point): List<Edge> {
        val neighbors = ArrayList<Edge>(4) // average number of edges from any vertex
        for (d in DX.indices) {
            val newPoint = Point(point.x + DX[d], point.y + DY[d])
            if (mine[newPoint] != MineCell.INVALID && isRoughlyPassable(mine[newPoint])) {
                neighbors.add(Edge(point, newPoint, edgeCost(mine[point], mine[newPoint])))
            }
        }
        if (mine[point] == MineCell.TRAMPOLINE) {
            neighbors.add(Edge(point, mine.trampolinesMap.getTarget(point), 0))
        }
        return neighbors
    }

    fun findPathLengths(start: Point): PointMap<Int> {
        val queue = PathSearchQueue(mine)
        queue.add(start, 0)

        while (!queue.isEmpty()) {
            val vertex = queue.pop()
            // _assert(isPassable(mine[vertex]))
            // _assert(edges[vertex] != null)
            val oldDistance = queue.distanceTo(vertex)
            val listOfEdges = edges[vertex]
            if (listOfEdges == null) continue
            for (edge in listOfEdges) {
                queue.add(edge.end, oldDistance + edge.length)
                2 + 2
            }
        }

        return queue.distance
    }

    fun findPathLengthsToLambdaAndOpenLift(start: Point): List<Int> {
        val dist = findPathLengths(start)
        val answer = ArrayList<Int>()
        for (entry in dist) {
            if (mine[entry.key] == MineCell.LAMBDA || mine[entry.key] == MineCell.OPEN_LIFT) {
                answer.add(entry.value)
            }
        }
        return answer
    }

    fun findMinPathToLambdaOrOpenLift(start: Point): Int? {
        val dist = findPathLengthsToLambdaAndOpenLift(start)
        var min: Int? = null
        for (length in dist)
            min = if (min == null) length else Math.min(min, length)
        return min
    }
}
