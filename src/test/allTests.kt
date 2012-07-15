package test

import junit.framework.Test
import junit.framework.TestSuite
import evaluator.EvaluatorTest
import evaluator.MineTest
import evaluator.MovesTest
import io.TestReading
import evolution.createSuite
import evaluator.incremental.mineUpdateWithIncrementalCopy
import evaluator.mineUpdateWithFullCopy
import test.solver.BestRobotStatesTest

public fun suite(): Test {
    val suite = TestSuite()

    suite.addTestSuite(javaClass<EvaluatorTest>())
    suite.addTestSuite(javaClass<MineTest>())
    suite.addTestSuite(javaClass<MovesTest>())
    suite.addTestSuite(javaClass<TestReading>())
    suite.addTestSuite(javaClass<BestRobotStatesTest>())
    suite.addTest(createSuite("Evolution") {m -> mineUpdateWithFullCopy(m)})
//    suite.addTest(createSuite("Incremental Evolution") {m -> mineUpdateWithIncrementalCopy(m)})

    return suite
}