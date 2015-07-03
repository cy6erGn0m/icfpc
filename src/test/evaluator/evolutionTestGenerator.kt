package evolution.generator

import java.io.File

fun main(args: Array<String>) {
    val rootDir = File("src/test/evaluator/")
    val evolutionFile = File(rootDir, "EvolutionTestGenerated.kt")
    val incEvolutionFile = File(rootDir, "IncrementalEvolutionTestGenerated.kt")
    if (!evolutionFile.exists()) evolutionFile.createNewFile()
    if (!incEvolutionFile.exists()) incEvolutionFile.createNewFile()

    val evSb = StringBuilder()
    val incEvSb = StringBuilder()
    evSb.println("package evolution")
    evSb.println()
    evSb.println("class EvolutionTestGenerated: AbstractEvolutionTest() {")
    evSb.println()

    incEvSb.println("package evolution")
    incEvSb.println()
    incEvSb.println("class IncrementalEvolutionTestGenerated: AbstractEvolutionTest() {")
    incEvSb.println()

    File("mines").recurse {
        file ->
        if (file.getName().endsWith(".evolution")) {
            evSb.println("    fun testEvolution_${file.path.pathToTestName()}() {")
            evSb.println("        doTest(\"${file.path.toSystemIndependentPath()}\", mineUpdateWithFullCopyStrategy)")
            evSb.println("    }")
            evSb.println()

            incEvSb.println("    fun testIncrementalEvolution_${file.path.pathToTestName()}() {")
            incEvSb.println("        doTest(\"${file.path.toSystemIndependentPath()}\", mineUpdateWithIncrementalCopyStrategy)")
            incEvSb.println("    }")
            incEvSb.println()
        }
    }

    evSb.println("}")
    incEvSb.println("}")

    evolutionFile.writeText(evSb.toString())
    incEvolutionFile.writeText(incEvSb.toString())
}

fun String.pathToTestName(): String {
    return this.toSystemIndependentPath().replace('/', '_').replace('.', '_').replace('-', '_')
}

val NEWLINE = System.getProperty("line.separator")

fun StringBuilder.println(str: String = "") {
    this.append(str).append(NEWLINE)
}

fun String.toSystemIndependentPath(): String {
    return this.replace('\\', '/')
}

