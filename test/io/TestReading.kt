package io

import java.io.File
import java.io.FileInputStream
import junit.framework.ComparisonFailure
import junit.framework.TestCase
import kotlin.test.assertTrue
import testUtil.assertSameLines

public class TestReading : TestCase() {
    public fun testReadAllMines() {
        var ex : ComparisonFailure? = null
        var totalFiles = 0
        var errorFiles = 0
        File("mines").recurse{ f ->
            if (f.isFile()) {
                val input = FileInputStream(f)
                val mine = readMine(input)
                input.close()
                totalFiles++
                try {
                    assertSameLines(f.readText(), mine.toString())
                } catch (e : ComparisonFailure) {
                    ex = e
                    errorFiles++
                }
            }
        }
        assertTrue((ex == null) == (errorFiles == 0))
        if (ex == null) {
            println("Read $totalFiles files with no errors")
        }
        else {
            println("Read $totalFiles files, $errorFiles with errors, last error is the following")
            throw ex!!
        }
    }
}