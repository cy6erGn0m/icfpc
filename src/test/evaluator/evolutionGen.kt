package evolution

import java.io.File
import io.readMine
import evaluator.mineUpdateWithFullCopy

fun main(args: Array<String>) {
    File("mines").walkTopDown().filter { it.name.endsWith(".map") }.forEach {
        val relativePath = it.path.substring("mines".length)
        val outFile = File("mines/evolution/$relativePath.evolution")
        outFile.parentFile!!.mkdirs()
        outFile.writeText(evolution(readMine(it), {m -> mineUpdateWithFullCopy(m)}))
        println(outFile)
    }
}
