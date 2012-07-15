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

    // remove last blank lines
    while (lines[lines.size() - 1].isBlankLine()) {
        lines.remove(lines.size() - 1)
    }
    return lines
}

private val validMetadataKeywords = hashSet("Water", "Flooding", "Waterproof", "Trampoline", "Growth", "Razors")

private fun String.isBlankLine(): Boolean {
    for (c in this) {
        if (!Character.isWhitespace(c)) {
            return false
        }
    }
    return true
}

private fun findSeparatorLine(lines: List<String>) : Int {
    // we need to cut all blank and metadata lines from the end
    for (i in 0..lines.size() - 1) {
        val index = lines.size() - i - 1
        val line = lines[index]
        if (line.isBlankLine() || validMetadataKeywords.contains(line.trim().split("\\s")[0])) {
            // blank line, metadata line, okay
        }
        else {
            return if (index == lines.size() - 1) - 1 else index + 1
        }
    }
    return -1
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
            if (line.startsWith("Waterproof")) {
                mine.waterproof = Integer.parseInt(line.trimLeading("Waterproof").trim())
            }
            else if (line.startsWith("Water")) {
                mine.water = Integer.parseInt(line.trimLeading("Water").trim()) - 1 // we count from 0
            }
            else if (line.startsWith("Flooding")) {
                val floodInfos = line.trimLeading("Flooding").trim().split('/')
                _assert(floodInfos.size <= 2, "only one slash is allowed")
                mine.floodPeriod = Integer.parseInt(floodInfos[0])
                mine.nextFlood = if (floodInfos.size == 2) Integer.parseInt(floodInfos[1]) else mine.floodPeriod
            }
            else if (line.startsWith("Trampoline")) {
                val trimmed = line.trimLeading("Trampoline").trim() // e.g., "A targets 1"
                val trampolineId = trimmed[0]
                val targetId = trimmed[trimmed.length() - 1]
                val trampolineLocation = idToLocation[trampolineId]
                if (trampolineLocation == null) {
                    throw IllegalStateException("Could not find trampoline for id: $trampolineId")
                }
                val targetLocation = idToLocation[targetId]
                if (targetLocation == null) {
                    throw IllegalStateException("Could not find target for id: $targetId")
                }
                trampolinesMap.addLink(trampolineLocation, targetLocation)
            }
            else if (line.startsWith("Growth")) {
                val growthInfos = line.trimLeading("Growth").trim().split('/')
                _assert(growthInfos.size <= 2, "only one slash is allowed")
                mine.beardGrowthPeriod = Integer.parseInt(growthInfos[0])
                mine.nextBeardGrowth = if (growthInfos.size == 2) Integer.parseInt(growthInfos[1]) else mine.beardGrowthPeriod
            }
            else if (line.startsWith("Razors")) {
                mine.razors = Integer.parseInt(line.trimLeading("Razors").trim())
            }
        }
    }

    trampolinesMap.checkForOrphanTrampolinesAndTargets()

    return mine
}