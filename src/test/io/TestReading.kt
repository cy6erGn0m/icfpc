package io

import java.io.File
import java.io.FileInputStream
import junit.framework.ComparisonFailure
import junit.framework.TestCase
import kotlin.test.assertTrue
import testUtil.assertSameLines
import kotlin.test.assertEquals
import junit.framework.Assert

public class TestReading : TestCase() {
    public fun testReadAllMines() {
        var ex : ComparisonFailure? = null
        var totalFiles = 0
        var errorFiles = 0
        File("mines").recurse{ f ->
            if (f.isFile() && f.getName().endsWith(".map")) {
                val input = FileInputStream(f)
                val mine = readMine(input)
                input.close()
                totalFiles++
                try {
                    assertSameLines(f.toString(), f.readText(), mine.serialize())
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

    public fun testReadInNonStandardFormat() {
        val toRead = """A#1
###

 $


Trampoline   A   targets$1
         Razors  $7777$$

Water$$$$$$$$$$$$$$$$$1
$$
Growth 55

        """.replace("$", "\t")
        val toCheck = """A#1
###

Water 1
Flooding 0
Waterproof 10
Trampoline A targets 1
Growth 55
Razors 7777
"""
        Assert.assertEquals(toCheck, readMine(toRead).serialize())
    }
}