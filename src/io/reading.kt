package io

import java.io.InputStream
import java.util.List
import model.Mine
import model.MineCell
import model.toMineCell
import util._assert

public fun readMine(input: InputStream): Mine {
    return readMine(streamToLines(input))
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

private fun findSeparatorLine(lines: List<String>) : Int {
    val lastBlankLine = lines.indexOf("")
    if (lastBlankLine == -1) {
        return -1
    }
    else {
        for (i in lastBlankLine + 1 .. lines.size() - 1) {
            if (!lines[i].matches("\\w+\\s+[0-9/]+\\s*")) {
                return -1
            }
        }
    }
    return lastBlankLine
}

public fun readMine(lines: List<String>): Mine {
    val separatorLine = findSeparatorLine(lines)

    val height = if (separatorLine == -1) lines.size() else separatorLine
    val lengths: List<Int> = lines.map { it -> it.length }
    val width = lengths.fold(0, { x, y -> Math.max(x, y) })

    val mine = Mine(width, height)
    for (y in 0..(height - 1)) {
        val line = lines[height - y - 1]
        for (x in 0..(width - 1)) {
            mine[x, y] = if (x < line.length()) line[x].toMineCell() else MineCell.EMPTY
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
            else {
                throw IllegalArgumentException("Don't know how to parse line: " + line)
            }
        }
    }

    return mine
}