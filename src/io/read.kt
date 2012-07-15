package io

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.HashMap
import java.util.List
import model.*
import util._assert

public fun readMine(input: InputStream): Mine {
    return readMine(streamToLines(input))
}

public fun readMine(input: File): Mine {
    return readMine(FileInputStream(input))
}

public fun readMine(string: String): Mine {
    return readMine(textToLines(string))
}

public fun streamToLines(input: InputStream): List<String> {
    return textToLines(input.reader().readText())
}

private fun textToLines(text: String): List<String> {
    val lines = text.replace("\r\n", "\n").split('\n').toList()

    // remove last empty lines
    while (lines[lines.size() - 1].isEmpty()) {
        lines.remove(lines.size() - 1)
    }
    return lines
}

private val validMetadataKeywords = hashSet("Water", "Flooding", "Waterproof", "Trampoline", "Growth", "Razors")

private fun findSeparatorLine(lines: List<String>) : Int {
    val lastBlankLine = lines.lastIndexOf("")
    if (lastBlankLine == -1) {
        return -1
    }
    else {
        for (i in lastBlankLine + 1 .. lines.size() - 1) {
            val line = lines[i]
            val space = line.substring(0, Math.min(15, line.length)).indexOf(" ")
            if (!(line[0].isUpperCase() && space != -1 && validMetadataKeywords.contains(line.substring(0, space)))) {
                return -1
            }
        }
    }
    return lastBlankLine
}

public fun readMine(lines: List<String>): Mine {
    val separatorLine = findSeparatorLine(lines)

    val height = if (separatorLine == -1) lines.size() else separatorLine
    val lengths: List<Int> = lines.subList(0, height).map { it -> it.length }
    val width = lengths.fold(0, { x, y -> Math.max(x, y) })

    val trampolinesMap = TrampolinesMap()
    val idToLocation = HashMap<Char, Point>()
    val mine = Mine(width, height, trampolinesMap)
    for (y in 0..(height - 1)) {
        val line = lines[height - y - 1]
        for (x in 0..(width - 1)) {
            if (x < line.length()) {
                val c = line[x]
                val point = Point(x, y)
                if (c.isTrampolineId || c.isTargetId) {
                    idToLocation[c] = point
                    trampolinesMap.addId(c, point)
                    mine[x, y] = if (c.isTrampolineId) MineCell.TRAMPOLINE else MineCell.TARGET
                } else {
                    mine[x, y] = c.toMineCell()
                }
            }
            else mine[x, y] = MineCell.EMPTY
        }
    }

    if (separatorLine != -1) { // have metadata
        for (i in separatorLine + 1..(lines.size() - 1)) {
            val line = lines[i].trim()
            if (line.startsWith("Water ")) {
                mine.water = Integer.parseInt(line.trimLeading("Water ")) - 1 // we count from 0
            }
            else if (line.startsWith("Flooding ")) {
                val floodInfos = line.trimLeading("Flooding ").split('/')
                _assert(floodInfos.size <= 2, "only one slash is allowed")
                mine.floodPeriod = Integer.parseInt(floodInfos[0])
                mine.nextFlood = if (floodInfos.size == 2) Integer.parseInt(floodInfos[1]) else mine.floodPeriod
            }
            else if (line.startsWith("Waterproof ")) {
                mine.waterproof = Integer.parseInt(line.trimLeading("Waterproof "))
            }
            else if (line.startsWith("Trampoline ")) {
                val trampolineId = line.trimLeading("Trampoline ")[0]
                val targetId = line.trimLeading("Trampoline $trampolineId targets ")[0]
                val trampolineLocation = idToLocation[trampolineId]
                if (trampolineLocation == null) {
                    throw IllegalStateException("Could not find trampoline for id: $trampolineId")
                }
                val targetLocation = idToLocation[targetId]
                trampolinesMap.addLink(trampolineLocation, targetLocation!!)
            }
            else if (line.startsWith("Growth ")) {
                val growthInfos = line.trimLeading("Growth ").split('/')
                _assert(growthInfos.size <= 2, "only one slash is allowed")
                mine.beardGrowthPeriod = Integer.parseInt(growthInfos[0])
                mine.nextBeardGrowth = if (growthInfos.size == 2) Integer.parseInt(growthInfos[1]) else mine.beardGrowthPeriod
            }
            else if (line.startsWith("Razors ")) {
                mine.razors = Integer.parseInt(line.trimLeading("Razors "))
            }
            else {
                throw IllegalArgumentException("Don't know how to parse line: " + line)
            }
        }
    }

    trampolinesMap.checkForOrphanTrampolinesAndTargets()

    return mine
}