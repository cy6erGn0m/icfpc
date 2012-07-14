package evolution

import java.io.File
import io.readMine
import evaluator.mineUpdateWithFullCopy

fun main(args: Array<String>) {
    ROOT_DIR.recurse {
    file ->
        if (file.getName()!!.endsWith(".map")) {
            val relativePath = file.getPath()!!.substring("mines".size)
            val outFile = File("mines/evolution/${relativePath}.$EXTENSION")
            outFile.getParentFile()!!.mkdirs()
            outFile.writeText(evolution(readMine(file), {m -> mineUpdateWithFullCopy(m)}))
            println(outFile)
        }
    }
}
