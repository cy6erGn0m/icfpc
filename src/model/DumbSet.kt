package util

import model.Point
import java.util.AbstractSet
import java.util.Collections
import java.util.ArrayList

object DumbSet : AbstractSet<Point>() {
    public override val size: Int get() = 0

    public override fun iterator(): MutableIterator<Point> {
        return ArrayList<Point>().iterator()
    }

    public override fun add(element: Point): Boolean {
        return true
    }


    public override fun remove(element: Point): Boolean {
        return false
    }

    public override fun toString(): String = "DumbSet"
}

