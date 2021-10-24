package ai.salmon.computing

import org.apache.commons.math3.stat.inference.TestUtils

trait BaseStatisticTransformer {
  def srm(controlSize: Int, treatmentSize: Int, alpha: Double): Boolean = {
    val uniform = (treatmentSize + controlSize).toDouble / 2
    TestUtils.chiSquareTest(
      Array(uniform, uniform),
      Array(controlSize, treatmentSize),
      alpha
    )
  }
}
