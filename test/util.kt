package testUtil

import junit.framework.ComparisonFailure
import util.repeat

public fun assertSameLines(expected : String, actual : String) {
    val expectedLines = expected.split('\n')
    val actualLines = actual.split('\n')
    for (i in 0..(Math.max(expectedLines.size, actualLines.size) - 1)) {
        var expectedLine = if (i < expectedLines.size) expectedLines[i] else ""
        var actualLine = if (i < actualLines.size) actualLines[i] else ""
        val len = Math.max(expectedLine.length, actualLine.length)

        expectedLine += " ".repeat(len - expectedLine.length)
        actualLine += " ".repeat(len - actualLine.length)
        if (expectedLine != actualLine) {
            throw ComparisonFailure(null, expected, actual)
        }
    }
}