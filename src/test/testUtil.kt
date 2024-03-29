package testUtil

import junit.framework.ComparisonFailure
import junit.framework.TestCase
import java.util.ArrayList
import model.Move

public fun assertSameLines(message: String, expectedText: String, actualText: String) {
    val expectedLines = expectedText.split('\n')
    val actualLines = actualText.split('\n')
    for (i in 0..(Math.max(expectedLines.size, actualLines.size) - 1)) {
        val expectedLine = if (i < expectedLines.size) expectedLines[i].trimEnd() else ""
        val actualLine = if (i < actualLines.size) actualLines[i].trimEnd() else ""

        if (expectedLine != actualLine) {
            throw ComparisonFailure(message, expectedText, actualText)
        }
    }
}

public abstract class UsefulTestCase: TestCase() {
    protected fun getTestName(): String {
        val name = name ?: return ""

        return name.removePrefix("test").removeSuffix("test").decapitalize()
    }

}

public fun readMovesFromString(s: String): List<Move> =
    s.map { c ->
        when(c) {
            'A' -> Move.ABORT
            'W' -> Move.WAIT
            'L' -> Move.LEFT
            'R' -> Move.RIGHT
            'U' -> Move.UP
            'D' -> Move.DOWN
            'S' -> Move.SHAVE
            else -> throw IllegalArgumentException("Unknown character: $c")
        }
    }
