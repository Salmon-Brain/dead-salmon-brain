package ai.salmon.computing

import helpers.ExperimentDataGenerator.generateDataForWelchTest
import helpers.SparkHelper
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class OutlierRemoveTransformerSpec extends AnyFlatSpec with SparkHelper with Matchers {
  "OutlierRemoveTransformerSpec" should "be" in {
    val data = generateDataForWelchTest()
    val clearData = new OutlierRemoveTransformer().transform(data)
    assert(clearData.count() == 26)
  }
}
