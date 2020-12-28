package nlp

import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.util.DefaultParamsWritable
import org.apache.spark.sql.types.{DataType, DataTypes}

class LanguageDetector(override val uid: String)
  extends UnaryTransformer[String, String, LanguageDetector] with DefaultParamsWritable {


  override protected def createTransformFunc: String => String = (text: String) => {

    // todo implement language detection here.

    println("not yet implemented -> returning default language code for english")
    "eng"
  }

  override protected def outputDataType: DataType = DataTypes.StringType

}
