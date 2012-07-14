package evolution

import model.Mine
import model.equalsTo
import java.io.FileInputStream
import io.readMine
import org.junit.Assert.assertEquals
import junit.framework.TestCase
import testUtil.UsefulTestCase
import java.io.File
import io.streamToLines
import junit.framework.Test
import junit.framework.TestSuite
import evaluator.mineUpdate

val END_MINE = "-END MINE"
val ROOT_DIR = File("mines")
val EXTENSION = "evolution"

class EvolutionTest(val file: File, val update: (Mine) -> Mine) : TestCase("test" + file.getName()!!.capitalize()) {
    public override fun runTest() {
        println(file)
        val lines = streamToLines(FileInputStream(file))

        val endMineIndex = lines.indexOf(END_MINE)
        val initialState = readMine(lines.subList(0, endMineIndex))
        val actual = evolution(initialState, update)
        val expected = file.readText()

        assertEquals(expected, actual)
    }
}

public fun suite(): Test {
    return createSuite()
}
public fun createSuite(): Test {
    val suite = TestSuite("Evolution")
    ROOT_DIR.recurse {
        file ->
        if (file.getName()!!.endsWith(".$EXTENSION")) {
            suite.addTest(EvolutionTest(file, {m -> mineUpdate(m)}))
        }
    }
    return suite
}

fun evolution(mine: Mine, update: (Mine) -> Mine): String {
    val result = StringBuilder(mine.toString())
    result.append(END_MINE + "\n")
    var current = mine
    while (true) {
        val new = update(current)
        if (new equalsTo current) {
            break;
        }
        result.append(new)
        result.append(END_MINE + "\n")
        current = new
    }
    return result.toString()!!
}