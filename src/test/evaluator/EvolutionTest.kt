package evolution

import io.readMine
import io.serialize
import io.streamToLines
import java.io.File
import java.io.FileInputStream
import junit.framework.TestCase
import model.Mine
import model.equalsTo
import org.junit.Assert.assertEquals
import evaluator.mineUpdateWithFullCopy
import evaluator.incremental.mineUpdateWithIncrementalCopy

abstract class AbstractEvolutionTest(): TestCase() {
    val mineUpdateWithFullCopyStrategy = {(m: Mine) -> mineUpdateWithFullCopy(m) }
    val mineUpdateWithIncrementalCopyStrategy = {(m: Mine) -> mineUpdateWithIncrementalCopy(m) }

    public fun doTest(path: String, update: (Mine) -> Mine) {
        val file = File(path)
        println(file)
        val lines = streamToLines(FileInputStream(file))

        val endMineIndex = lines.indexOf(END_MINE)
        val initialState = readMine(lines.subList(0, endMineIndex))
        val actual = evolution(initialState, update)
        val expected = file.readText()

        assertEquals(expected, actual)
    }

}

val END_MINE = "-END MINE"

fun evolution(mine: Mine, update: (Mine) -> Mine): String {
    val result = StringBuilder(mine.serialize())
    result.append(END_MINE + "\n")
    var current = mine
    while (true) {
        val new = update(current)
        if (new equalsTo current) {
            break;
        }
        result.append(new.serialize())
        result.append(END_MINE + "\n")
        current = new
    }
    return result.toString()
}

