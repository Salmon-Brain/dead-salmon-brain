# Dead salmon brain

Apache Spark based framework for analysis A/B experiments

## Why
Because we didn't find good and scalable
realization for industrial A/B analysis. 

## Getting Started
## Installation

```bash
./gradlew build
```


###Data contract

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

### Usage for 


```scala
new Pipeline().setStages(
  Array(
    new CumulativeMetricTransformer(), // aggregate all metrics
    new OutlierRemoveTransformer(), // remove outliers by percentile
    new AutoStatisticsTransformer() // auto choose and compute Welch or MannWhitney test 
  )
)
```

### Usage as end2end stat engine from existing source


## Contributing
Pull requests are welcome.

## License
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
