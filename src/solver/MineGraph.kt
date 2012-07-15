package solver

import model.Point
import model.Mine
import java.util.List
import java.util.ArrayList
import java.util.HashMap
import model.MineCell
import java.util.HashSet
import java.util.LinkedList
import util._assert
import java.util.AbstractQueue

private class Edge(
        val begin: Point,
        val end: Point,
        val length: Int
)

private fun Mine.iterator() = object : Iterator<Point> {
    var x = 0
    var y = 0
    override fun next(): Point {
        if (!hasNext) throw IllegalStateException("Mine has no more cells")
        val ans = Point(x, y)
        if (++x == width) {
            x = 0
            y++
        }
        return ans
    }
    override val hasNext: Boolean
        get() = y < height
}

private val DX = array(1, 0, -1, 0)
private val DY = array(0, 1, 0, -1)

//TODO make more efficient
class PointSet : HashSet<Point>()
class PointMap<T> : HashMap<Point, T>()

private class PathSearchQueue {
    val queue = LinkedList<Point>()
    val distance = PointMap<Int>()

    fun push(point: Point, dist: Int) {
        queue.offer(point)
        distance[point] = dist
    }

    fun pop() = queue.poll()!!
    fun isEmpty() = queue.isEmpty()
    fun isVisited(point: Point) = distance[point] != null
    fun distanceTo(point: Point) = distance[point]!!
}


class MineGraph(val mine: Mine) {
    val vertices = PointSet()
    val edges = PointMap<List<Edge>>();

    {
        val cells = mine.width * mine.height
        for (point in mine) {
            if (isPassable(mine[point])) {
                vertices.add(point)
                edges[point] = getNeighbors(point)
            }
        }
        // println("edges:")
        // for (entry in edges) for (e in entry.value) println("${entry.key} -> ${e.end}")
    }

    private fun isPassable(cell: MineCell) = cell.isPassable() || cell.isRock() || cell == MineCell.ROBOT

    private fun edgeCost(begin: MineCell, end: MineCell): Int {
        var ans = 1
        if (begin.isRock())
            ans += 5
        if (end.isRock())
            ans += 5
        return ans
    }

    private fun getNeighbors(point: Point): List<Edge> {
        val neighbors = ArrayList<Edge>(4) // average number of edges from any vertex
        for (d in 0..DX.size-1) {
            val newPoint = Point(point.x + DX[d], point.y + DY[d])
            if (mine[newPoint] != MineCell.INVALID && isPassable(mine[newPoint])) {
                neighbors.add(Edge(point, newPoint, edgeCost(mine[point], mine[newPoint])))
            }
        }
        return neighbors
    }

    fun findPathLengths(start: Point): PointMap<Int> {
        val queue = PathSearchQueue()
        queue.push(start, 0)

        while (!queue.isEmpty()) {
            val vertex = queue.pop()
            // _assert(isPassable(mine[vertex]))
            // _assert(edges[vertex] != null)
            val oldDistance = queue.distanceTo(vertex)
            for (edge in edges[vertex]) {
                if (!queue.isVisited(edge.end)) {
                    queue.push(edge.end, oldDistance + edge.length)
                }
                2 + 2
            }
        }

        return queue.distance
    }

    fun findPathLengthsToLambda(start: Point): List<Int> {
        val lengths = findPathLengths(start)
        val answer = ArrayList<Int>()
        for (entry in lengths) {
            if (mine[entry.key] == MineCell.LAMBDA) {
                answer.add(entry.value)
            }
        }
        return answer
    }

    fun findMinPathToLambda(start: Point): Int {
        val lengths = findPathLengthsToLambda(start)
        var min = Integer.MAX_VALUE
        for (length in lengths)
            min = Math.min(min, length)
        return min
    }
}
