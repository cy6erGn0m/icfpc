package util

import java.util.Set
import model.Point
import java.util.AbstractSet
import java.util.Collections

object DumbSet : AbstractSet<Point>() {
    public override fun size(): Int {
        return 0;
    }

    public override fun iterator(): java.util.Iterator<Point> {
        return Collections.emptyList<Point>()!!.iterator()
    }

    public override fun add(e: Point): Boolean {
        return true
    }


    public override fun remove(o: Any?): Boolean {
        return false
    }
}

