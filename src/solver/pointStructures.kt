package solver

import model.Mine
import model.Point
import java.util.*

class PointMap<T: Any>(val mine : Mine) : AbstractMap<Point, T>() {
    val array: Array<Any?> = Array<Any?>(mine.width * mine.height) { null }

    override var size = 0

    inner class PointMapEntry(override var key: Point, override var value: T) : MutableMap.MutableEntry<Point, T> {
        public override fun setValue(newValue: T): T {
            value = newValue
            return newValue
        }
        public override fun equals(other: Any?): Boolean = this === other
        public override fun hashCode(): Int = 0
    }

    public override val entries: MutableSet<MutableMap.MutableEntry<Point, T>> = object : AbstractSet<MutableMap.MutableEntry<Point, T>>() {
        public override val size: Int get() = this@PointMap.size
        public override fun iterator(): MutableIterator<MutableMap.MutableEntry<Point, T>> = object : MutableIterator<MutableMap.MutableEntry<Point, T>> {
            var x = 0
            var y = 0
            var visited = 0
            private fun makeOneStep() {
                if (++x == mine.width) {
                    x = 0
                    y++
                }
            }
            public override fun hasNext(): Boolean = visited < this@PointMap.size
            public override fun next(): MutableMap.MutableEntry<Point, T> {
                if (!hasNext())
                    throw IllegalStateException("PointMap has no more cells")
                while (array[index(x, y)] == null)
                    makeOneStep()
                val ans = PointMapEntry(Point(x, y), array[index(x, y)] as T)
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
        return prev as T?
    }

    public override fun get(key: Point): T? {
        val i = index(key)
        return array[i] as T?
    }

    public override fun isEmpty(): Boolean = size == 0
}

