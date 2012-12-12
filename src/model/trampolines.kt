package model

import java.util.ArrayList
import java.util.HashMap

val Char.isTrampolineId: Boolean get() = this in 'A'..'I'
val Char.isTargetId: Boolean get() = this in '1'..'9'

public class TrampolinesMap() {
    private val trampolineToTarget: MutableMap<Point, Point> = HashMap<Point, Point>()
    private val targetToTrampoline: MutableMap<Point, MutableList<Point>> = HashMap<Point, MutableList<Point>>()

    private val locationToId: MutableMap<Point, Char> = HashMap<Point, Char>()

    // use only from map reader
    public fun addId(id: Char, location: Point) {
        if (locationToId.containsKey(location) || locationToId.containsValue(id)) {
            throw IllegalArgumentException("Adding to the same location or id")
        }
        locationToId[location] = id
    }

    // use only from map reader
    public fun addLink(trampoline: Point, target: Point) {
        if (!locationToId.containsKey(trampoline)) {
            throw IllegalArgumentException("Unregistered trampoline: " + trampoline)
        }
        if (!locationToId.containsKey(target)) {
            throw IllegalArgumentException("Unregistered target: " + target)
        }

        trampolineToTarget[trampoline] = target
        if (!targetToTrampoline.containsKey(target)) {
            targetToTrampoline[target] = ArrayList<Point>()
        }
        targetToTrampoline[target]!!.add(trampoline)
    }

    public fun getTarget(trampoline: Point): Point {
        val target = trampolineToTarget[trampoline]
        return if (target != null) target else throw IllegalArgumentException("No trampoline at " + trampoline)
    }

    public fun getTrampolines(target: Point): List<Point> {
        val trampolines = targetToTrampoline[target]
        return if (trampolines != null) trampolines else throw IllegalArgumentException("No target at " + target)
    }

    public fun getId(location: Point): Char {
        val id = locationToId[location]
        return if (id != null) id else throw IllegalArgumentException("No trampoline/target at " + location)
    }

    public fun checkForOrphanTrampolinesAndTargets() {
        for (locationAndId in locationToId) {
            val location = locationAndId.getKey()
            val id = locationAndId.getValue()
            if (id.isTrampolineId) {
                if (!(location in trampolineToTarget.keySet())) {
                    throw IllegalStateException("No target for trampoline $id")
                }
            }
            else if (id.isTargetId) {
                if (!(location in targetToTrampoline.keySet())) {
                    throw IllegalStateException("No trampoline for target $id")
                }
            }
            else {
                throw IllegalStateException("Unexpected id: $id")
            }
        }
    }
}

