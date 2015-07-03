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
