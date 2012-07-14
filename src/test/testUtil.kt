package testUtil

import junit.framework.ComparisonFailure
import junit.framework.TestCase
import util.trimTrailingSpaces

public fun assertSameLines(message: String, expectedText: String, actualText: String) {
    val expectedLines = expectedText.split('\n')
    val actualLines = actualText.split('\n')
    for (i in 0..(Math.max(expectedLines.size, actualLines.size) - 1)) {
        val expectedLine = if (i < expectedLines.size) expectedLines[i].trimTrailingSpaces() else ""
        val actualLine = if (i < actualLines.size) actualLines[i].trimTrailingSpaces() else ""

        if (expectedLine != actualLine) {
            throw ComparisonFailure(message, expectedText, actualText)
        }
    }
}

public abstract class UsefulTestCase : TestCase() {
    protected fun getTestName() : String {
        val name = getName()
        if (name == null) {
            return ""
        }
        return name.trim("test").decapitalize()
    }

}