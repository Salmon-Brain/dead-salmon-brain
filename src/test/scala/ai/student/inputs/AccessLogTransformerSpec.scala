package ai.student.inputs;

import ai.student.computing.ExpData
import helpers.SparkHelper
import org.apache.spark.sql.DataFrame
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class AccessLogTransformerSpec extends AnyFlatSpec with SparkHelper with Matchers {

  "AccessLogTransformer" should "be" in {

    import sqlc.implicits._
    val logsDF: DataFrame = sc
      .parallelize(
        Seq(
          ("user1", 1L, "/view/1"),
          ("user1", 2L, "/view/1"),
          ("user1", 3L, "/view/2"),
          ("user1", 4L, "/other/2"),
          ("user1", 5L, "/click/2"),
          ("user2", 7L, "/view/1"),
          ("user2", 8L, "/click/1"),
          ("user4", 9L, "/view/1"),
          ("user5", 10L, "/other/1")
        )
      )
      .toDF("entityUid", "timestamp", "path")

    val transformer = new AccessLogTransformer()
      .setMetricNameExpression(
        "case when regexp_extract(path, '^(/click/)') = '/click/' then 'click' else " +
          "case when regexp_extract(path, '^(/view/)') = '/view/' then 'view' else 'none' end end"
      )
      .setMetricValueExpression("1.0")
      .setExpUidExpression("'exp1'")
      .setVariantIdExpression(
        "case when regexp_extract(entityUid, '([0-9])$') = 1 then 'treatment' else 'control' end"
      )

    val metrics = transformer.transform(logsDF)

    val expected = sc
      .parallelize(
        Seq(
          ExpData(1L, "treatment", "user1", "exp1", 1.0, "view"),
          ExpData(2L, "treatment", "user1", "exp1", 1.0, "view"),
          ExpData(3L, "treatment", "user1", "exp1", 1.0, "view"),
          ExpData(5L, "treatment", "user1", "exp1", 1.0, "click"),
          ExpData(7L, "control", "user2", "exp1", 1.0, "view"),
          ExpData(8L, "control", "user2", "exp1", 1.0, "click"),
          ExpData(9L, "control", "user4", "exp1", 1.0, "view")
        )
      )
      .toDF()

    metrics.collect() should contain theSameElementsAs (expected.collect())

  }
}
