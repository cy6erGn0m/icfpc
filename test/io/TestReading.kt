package io

import java.io.File
import java.io.FileInputStream
import junit.framework.TestCase
import util.assertSameLines

public class TestReading : TestCase() {
    public fun testReadAllMines() {
        File("mines").recurse{ f ->
            if (f.isFile()) {
                val input = FileInputStream(f)
                val mine = readMine(input)
                input.close()
                assertSameLines(f.readText(), mine.toString())
            }
        }
    }
}