# Dead salmon brain

Apache Spark based framework for analysis A/B experiments

## Why
Because good and scalable
realization for industrial A/B analysis doesn't exist in open source

## Getting Started
### Installation

```bash
./gradlew build
```


### Data model

```scala
case class ExpData(
    timestamp: Long, //metric timestamp
    variantId: String, //(treatment and control)
    entityUid: String, //unique entity id
    expUid: String, //unique experiment id
    metricValue: Double, //numeric metric value
    metricName: String, //unique metric name in metricSource space  
    metricSource: String, //metric source 
    isAdditive: Boolean   // is additive metric or not
)
```

### Usage 


```scala
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.spark.ml.Pipeline
import ai.salmon.computing.{ExpData, CumulativeMetricTransformer, OutlierRemoveTransformer, AutoStatisticsTransformer}


val control = new NormalDistribution(1, 1)
  .sample(1000)
  .zipWithIndex.map { case (value, idx) =>
  ExpData(System.currentTimeMillis(), "control", idx.toString, "exp", value, "timeSpent")
}

val treatment = new NormalDistribution(2, 4)
  .sample(1000)
  .zipWithIndex.map { case (value, idx) =>
  ExpData(System.currentTimeMillis(), "treatment", idx.toString, "exp", value, "timeSpent")
}


val model = new Pipeline().setStages(
  Array(
    new CumulativeMetricTransformer(), // aggregate all metrics
    new OutlierRemoveTransformer(), // remove outliers by percentile
    new AutoStatisticsTransformer() // auto choose and compute Welch or MannWhitney test 
  )
)

val report = model.fit(data).transform(data)

report.select(
  "metricName",
  "expUid",
  "statisticsData.srm",
  "statisticsData.testType",
  "statisticsData.statResult.pValue",
  "statisticsData.statResult.requiredSampleSizeByVariant",
  "statisticsData.statResult.percentageLeft",
  "statisticsData.statResult.percentageRight"
).show
+----------+------+-----+--------+-------+---------------------------+-----------------+-----------------+
|metricName|expUid|  srm|testType|pValue |requiredSampleSizeByVariant|   percentageLeft|  percentageRight|
+----------+------+-----+--------+-------+---------------------------+-----------------+-----------------+
| timeSpent|   exp|false|   WELCH|1.3e-21|                         82|          94.6294|         148.8822|
+----------+------+-----+--------+-------+---------------------------+-----------------+-----------------+
```

### What they are and how to interpret them?
[srm](https://towardsdatascience.com/the-essential-guide-to-sample-ratio-mismatch-for-your-a-b-tests-96a4db81d7a4)
 if true than your test is invalid 

[pValue](https://en.wikipedia.org/wiki/P-value) 
 if less than 0.05 or 0.01 then great

[requiredSampleSizeByVariant](https://en.wikipedia.org/wiki/Sample_size_determination)
 estimated required sample size by each variant for observed test and data parameters

[percentageLeft](https://en.wikipedia.org/wiki/Confidence_interval) 
 lower percent confidence interval

[percentageRight](https://en.wikipedia.org/wiki/Confidence_interval) 
 upper percent confidence interval


## Contributing
Pull requests are welcome.

## License
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
