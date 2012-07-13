package evaluator

import model.Mine
import java.io.FileInputStream
import io.readMine
import org.junit.Test as test
import org.junit.Assert.assertEquals

class EvaluatorTest {

    fun loadMine(fileName: String): Mine {
        return readMine(FileInputStream(fileName))
    }

    test fun test1() {
        val inFile = "mines/evaluator/case1-in.map"
        val outFile = "mines/evaluator/case1-out.map"
        val inMine = loadMine(inFile)
        val outMine = loadMine(outFile)
        val actual = mapUpdate(inMine)
        assertEquals(outMine.toString(), actual.toString())
    }

}