package solver

import io.readMine
import java.lang.Thread.currentThread
import model.Mine

public class Solver {
    private val initialMine: Mine
    private val workerThread = Thread.currentThread()!!;

    {
        initialMine = readMine(System.`in`)
    }

    public fun start() {
        // TODO do something more adequate ;)
        var i = 0
        while (true) {
            i++
            System.err.println("Thinking... $i")
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    public fun interruptAndWriteResult() {
        workerThread.interrupt()
        println("DLLLDA")
    }
}