package util

import io.serialize
import java.io.PrintWriter
import model.Move
import solver.RobotHash
import solver.RobotState
import solver.StateQueue

fun _assert(c: Boolean, message: String) {
    if (!c) {
        throw AssertionError(message)
    }
}

fun _assert(c: Boolean) = _assert(c, "Assertion failed")

fun String.repeat(count : Int) : String {
    val buf = StringBuilder()
    for (i in 1..count) {
        buf.append(this)
    }
    return buf.toString()
}

fun String.trimTrailingSpaces() : String {
    // workaround for downto bug
    if (length == 1) {
        return if (Character.isWhitespace(this[0])) "" else this
    }

    for (i in (0..(length - 1)).reversed()) {
        if (!Character.isWhitespace(this[i])) {
            return substring(0, i + 1)
        }
    }
    return ""
}

class Logger(val fileName: String, val isActive: Boolean = true) {
    val logFile = PrintWriter(fileName)

    fun log(s: String) {
        if (!isActive) return
        logFile.println(s)
        flush()
    }

    fun flush() {
        logFile.flush()
    }

    fun close() {
        logFile.close()
    }

    fun logNewState(queue: StateQueue, newState: RobotState) {
        log("path: ${newState.path}")
        log("visited: ${queue.visited.size()}")
        log("status: ${newState.robot.status}")
        log("score: ${newState.score}")
        log("hash: ${RobotHash.calculate(newState.robot)}")
        log(newState.robot.mine.serialize())
    }
}

class Ref<T>(var value : T)
