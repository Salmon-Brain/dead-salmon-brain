package org.apache.spark.sql.types

import ai.salmon.computing.Variants

class VariantUDT extends UserDefinedType[Variants] {
  override def sqlType: DataType = IntegerType

  override def serialize(obj: Variants.Value): Int = obj.id

  override def deserialize(datum: Any): Variants.Value = Variants(datum.asInstanceOf[Int])

  override def userClass: Class[Variants.Value] = classOf[Variants.Value]
}

object EnumUDTRegister {

  def register() {
    UDTRegistration.register(
      classOf[Variants.Variant].getCanonicalName,
      classOf[VariantUDT].getCanonicalName
    )
  }

}
