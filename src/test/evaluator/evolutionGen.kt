package evolution

import java.io.File
import io.readMine
import evaluator.mineUpdateWithFullCopy

fun main(args: Array<String>) {
    File("mines").recurse {
    file ->
        if (file.getName().endsWith(".map")) {
            val relativePath = file.getPath().substring("mines".length())
            val outFile = File("mines/evolution/${relativePath}.evolution")
            outFile.getParentFile()!!.mkdirs()
            outFile.writeText(evolution(readMine(file), {m -> mineUpdateWithFullCopy(m)}))
            println(outFile)
        }
    }
}
