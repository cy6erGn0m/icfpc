package io

import java.util.HashMap
import model.Point
import model.MineCell
import model.Mine

public fun Mine.serialize() : String {
    val sb = StringBuilder()
    var unresolvedTrampolinesToLocations = HashMap<Char, Point>()
    for (yy in 0..height - 1) {
        val y = height - yy - 1
        for (x in 0..width - 1) {
            val cell = this[x, y]
            if (cell == MineCell.TRAMPOLINE || cell == MineCell.TARGET) {
                val location = Point(x, y)
                val id = trampolinesMap.getId(location)
                if (cell == MineCell.TRAMPOLINE) {
                    unresolvedTrampolinesToLocations[id] = location
                }
                sb.append(id)
            }
            else {
                sb.append(cell)
            }
        }
        sb.append("\n")
    }

    val waterMetadataPresent = !(water == -1 && floodPeriod == 0 && waterproof == 10)
    val trampolinesMetadataPresent = !unresolvedTrampolinesToLocations.isEmpty()
    val beardMetadataPresent = !(beardGrowthPeriod == 25 && razors == 0)

    if (waterMetadataPresent || trampolinesMetadataPresent || beardMetadataPresent) {
        sb.append("\n")
    }

    if (waterMetadataPresent) {
        sb.append("Water ${water + 1}\n")
        sb.append("Flooding $floodPeriod${ if (floodPeriod != nextFlood) "/" + nextFlood else "" }\n")
        sb.append("Waterproof $waterproof\n")
    }

    if (trampolinesMetadataPresent) {
        for (trampolineId in 'A'..'I') {
            val trampolineLocation = unresolvedTrampolinesToLocations[trampolineId]
            if (trampolineLocation == null) continue
            val targetLocation = trampolinesMap.getTarget(trampolineLocation)
            val targetId = trampolinesMap.getId(targetLocation)
            sb.append("Trampoline $trampolineId targets $targetId\n")
        }
    }

    if (beardMetadataPresent) {
        sb.append("Growth $beardGrowthPeriod${ if (beardGrowthPeriod != nextBeardGrowth) "/" + nextBeardGrowth else "" }\n")
        sb.append("Razors $razors\n")
    }

    return sb.toString()!!
}