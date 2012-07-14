package test.minegen

import model.CellMatrix
import java.util.List
import java.util.Random
import util._assert
import model.ArrayCellMatrix
import model.MineCell
import java.util.Collections
import java.util.ArrayList
import java.util.Collection
import model.Mine
import model.count
import model.contains
import java.io.File

val DEBUG = false

class MineGenerator(private val seed: Long = 1234567) {

    private val random = Random(seed)

    private class RandomSequence(
            val length: Int,
            val pieces: Collection<CellMatrix>,
            val random: Random
    ) {
        val chunks = run {
            val n = length / pieces.size
            if (n == 0) 1 else n
        }
        val chunkWithRobot = random.nextInt() mod chunks
        val chunkWithLift = random.nextInt() mod chunks + 1 // there may be no lift of the map

        var liftReturned = false

        val initialPieces = ArrayList<CellMatrix>(pieces)
        val shuffledPieces = ArrayList<CellMatrix>(pieces.size)
        var currentChunk = 0
        var offsetInChunk = 0

        fun next(): CellMatrix {
            if (offsetInChunk == 0) {
                shuffledPieces.clear()
                shuffledPieces.addAll(pieces)
                Collections.shuffle(shuffledPieces, random)
            }
            val result = shuffledPieces[offsetInChunk]
            offsetInChunk++
            if (offsetInChunk >= pieces.size()) {
                offsetInChunk = 0
                currentChunk++
            }
            val robotThere = MineCell.ROBOT in result
            val liftThere = MineCell.CLOSED_LIFT in result || MineCell.OPEN_LIFT in result
            if (robotThere) {
                if (currentChunk == chunkWithRobot) {
                    liftReturned = liftReturned || liftThere
                    return result
                }
                return next()
            }

            if (liftThere && currentChunk != chunkWithLift) {
                return next()
            }

            return result
        }

    }

    fun generateMineMatrix(pieces: Collection<CellMatrix>, widthInPieces: Int, heightInPieces: Int): CellMatrix? {
        val matrix = doGenerateMineMatrix(pieces, widthInPieces, heightInPieces)
        // Check: one robot, one lift, has lambdas <-> lift closed
//        return matrix
        if (matrix.count(MineCell.ROBOT) == 1) {
            val lambdas = matrix.count(MineCell.LAMBDA)
            if (lambdas == 0) {
                val closedLifts = matrix.count(MineCell.CLOSED_LIFT)
                if (closedLifts == 1) {
                    return matrix
                }
                if (DEBUG) {
                    println("===================================")
                    println("Generated:")
                    println(Mine(matrix))
                    println("No lambdas")
                    println("Closed lifts: $closedLifts")
                    println("!!!!!!!!!!! Rejected")
                }
            }
            else {
                val openLifts = matrix.count(MineCell.CLOSED_LIFT)
                if (openLifts == 1) {
                    return matrix
                }
                if (DEBUG) {
                    println("===================================")
                    println("Generated:")
                    println(Mine(matrix))
                    println("$lambdas lambdas")
                    println("Open lifts: $openLifts")
                    println("!!!!!!!!!!! Rejected")
                }
            }
        }
        return null
    }

    private fun doGenerateMineMatrix(pieces: Collection<CellMatrix>, widthInPieces: Int, heightInPieces: Int): CellMatrix {
        _assert(pieces.size() > 0, "Can't generate mine: no pieces")
        // We assume all pieces to be of the same size
        val pieceW = pieces.first().width
        val pieceH = pieces.first().height

        val effectiveW = pieceW * widthInPieces + 2 // for the border of walls
        val effectiveH = pieceH * heightInPieces + 2

        val matrix = ArrayCellMatrix(effectiveW, effectiveH)
        for (x in 0..effectiveW - 1) {
            matrix[x, 0] = MineCell.WALL
            matrix[x, effectiveH - 1] = MineCell.WALL
        }
        for (y in 0..effectiveH - 1) {
            matrix[0, y] = MineCell.WALL
            matrix[effectiveW - 1, y] = MineCell.WALL
        }

        val seq = RandomSequence(widthInPieces * heightInPieces, pieces, random)
        for (px in 0..widthInPieces - 1) {
            for (py in 0..heightInPieces - 1) {
                matrix.putPiece(1 + px, 1 + py, seq.next())
            }
        }

        return matrix
    }

    fun CellMatrix.putPiece(startX: Int, startY: Int, piece: CellMatrix) {
        for (x in 0..piece.width - 1) {
            for (y in 0..piece.height - 1) {
                this[startX + x, startY + y] = piece[x, y]
            }
        }
    }

}

fun main(args: Array<String>) {
    fun piece(cell: MineCell): CellMatrix {
        val r = ArrayCellMatrix(1, 1)
        r[0, 0] = cell
        return r
    }

    val cells = ArrayList(model.validCells)
    cells.remove(MineCell.OPEN_LIFT)
    cells.add(MineCell.EMPTY)
    cells.add(MineCell.EMPTY)
    cells.add(MineCell.EMPTY)
    cells.add(MineCell.EMPTY)

    val littlePieces = cells.map<MineCell, CellMatrix> {
        cell -> piece(cell)
    }


    val mineGenerator = MineGenerator()
    var k = 0
    for (N in 5..100 step 5) {
        var i = 0

        while (i < 10) {
            val matrix = mineGenerator.generateMineMatrix(littlePieces, N, N)
            if (matrix != null) {
                val file = File("mines/random/mine${k}_${N}x${N}.map")
                file.writeText(Mine(matrix).toString())
                println("Done: $file")
                i++
                k++
            }
            else {
                println("$N x $N failed")
            }
        }
    }
}