from pyspark import keyword_only
from pyspark.ml.common import inherit_doc
from pyspark.ml.param import Param, Params, TypeConverters
from pyspark.ml.util import JavaMLReadable, JavaMLWritable
from pyspark.ml.wrapper import (
    JavaTransformer,
)

from .params import BaseStatisticTransformerParameters, BasicStatInferenceParameters


@inherit_doc
class CumulativeMetricTransformer(
    BaseStatisticTransformerParameters, JavaTransformer, JavaMLReadable, JavaMLWritable
):
    numBuckets = Param(
        Params._dummy(),
        "numBuckets",
        "",
        typeConverter=TypeConverters.toInt,
    )

    numeratorMetrics = Param(
        Params._dummy(),
        "numeratorMetrics",
        "",
        typeConverter=TypeConverters.toListString,
    )

    denominatorMetrics = Param(
        Params._dummy(),
        "denominatorMetrics",
        "",
        typeConverter=TypeConverters.toListString,
    )

    ratioMetrics = Param(
        Params._dummy(),
        "ratioMetrics",
        "",
        typeConverter=TypeConverters.toListString,
    )

    @keyword_only
    def __init__(
        self,
        *,
        metricSourceColumn="metricSource",
        entityIdColumn="entityUid",
        experimentColumn="expUid",
        variantColumn="variantId",
        valueColumn="metricValue",
        metricNameColumn="metricName",
        additiveColumn="isAdditive",
        numBuckets=-1,
        numeratorMetrics=[],
        denominatorMetrics=[],
        ratioMetric=[]
    ):
        super(CumulativeMetricTransformer, self).__init__()
        self._java_obj = self._new_java_obj(
            "ai.salmon.computing.CumulativeMetricTransformer", self.uid
        )
        self._setDefault(numBuckets=-1)
        self._setDefault(numeratorMetrics=[])
        self._setDefault(denominatorMetrics=[])
        self._setDefault(ratioMetrics=[])
        kwargs = self._input_kwargs
        self.setParams(**kwargs)

    @keyword_only
    def setParams(
        self,
        *,
        metricSourceColumn="metricSource",
        entityIdColumn="entityUid",
        experimentColumn="expUid",
        variantColumn="variantId",
        valueColumn="metricValue",
        metricNameColumn="metricName",
        additiveColumn="isAdditive",
        numBuckets=-1,
        numeratorMetrics=[],
        denominatorMetrics=[],
        ratioMetric=[]
    ):
        kwargs = self._input_kwargs
        return self._set(**kwargs)

    def setNumeratorMetrics(self, value):
        return self._set(numeratorMetrics=value)

    def setDenominatorMetrics(self, value):
        return self._set(denominatorMetrics=value)

    def setRatioMetrics(self, value):
        return self._set(ratioMetrics=value)


class StatisticTransformer:
    @keyword_only
    def setParams(
        self,
        *,
        alpha=0.05,
        beta=0.2,
        srmAlpha=0.05,
        metricSourceColumn="metricSource",
        entityIdColumn="entityUid",
        experimentColumn="expUid",
        variantColumn="variantId",
        valueColumn="metricValue",
        metricNameColumn="metricName",
        additiveColumn="isAdditive"
    ):
        kwargs = self._input_kwargs
        return self._set(**kwargs)


@inherit_doc
class WelchStatisticsTransformer(
    BasicStatInferenceParameters,
    BaseStatisticTransformerParameters,
    StatisticTransformer,
    JavaTransformer,
    JavaMLReadable,
    JavaMLWritable,
):
    @keyword_only
    def __init__(
        self,
        *,
        alpha=0.05,
        beta=0.2,
        srmAlpha=0.05,
        metricSourceColumn="metricSource",
        entityIdColumn="entityUid",
        experimentColumn="expUid",
        variantColumn="variantId",
        valueColumn="metricValue",
        metricNameColumn="metricName",
        additiveColumn="isAdditive"
    ):
        super(WelchStatisticsTransformer, self).__init__()
        self._java_obj = self._new_java_obj(
            "ai.salmon.computing.WelchStatisticsTransformer", self.uid
        )
        kwargs = self._input_kwargs
        self.setParams(**kwargs)


