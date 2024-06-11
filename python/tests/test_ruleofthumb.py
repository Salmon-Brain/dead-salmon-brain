import pytest
from pyspark.sql import SparkSession, DataFrame

from ai.salmonbrain.ruleofthumb import (
    CumulativeMetricTransformer,
    WelchStatisticsTransformer,
    OutlierRemoveTransformer,
    AutoStatisticsTransformer,
)


@pytest.fixture(scope="session")
def path(pytestconfig):
    return pytestconfig.getoption("path")


@pytest.fixture(scope="session")
def data_sample_for_outlier(spark: SparkSession):
    return spark.createDataFrame(
        [
            ("common", "all", "feedback", "1", "exp", "treatment", i, "shows", True)
            for i in range(100)
        ] + [
            ("common", "all", "feedback", "1", "exp", "treatment", i, "clicks", True)
            for i in range(100)
        ],
        [
            "categoryName",
            "categoryValue",
            "metricSource",
            "entityUid",
            "experimentUid",
            "variantId",
            "metricValue",
            "metricName",
            "isAdditive",
        ],
    )


@pytest.fixture(scope="session")
def data_sample(spark: SparkSession):
    return spark.createDataFrame(
        [
            ("common", "all", "feedback", "1", "exp", "treatment", 11.0, "shows", True),
            ("common", "all", "feedback", "1", "exp", "treatment", 3.0, "likes", True),
            ("common", "all", "feedback", "1", "exp", "treatment", 11.0, "shows", True),
            ("common", "all", "feedback", "1", "exp", "treatment", 3.0, "likes", True),
            ("common", "all", "feedback", "2", "exp", "treatment", 10.0, "shows", True),
            ("common", "all", "feedback", "2", "exp", "treatment", 5.0, "likes", True),
            ("common", "all", "feedback", "2", "exp", "treatment", 10.0, "shows", True),
            ("common", "all", "feedback", "2", "exp", "treatment", 5.0, "likes", True),
            ("common", "all", "feedback", "3", "exp", "treatment", 10.0, "shows", True),
            ("common", "all", "feedback", "3", "exp", "treatment", 5.0, "likes", True),
            ("common", "all", "feedback", "4", "exp", "treatment", 10.0, "shows", True),
            ("common", "all", "feedback", "4", "exp", "treatment", 5.0, "likes", True),
            ("common", "all", "feedback", "5", "exp", "treatment", 10.0, "shows", True),
            ("common", "all", "feedback", "5", "exp", "treatment", 5.0, "likes", True),
            ("common", "all", "feedback", "3", "exp", "control", 11.0, "shows", True),
            ("common", "all", "feedback", "3", "exp", "control", 7.0, "likes", True),
            ("common", "all", "feedback", "3", "exp", "control", 11.0, "shows", True),
            ("common", "all", "feedback", "3", "exp", "control", 7.0, "likes", True),
            ("common", "all", "feedback", "4", "exp", "control", 10.0, "shows", True),
            ("common", "all", "feedback", "4", "exp", "control", 8.0, "likes", True),
            ("common", "all", "feedback", "4", "exp", "control", 10.0, "shows", True),
            ("common", "all", "feedback", "4", "exp", "control", 8.0, "likes", True),
            ("common", "all", "feedback", "5", "exp", "control", 10.0, "shows", True),
            ("common", "all", "feedback", "5", "exp", "control", 8.0, "likes", True),
            ("common", "all", "feedback", "6", "exp", "control", 10.0, "shows", True),
            ("common", "all", "feedback", "6", "exp", "control", 5.0, "likes", True),
            ("common", "all", "feedback", "7", "exp", "control", 10.0, "shows", True),
            ("common", "all", "feedback", "7", "exp", "control", 5.0, "likes", True),
        ],
        [
            "categoryName",
            "categoryValue",
            "metricSource",
            "entityUid",
            "experimentUid",
            "variantId",
            "metricValue",
            "metricName",
            "isAdditive",
        ],
    )


@pytest.fixture(scope="session")
def spark(path: str):
    return (
        SparkSession.builder.appName("Python Spark for tests")
        .config("spark.jars", path)
        .getOrCreate()
    )


def test_cumulativeMetricTransformer(data_sample: DataFrame):
    cum = CumulativeMetricTransformer(
        metricSourceColumn="metricSource",
        entityIdColumn="entityUid",
        experimentColumn="experimentUid",
        variantColumn="variantId",
        valueColumn="metricValue",
        metricNameColumn="metricName",
        additiveColumn="isAdditive",
        numBuckets=-1,
    )

    cum_sample = cum.transform(data_sample)
    values = [
        row.asDict()
        for row in cum_sample.select("entityUid", "metricName", "metricValue").collect()
    ]

    expected = [
        {"entityUid": "2", "metricName": "likes", "metricValue": 10.0},
        {"entityUid": "1", "metricName": "likes", "metricValue": 6.0},
        {"entityUid": "4", "metricName": "likes", "metricValue": 16.0},
        {"entityUid": "4", "metricName": "shows", "metricValue": 20.0},
        {"entityUid": "1", "metricName": "shows", "metricValue": 22.0},
        {"entityUid": "3", "metricName": "shows", "metricValue": 22.0},
        {"entityUid": "3", "metricName": "likes", "metricValue": 14.0},
        {"entityUid": "2", "metricName": "shows", "metricValue": 20.0},
    ]

    for value in expected:
        assert value in values


def test_welchStatisticsTransformer(data_sample: DataFrame):
    cum = CumulativeMetricTransformer()
    welch = WelchStatisticsTransformer(minValidSampleSize=3)
    result = welch.transform(cum.transform(data_sample))

    p_values = [
        i["pValue"] for i in result.select("statisticsData.statResult.pValue").collect()
    ]
    assert all([p > 0.05 for p in p_values])


def test_mannWhitneyStatisticsTransformer(data_sample: DataFrame):
    cum = CumulativeMetricTransformer()
    welch = WelchStatisticsTransformer(minValidSampleSize=3)
    result = welch.transform(cum.transform(data_sample))

    p_values = [
        i["pValue"] for i in result.select("statisticsData.statResult.pValue").collect()
    ]
    assert all([p > 0.05 for p in p_values])


def test_autoStatisticsTransformer(data_sample: DataFrame):
    cum = CumulativeMetricTransformer()
    auto = AutoStatisticsTransformer(minValidSampleSize=3)
    result = auto.transform(cum.transform(data_sample))

    p_values = [
        i["pValue"] for i in result.select("statisticsData.statResult.pValue").collect()
    ]
    assert all([p > 0.05 for p in p_values])


def test_outlierRemoveTransformer(data_sample_for_outlier: DataFrame):
    outlier = OutlierRemoveTransformer(lowerPercentile=0.05, upperPercentile=0.95, excludedMetrics=["clicks"])
    result = outlier.transform(data_sample_for_outlier)

    result.show()

    countViews = result.filter("metricName = 'shows'").count()
    countClicks = result.filter("metricName = 'clicks'").count()
    assert countViews == 89
    assert countClicks == 100

