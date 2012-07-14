package evolution.incrementalTest

import junit.framework.Test
import evolution.createSuite
import evaluator.incremental.mineUpdateWithIncrementalCopy

public fun suite(): Test {
    return createSuite("Incremental Evolution") {m -> mineUpdateWithIncrementalCopy(m)}
}