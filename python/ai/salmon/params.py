from pyspark.ml.param import *


class BaseStatisticTransformerParameters(Params):
    metricSourceColumn = Param(
        Params._dummy(),
        "metricSourceColumn",
        "",
        typeConverter=TypeConverters.toString,
    )

    entityIdColumn = Param(
        Params._dummy(),
        "entityIdColumn",
        "",
        typeConverter=TypeConverters.toString,
    )
    experimentColumn = Param(
        Params._dummy(),
        "experimentColumn",
        "",
        typeConverter=TypeConverters.toString,
    )
    variantColumn = Param(
        Params._dummy(),
        "variantColumn",
        "",
        typeConverter=TypeConverters.toString,
    )
    valueColumn = Param(
        Params._dummy(),
        "valueColumn",
        "",
        typeConverter=TypeConverters.toString,
    )

    metricNameColumn = Param(
        Params._dummy(),
        "metricNameColumn",
        "",
        typeConverter=TypeConverters.toString,
    )

    additiveColumn = Param(
        Params._dummy(),
        "additiveColumn",
        "",
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

    def getMetricSourceColumn(self):
        return self.getOrDefault(self.metricSourceColumn)

    def getEntityIdColumn(self):
        return self.getOrDefault(self.entityIdColumn)

    def getExperimentColumn(self):
        return self.getOrDefault(self.experimentColumn)

    def getVariantColumn(self):
        return self.getOrDefault(self.variantColumn)

    def getValueColumn(self):
        return self.getOrDefault(self.valueColumn)

    def getAdditiveColumn(self):
        return self.getOrDefault(self.additiveColumn)

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
