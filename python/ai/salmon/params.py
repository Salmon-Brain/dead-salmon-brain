from pyspark.ml.param import *


class BaseStatisticTransformerParameters(Params):
    metricSourceColumn = Param(
        Params._dummy(),
        "metricSourceColumn",
        "column with experiment metric data source",
        typeConverter=TypeConverters.toString,
    )

    entityIdColumn = Param(
        Params._dummy(),
        "entityIdColumn",
        "column with entity id",
        typeConverter=TypeConverters.toString,
    )
    experimentColumn = Param(
        Params._dummy(),
        "experimentColumn",
        "column with experiment id",
        typeConverter=TypeConverters.toString,
    )
    variantColumn = Param(
        Params._dummy(),
        "variantColumn",
        "column to split control and treatment",
        typeConverter=TypeConverters.toString,
    )
    valueColumn = Param(
        Params._dummy(),
        "valueColumn",
        "column with metric value",
        typeConverter=TypeConverters.toString,
    )

    metricNameColumn = Param(
        Params._dummy(),
        "metricNameColumn",
        "column with metric name",
        typeConverter=TypeConverters.toString,
    )

    additiveColumn = Param(
        Params._dummy(),
        "additiveColumn",
        "column with metric type additive or non_additive",
        typeConverter=TypeConverters.toString,
    )

    controlName = Param(
        Params._dummy(),
        "controlName",
        "label for control groups",
        typeConverter=TypeConverters.toString,
    )

    treatmentName = Param(
        Params._dummy(),
        "treatmentName",
        "label for treatment groups",
        typeConverter=TypeConverters.toString,
    )

    def __init__(self):
        super(BaseStatisticTransformerParameters, self).__init__()
        self._setDefault(metricSourceColumn="metricSource")
        self._setDefault(entityIdColumn="entityUid")
        self._setDefault(experimentColumn="expUid")
        self._setDefault(variantColumn="variantId")
        self._setDefault(valueColumn="metricValue")
        self._setDefault(additiveColumn="isAdditive")
        self._setDefault(controlName="control")
        self._setDefault(treatmentName="treatment")

    def setMetricSourceColumn(self, value):
        return self._set(metricSourceColumn=value)

    def setEntityIdColumn(self, value):
        return self._set(entityIdColumn=value)

    def setExperimentColumn(self, value):
        return self._set(experimentColumn=value)

    def setVariantColumn(self, value):
        return self._set(variantColumn=value)

    def setValueColumn(self, value):
        return self._set(valueColumn=value)

    def setAdditiveColumn(self, value):
        return self._set(additiveColumn=value)

    def setControlName(self, value):
        return self._set(controlName=value)

    def setTreatmentName(self, value):
        return self._set(treatmentName=value)


class BasicStatInferenceParameters(Params):
    alpha = Param(
        Params._dummy(),
        "alpha",
        "parameter for check Type 1 error",
        typeConverter=TypeConverters.toFloat,
    )

    beta = Param(
        Params._dummy(),
        "beta",
        "parameter for check Type 2 error",
        typeConverter=TypeConverters.toFloat,
    )

    srmAlpha = Param(
        Params._dummy(),
        "srmAlpha",
        "parameter for check FPR for SRM",
        typeConverter=TypeConverters.toFloat,
    )

    def __init__(self):
        super(BasicStatInferenceParameters, self).__init__()
        self._setDefault(alpha=0.05)
        self._setDefault(beta=0.02)
        self._setDefault(srmAlpha=0.05)

    def setAlpha(self, value):
        return self._set(alpha=value)

    def setBeta(self, value):
        return self._set(beta=value)

    def setSrmAlpha(self, value):
        return self._set(srmAlpha=value)
