package ai.salmon.inputs;

import ai.salmon.computing.{ExpData, Variants}
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
          ExpData(1L, Variants.Treatment, "user1", "exp1", 1.0, "view"),
          ExpData(2L, Variants.Treatment, "user1", "exp1", 1.0, "view"),
          ExpData(3L, Variants.Treatment, "user1", "exp1", 1.0, "view"),
          ExpData(5L, Variants.Treatment, "user1", "exp1", 1.0, "click"),
          ExpData(7L, Variants.Control, "user2", "exp1", 1.0, "view"),
          ExpData(8L, Variants.Control, "user2", "exp1", 1.0, "click"),
          ExpData(9L, Variants.Control, "user4", "exp1", 1.0, "view")
        )
      )
      .toDF()

    metrics.collect() should contain theSameElementsAs (expected.collect())

  }
}
