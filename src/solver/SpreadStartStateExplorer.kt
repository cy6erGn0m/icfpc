package solver

import java.util.ArrayList
import util._assert
import model.MineCell
import model.Point
import model.Move

class SpreadStartStateExplorer(
        solver: SolverFramework
) : StateExplorer(solver) {
    class Clusters(val perClusterLimit: Int): StateAcceptor {
        inner class Cluster(
                val minx: Int,
                val maxx: Int,
                val miny: Int,
                val maxy: Int
        ) {
            val best = BestRobotStates(perClusterLimit)

            fun contains(state: RobotState): Boolean {
                return (state.robot.x in minx..maxx) &&
                (state.robot.y in miny..maxy)
            }
        }

        val clusters = ArrayList<Cluster>()

        fun addCluster(minx: Int, maxx: Int, miny: Int, maxy: Int) {
            clusters.add(Cluster(minx, maxx, miny, maxy))
        }

        override fun add(state: RobotState) {
            var added = false
            for (c in clusters) {
                if (c.contains(state)) {
                    c.best.add(state)
                    added = true
                    break // todo ???
                }
            }
            _assert(added, "Not added: ${state.robot.pos}")
        }
    }

    fun findClusters(state: RobotState, depth: Int, totalLimit: Int): Clusters {
        val mine = state.robot.mine
        val cx = state.robot.x
        val cy = state.robot.y

        val trampolines = mine.getPointsOfType(MineCell.TRAMPOLINE)
        val targets = ArrayList<Point>()
        for (tpos in trampolines) {
            if (tpos.x in (cx - depth)..(cx + depth)
            && tpos.y in (cy - depth)..(cy + depth)) {
                val map = mine.trampolinesMap
                val target = map.getTarget(tpos)
                targets.add(target)
            }
        }

        val clusters = Clusters(totalLimit / (4 + targets.size))

        clusters.addCluster(cx - depth, cx, cy - depth, cy)
        clusters.addCluster(cx - depth, cx, cy + 1, cy + depth)
        clusters.addCluster(cx + 1, cx + depth, cy - depth, cy)
        clusters.addCluster(cx + 1, cx + depth, cy + 1, cy + depth)

        for (target in targets) {
            clusters.addCluster(target.x - depth, target.x + depth, target.y - depth, target.y + depth)
        }
        return clusters
    }

    override fun processStates(rootStates: Collection<RobotState>, queue: StateQueue, resultsLimit: Int, depth: Int): BestRobotStates {
        // We assume that rootStates is sorted in the ascending order
        _assert(queue.isEmpty(), "Queue must be empty")
        val finallyBestStates = BestRobotStates(resultsLimit)

        if (rootStates.size == 1) {
            val totalLimit = resultsLimit * 11
            val finallyBestStates = BestRobotStates(totalLimit)
            val state = rootStates.iterator().next()
            val clusters = findClusters(state, depth, totalLimit)
            queue.push(state)
            solver.iterateQueue(queue, depth, clusters)

            for (c in clusters.clusters) {
                for (state in c.best.getBestStates()) {
                    finallyBestStates.add(state)
                }
            }
            return finallyBestStates
        }
        else {
            for (root in rootStates) {
                if (solver.needToTerminateFlag) {
                    return finallyBestStates
                }
                queue.push(root)
                val states = BestRobotStates(resultsLimit)
                solver.iterateQueue(queue, depth, states)
                for (best in states.getBestStates()) {
                    finallyBestStates.add(best)
                }

            }
        }


        solver.logger.log("Best states ${solver.iteration}")
        for (state in finallyBestStates.bestStates) {
            solver.logger.logNewState(queue, state)
        }
        return finallyBestStates
    }

}