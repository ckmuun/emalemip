package documentSparkTypes

import org.apache.spark.sql.types.{BinaryType, StringType, StructField, StructType}

object DocumentContainerSchema {


  val columnSchema: StructType = StructType(
    StructField("filename", StringType, nullable = false) ::
    StructField("mimeContentType", StringType, nullable = false) ::
    StructField("data", BinaryType, nullable = false) :: Nil
  )


}
