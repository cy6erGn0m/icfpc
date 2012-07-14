package util

import java.io.PrintWriter
import java.io.FileWriter
import solver.StateQueue
import solver.RobotState
import model.Move
import solver.RobotHash

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
    return buf.toString()!!
}

fun String.trimTrailingSpaces() : String {
    // workaround for downto bug
    if (length == 1) {
        return if (Character.isWhitespace(this[0])) "" else this
    }

    for (i in length - 1 downto 0) {
        if (!Character.isWhitespace(this[i])) {
            return substring(0, i + 1)
        }
    }
    return ""
}

class Logger(val fileName: String) {
    val logFile = PrintWriter(fileName)

    fun log(s: String) {
        logFile.println(s)
    }

    fun flush() {
        logFile.flush()
    }

    fun close() {
        logFile.close()
    }

    fun logNewState(queue: StateQueue, newState: RobotState, move: Move) {
        log("path: ${newState.path}")
        log("visited: ${queue.visited.size()}")
        log("status: ${newState.robot.status}")
        log("move: ${move.repr}")
        log("hash: ${RobotHash.calculate(newState.robot)}")
        log(newState.robot.mine.toString())
        flush()
    }
}
