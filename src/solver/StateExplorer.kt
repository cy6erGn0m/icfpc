package solver

import model.Move

abstract class StateExplorer(
        val solver: SolverFramework
) {
    abstract fun processStates(rootStates : Collection<RobotState>, queue: StateQueue, resultsLimit: Int, depth: Int): BestRobotStates
}

class SimpleStateExplorer(
        solver: SolverFramework
) : StateExplorer(solver) {
    override fun processStates(rootStates : Collection<RobotState>, queue: StateQueue, resultsLimit: Int, depth: Int): BestRobotStates {
        for (r in rootStates) {
            queue.push(r)
        }
        val states = BestRobotStates(resultsLimit)

        solver.iterateQueue(queue, depth, states)

        solver.logger.log("Best states ${solver.iteration}")
        for (state in states.bestStates) {
            solver.logger.logNewState(queue, state)
        }
        return states
    }

}

