package io

import java.io.File
import java.io.FileInputStream
import junit.framework.ComparisonFailure
import junit.framework.TestCase
import util.repeat

public class TestReading : TestCase() {
    public fun testReadAllMines() {
        File("mines").recurse{ f ->
            if (f.isFile()) {
                val input = FileInputStream(f)
                val mine = readMine(input)
                input.close()
                val fileText = f.readText()
                val expectedLines = fileText.split('\n')
                val toStringText = mine.toString()
                val actualLines = toStringText.split('\n')
                for (i in 0..(Math.max(expectedLines.size, actualLines.size) - 1)) {
                    var expectedLine = if (i < expectedLines.size) expectedLines[i] else ""
                    var actualLine = if (i < actualLines.size) actualLines[i] else ""
                    val len = Math.max(expectedLine.length, actualLine.length)

                    expectedLine += " ".repeat(len - expectedLine.length)
                    actualLine += " ".repeat(len - actualLine.length)
                    if (expectedLine != actualLine) {
                        throw ComparisonFailure(null, fileText, toStringText)
                    }
                }
            }
        }
    }
}