package io

import java.io.InputStream
import java.util.ArrayList
import java.util.List
import model.Mine
import model.toMineCell
import model.MineCell

public fun readMine(input: InputStream): Mine {
    val lines = ArrayList<String>()
    input.reader().buffered().forEachLine { line ->
        lines.add(line)
    }
    return readMine(lines)
}

public fun readMine(string: String): Mine {
    val lines = string.split('\n').toList()
    return readMine(lines)
}

public fun readMine(lines: List<String>): Mine {
    val height = lines.size()
    val lengths: List<Int> = lines.map { it -> it.length }
    val width = lengths.fold(0, { x, y -> Math.max(x, y) })

    val mine = Mine(width, height)
    for (y in 0..(height - 1)) {
        val line = lines[height - y - 1]
        for (x in 0..(width - 1)) {
            mine[x, y] = if (x < line.length()) line[x].toMineCell() else MineCell.EMPTY
        }
    }
    return mine
}