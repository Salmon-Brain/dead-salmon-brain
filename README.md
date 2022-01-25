# Dead salmon brain

The dead salmon brain is an open source project for industrial A/B statistical analysis and reporting. The core library extends Apache Spark functionality, making it easy to integrate into your data processing pipelines and providing scalable implementations
of common online experimentation techniques and mathematical methods.

## API
Java/Scala/Python

## Getting Started
### Build from source

```bash
1) Clone git repository
2) Check spark,scala,hadoop and set correct in 3 step
3) bash setup.sh spark=3.1.2 scala=2.12.11 hadoop=3.2 is_build=true is_install_python_lib=false
```

### Setup from released packages
```bash
1) Clone git repository
2) Check version,spark,scala,hadoop and set correct in 3 step
3) bash setup.sh version=0.0.4 spark=3.1.2 scala=2.12.11 hadoop=3.2 is_build=false is_install_python_lib=false
```

### Add as library
[Maven Central](https://mvnrepository.com/artifact/ai.salmonbrain/)

[PIP](https://pypi.org/project/dead-salmon-brain/)


### Data model

```scala
case class ExpData(
    timestamp: Long, //metric timestamp
    variantId: String, //(treatment and control)
    entityUid: String, //unique entity id
    experimentUid: String, //unique experiment id
    metricValue: Double, //numeric metric value
    metricName: String, //unique metric name in metricSource space
    categoryName: String, //entity category name (i.e gender)
    categoryValue: String, // entity category value (i.e male, female, other)
    metricSource: String, //metric source 
    isAdditive: Boolean   // is additive metric or not
)
```

### Usage 


```scala
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.spark.ml.Pipeline
import ai.salmonbrain.computing.{ExpData, CumulativeMetricTransformer, OutlierRemoveTransformer, AutoStatisticsTransformer}


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
    new OutlierRemoveTransformer() // remove outliers by percentile
           .setLowerPercentile(0.01)
           .setUpperPercentile(0.99),
    new AutoStatisticsTransformer() // auto choose and compute Welch or MannWhitney test
            .setAlpha(0.05)
            .setBeta(0.2)
  )
)

val report = model.fit(data).transform(data)

report.select(
  "metricName",
  "experimentUid",
  "statisticsData.srm",
  "statisticsData.testType",
  "statisticsData.statResult.pValue",
  "statisticsData.statResult.requiredSampleSizeByVariant",
  "statisticsData.statResult.percentageLeft",
  "statisticsData.statResult.percentageRight"
).show
+----------+-------------+-----+--------+-------+---------------------------+-----------------+-----------------+
|metricName|experimentUid|  srm|testType|pValue |requiredSampleSizeByVariant|   percentageLeft|  percentageRight|
+----------+-------------+-----+--------+-------+---------------------------+-----------------+-----------------+
| timeSpent|          exp|false|   WELCH|1.3e-21|                         82|          94.6294|         148.8822|
+----------+-------------+-----+--------+-------+---------------------------+-----------------+-----------------+
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

### Extra features
```scala
import ai.salmon.computing.RatioMetricData

val cum = new CumulativeMetricTransformer()
          .setNumBuckets(256) // you can split your data by buckets and use buckets like new entity
          .setNumeratorNames(Array("clicks"))  // you can set numerator
          .setDenominatorNames(Array("views")) // and denominator
          .setRatioNames(Array("ctr"))  // and create new ratio metric
```

## Contributing
Pull requests are welcome.

## License
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
