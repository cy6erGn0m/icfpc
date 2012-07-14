package evolution.incrementalTest

import junit.framework.Test
import evolution.createSuite
import evaluator.incremental.incrementalMineUpdate

public fun suite(): Test {
    return createSuite {m -> incrementalMineUpdate(m)}
}