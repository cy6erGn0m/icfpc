package test

import junit.framework.Test
import junit.framework.TestSuite
import evaluator.EvaluatorTest
import evaluator.MineTest
import evaluator.MovesTest
import io.TestReading

public fun suite(): Test {
    val suite = TestSuite()

    suite.addTestSuite(javaClass<EvaluatorTest>())
    suite.addTestSuite(javaClass<MineTest>())
    suite.addTestSuite(javaClass<MovesTest>())
    suite.addTestSuite(javaClass<TestReading>())

    return suite
}