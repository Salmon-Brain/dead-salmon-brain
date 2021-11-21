from pyspark import since, keyword_only
from pyspark.ml.common import inherit_doc
from pyspark.ml.util import JavaMLReadable, JavaMLWritable
from pyspark.ml.wrapper import (
    JavaTransformer,
)

__all__ = ["CumulativeMetricTransformer"]

from params import BaseStatisticTransformerParameters


@inherit_doc
class CumulativeMetricTransformer(
    BaseStatisticTransformerParameters, JavaTransformer, JavaMLReadable, JavaMLWritable
):
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
        additiveColumn="isAdditive"
    ):
        super(CumulativeMetricTransformer, self).__init__()
        self._java_obj = self._new_java_obj(
            "import ai.salmon.computing.CumulativeMetricTransformer", self.uid
        )
        kwargs = self._input_kwargs
        self.setParams(**kwargs)

    @keyword_only
    @since("2.3.0")
    def setParams(
        self,
        *,
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
