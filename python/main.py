from pyspark.sql import SparkSession

from ai.salmon.computing import CumulativeMetricTransformer, WelchStatisticsTransformer

spark = (
    SparkSession.builder.appName("Python Spark SQL basic example")
    .config(
        "spark.jars",
        "../computing/build/libs/computing-0.0.1.jar",
    )
    .getOrCreate()
)

cum = CumulativeMetricTransformer()
welch = WelchStatisticsTransformer()

data = spark.createDataFrame(
    [
        ("feedback", "1", "exp", "treatment", 1.0, "ts", False),
        ("feedback", "2", "exp", "treatment", 10.0, "ts", False),
        ("feedback", "3", "exp", "control", 1.0, "ts", False),
        ("feedback", "4", "exp", "control", 15.0, "ts", False),
    ],
    [
        "metricSource",
        "entityUid",
        "expUid",
        "variantId",
        "metricValue",
        "metricName",
        "isAdditive",
    ],
)
welch.transform(cum.transform(data)).select("statisticsData.*").show()
