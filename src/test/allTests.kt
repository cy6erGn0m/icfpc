package test

import junit.framework.Test
import junit.framework.TestSuite
import evaluator.EvaluatorTest
import evaluator.MineTest
import evaluator.MovesTest
import io.TestReading
import evolution.createSuite
import evaluator.incremental.incrementalMineUpdate

public fun suite(): Test {
    val suite = TestSuite()

    suite.addTestSuite(javaClass<EvaluatorTest>())
    suite.addTestSuite(javaClass<MineTest>())
    suite.addTestSuite(javaClass<MovesTest>())
    suite.addTestSuite(javaClass<TestReading>())
    suite.addTest(createSuite())
    suite.addTest(createSuite {m -> incrementalMineUpdate(m)})

    return suite
}