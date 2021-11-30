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
        "change entity uid to synth buckets",
        typeConverter=TypeConverters.toInt,
    )

    numeratorNames = Param(
        Params._dummy(),
        "numeratorNames",
        "numerator metrics names",
        typeConverter=TypeConverters.toListString,
    )

    denominatorNames = Param(
        Params._dummy(),
        "denominatorNames",
        "denominator metric names",
        typeConverter=TypeConverters.toListString,
    )

    ratioNames = Param(
        Params._dummy(),
        "ratioNames",
        "new ratio metric names",
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
        numeratorNames=[],
        denominatorNames=[],
        ratioNames=[]
    ):
        super(CumulativeMetricTransformer, self).__init__()
        self._java_obj = self._new_java_obj(
            "ai.salmon.computing.CumulativeMetricTransformer", self.uid
        )
        self._setDefault(numBuckets=-1)
        self._setDefault(numeratorNames=[])
        self._setDefault(denominatorNames=[])
        self._setDefault(ratioNames=[])
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
        numeratorNames=[],
        denominatorNames=[],
        ratioNames=[]
    ):
        kwargs = self._input_kwargs
        return self._set(**kwargs)

    def setNumeratorNames(self, value):
        return self._set(numeratorNames=value)

    def setDenominatorNames(self, value):
        return self._set(denominatorNames=value)

    def setRatioNames(self, value):
        return self._set(ratioNames=value)


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
        additiveColumn="isAdditive",
        controlName="control",
        treatmentName="treatment"
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
        additiveColumn="isAdditive",
        controlName="control",
        treatmentName="treatment"
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
        additiveColumn="isAdditive",
        controlName="control",
        treatmentName="treatment"
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
        additiveColumn="isAdditive",
        controlName="control",
        treatmentName="treatment"
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
        "lower percentile to clear outliers",
        typeConverter=TypeConverters.toFloat,
    )

    upperPercentile = Param(
        Params._dummy(),
        "upperPercentile",
        "upper percentile to clear outliers",
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
