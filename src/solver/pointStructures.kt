package solver

import java.util.HashSet
import model.Point
import model.Mine
import java.util.AbstractMap
import java.util.HashMap
import java.util.Set
import java.util.Map
import java.util.AbstractSet
import java.util.Map.Entry

class PointSet : HashSet<Point>()

class PointMap<T>(val mine : Mine) : AbstractMap<Point, T>() {
    val array: Array<T?> = Array<T?>(mine.width * mine.height) { null }

    var size = 0

    class PointMapEntry(var myKey: Point, var myValue: T) : Map.Entry<Point, T> {
        public override fun setValue(value: T): T {
            myValue = value
            return value
        }
        public override fun getKey(): Point = myKey
        public override fun getValue(): T = myValue
        public override fun equals(o: Any?): Boolean = this === o
        public override fun hashCode(): Int = 0
    }

    public override fun entrySet(): Set<Map.Entry<Point, T>> = object : AbstractSet<Map.Entry<Point, T>>() {
        public override fun size(): Int = size
        public override fun iterator(): java.util.Iterator<Map.Entry<Point, T>> = object : java.util.Iterator<Map.Entry<Point, T>> {
            var x = 0
            var y = 0
            var visited = 0
            private fun makeOneStep() {
                if (++x == mine.width) {
                    x = 0
                    y++
                }
            }
            public override fun hasNext(): Boolean = visited < size
            public override fun next(): Map.Entry<Point, T> {
                if (!hasNext())
                    throw IllegalStateException("PointMap has no more cells")
                while (array[index(x, y)] == null)
                    makeOneStep()
                val ans = PointMapEntry(Point(x, y), array[index(x, y)]!!)
                makeOneStep()
                visited++
                return ans
            }
            public override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }


    private fun index(point: Point) = index(point.x, point.y)
    private fun index(x: Int, y: Int) = y * mine.width + x

    public override fun put(key: Point, value: T): T? {
        val i = index(key)
        val prev = array[i]
        if (prev == null)
            size++
        array[i] = value
        return prev
    }

    public override fun get(key: Any?): T? {
        if (key !is Point) return null
        val i = index(key)
        return array[i]
    }

    public override fun size(): Int = size

    public override fun isEmpty(): Boolean = size == 0
}

