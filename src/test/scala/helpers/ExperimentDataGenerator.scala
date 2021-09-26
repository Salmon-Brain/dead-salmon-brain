package helpers

import ai.student.computing.ExpData
import org.apache.commons.math3.distribution.{
  BetaDistribution,
  BinomialDistribution,
  NormalDistribution
}
import org.apache.commons.math3.random.Well19937a
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.from_unixtime

import java.sql.Timestamp
import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS
import scala.util.Random

object ExperimentDataGenerator extends SparkHelper {
  import spark.implicits._
  val randomGenerator = new Well19937a(777)

  def generateDataForWelchTest(): DataFrame = {
    //sigma = 1,N = 10
    val controlMetricValues = Seq(19.8, 20.4, 19.6, 17.8, 18.5, 18.9, 18.3, 18.9, 19.5, 22)
    //sigma = 16, N = 20
    val treatmentMetricValues = Seq(28.2, 26.6, 20.1, 23.3, 25.2, 22.1, 17.7, 27.6, 20.6, 13.7,
      23.2, 17.5, 20.6, 18, 23.9, 21.6, 24.3, 20.4, 24, 13.2)

    val controlData = controlMetricValues.zipWithIndex.map { case (value, idx) =>
      ExpData(System.currentTimeMillis(), "control", idx.toString, "exp", value, "timeSpent")
    }

    val treatmentData = treatmentMetricValues.zipWithIndex.map { case (value, idx) =>
      ExpData(
        System.currentTimeMillis(),
        "treatment",
        (idx + controlMetricValues.length).toString,
        "exp",
        value,
        "timeSpent"
      )
    }

    seqExpDataToDataFrame(controlData ++ treatmentData)
  }

  def experimentDataGenerator(
      controlSize: Int = 1000,
      treatmentSize: Int = 1000,
      controlSkew: Double = 1,
      treatmentSkew: Double = 1,
      successRate: Double = 0.02,
      uplift: Double = 0.1,
      beta: Double = 250,
      daysBefore: Int = 10,
      withAggregation: Boolean = true
  ): Seq[ExpData] = {

    val dates = randomDate(LocalDate.now().minusDays(daysBefore), LocalDate.now()).iterator
    val viewsControl = randomViewsDataGenerator(controlSize, controlSkew)
    val viewsTreatment = randomViewsDataGenerator(treatmentSize, treatmentSkew)
    val alphaControl = successRate * beta / (1 - successRate)
    val alphaTreatment = successRate * (1 + uplift) * beta / (1 - successRate * (1 + uplift))
    val clicksControl =
      randomClicksDataGenerator(
        viewsControl,
        randomClickProbDataGenerator(alphaControl, beta, controlSize)
      )
    val clicksTreatment =
      randomClicksDataGenerator(
        viewsTreatment,
        randomClickProbDataGenerator(alphaTreatment, beta, treatmentSize)
      )

    val controlViewsMetricValues = viewsControl.zipWithIndex.flatMap { case (value, idx) =>
      expandByValue(
        ExpData(dates.next(), "control", idx.toString, "exp", value, "views"),
        withAggregation
      )
    }

    val controlClicksMetricValues = clicksControl.zipWithIndex.flatMap { case (value, idx) =>
      expandByValue(
        ExpData(dates.next(), "control", idx.toString, "exp", value, "clicks"),
        withAggregation
      )
    }

    val treatmentViewsMetricValues = viewsTreatment.zipWithIndex.flatMap { case (value, idx) =>
      expandByValue(
        ExpData(
          dates.next(),
          "treatment",
          (idx + controlClicksMetricValues.length).toString,
          "exp",
          value,
          "views"
        ),
        withAggregation
      )
    }

    val treatmentClicksMetricValues = clicksTreatment.zipWithIndex.flatMap { case (value, idx) =>
      expandByValue(
        ExpData(
          dates.next(),
          "treatment",
          (idx + controlClicksMetricValues.length).toString,
          "exp",
          value,
          "clicks"
        ),
        withAggregation
      )
    }

    controlViewsMetricValues ++
      controlClicksMetricValues ++
      treatmentClicksMetricValues ++
      treatmentViewsMetricValues
  }

  def expandByValue(exp: ExpData, isNotExpand: Boolean): Seq[ExpData] = {
    val num = exp.metricValue.toInt
    num match {
      case x if x == 0 || x == 1 || isNotExpand => Seq(exp)
      case _ =>
        (0 until num).map(_ =>
          ExpData(exp.timestamp, exp.variantId, exp.entityUid, exp.expUid, 1, exp.metricName)
        )
    }
  }

  def seqExpDataToDataFrame(data: Seq[ExpData]): DataFrame = {
    sc
      .parallelize(data)
      .toDF
      .withColumn("date", from_unixtime($"timestamp" / 1000).cast("date"))
  }

  def randomViewsDataGenerator(size: Int, skew: Double): Seq[Int] = {
    val normalDistribution = new NormalDistribution(randomGenerator, 1, skew)
    normalDistribution.sample(size).map(p => Math.abs(Math.exp(p).toInt))
  }

  def randomClickProbDataGenerator(alpha: Double, beta: Double, size: Int): Seq[Double] = {
    new BetaDistribution(randomGenerator, alpha, beta).sample(size)
  }

  def randomClicksDataGenerator(views: Seq[Int], clickProbability: Seq[Double]): Seq[Int] = {
    clickProbability.zip(views).map { case (prob, views) =>
      new BinomialDistribution(randomGenerator, views, prob).sample()
    }
  }

  def randomDate(from: LocalDate, to: LocalDate): Stream[Long] = {
    val diff = DAYS.between(from, to)
    val random = new Random(System.nanoTime)
    val randomDay = from.plusDays(random.nextInt(diff.toInt))
    val timestamp = Timestamp.valueOf(randomDay.atStartOfDay())
    timestamp.getTime #:: randomDate(
      from,
      to
    )
  }
}