@inherit_doc
class MannWhitneyStatisticsTransformer(
    BasicStatInferenceParameters,
    BaseStatisticTransformerParameters,
    StatisticTransformer,
    JavaTransformer,
    JavaMLReadable,
    JavaMLWritable,
):
    @keyword_only
    def __init__(
        self,
        *,
        alpha=0.05,
        beta=0.2,
        srmAlpha=0.05,
        metricSourceColumn="metricSource",
        entityIdColumn="entityUid",
        experimentColumn="expUid",
        variantColumn="variantId",
        valueColumn="metricValue",
        metricNameColumn="metricName",
        additiveColumn="isAdditive"
    ):
        super(MannWhitneyStatisticsTransformer, self).__init__()
        self._java_obj = self._new_java_obj(
            "ai.salmon.computing.MannWhitneyStatisticsTransformer", self.uid
        )
        kwargs = self._input_kwargs
        self.setParams(**kwargs)


@inherit_doc
class AutoStatisticsTransformer(
    BasicStatInferenceParameters,
    BaseStatisticTransformerParameters,
    StatisticTransformer,
    JavaTransformer,
    JavaMLReadable,
    JavaMLWritable,
):
    @keyword_only
    def __init__(
        self,
        *,
        alpha=0.05,
        beta=0.2,
        srmAlpha=0.05,
        metricSourceColumn="metricSource",
        entityIdColumn="entityUid",
        experimentColumn="expUid",
        variantColumn="variantId",
        valueColumn="metricValue",
        metricNameColumn="metricName",
        additiveColumn="isAdditive"
    ):
        super(AutoStatisticsTransformer, self).__init__()
        self._java_obj = self._new_java_obj(
            "ai.salmon.computing.AutoStatisticsTransformer", self.uid
        )
        kwargs = self._input_kwargs
        self.setParams(**kwargs)


@inherit_doc
class OutlierRemoveTransformer(
    BaseStatisticTransformerParameters, JavaTransformer, JavaMLReadable, JavaMLWritable
):
    lowerPercentile = Param(
        Params._dummy(),
        "lowerPercentile",
        "",
        typeConverter=TypeConverters.toFloat,
    )

    upperPercentile = Param(
        Params._dummy(),
        "upperPercentile",
        "",
        typeConverter=TypeConverters.toFloat,
    )

    @keyword_only
    def __init__(
        self,
        *,
        metricSourceColumn="metricSource",
        entityIdColumn="entityUid",
        experimentColumn="expUid",
        variantColumn="variantId",
        valueColumn="metricValue",
        metricNameColumn="metricName",
        additiveColumn="isAdditive",
        lowerPercentile=0.01,
        upperPercentile=0.99
    ):
        super(OutlierRemoveTransformer, self).__init__()
        self._java_obj = self._new_java_obj(
            "ai.salmon.computing.OutlierRemoveTransformer", self.uid
        )
        self._setDefault(lowerPercentile=0.01)
        self._setDefault(upperPercentile=0.99)
        kwargs = self._input_kwargs
        self.setParams(**kwargs)

    @keyword_only
    def setParams(
        self,
        *,
        metricSourceColumn="metricSource",
        entityIdColumn="entityUid",
        experimentColumn="expUid",
        variantColumn="variantId",
        valueColumn="metricValue",
        metricNameColumn="metricName",
        additiveColumn="isAdditive",
        lowerPercentile=0.01,
        upperPercentile=0.99
    ):
        kwargs = self._input_kwargs
        return self._set(**kwargs)

    def setLowerPercentile(self, value):
        return self._set(lowerPercentile=value)

    def setUpperPercentile(self, value):
        return self._set(upperPercentile=value)
