package evaluator


import model.Mine
import java.io.FileInputStream
import io.readMine
import org.junit.Test as test
import org.junit.Assert.assertEquals

class MineTest() {

    test fun robot1() {
        val robotAtZero = readMine("R")
        assertEquals(0, robotAtZero.robotX)
        assertEquals(0, robotAtZero.robotY)
    }
}