package customTestTransformer

import modelServe.Bin2ImageSchemaTransformer
import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.util.DefaultParamsWritable
import org.apache.spark.sql.types.{DataType, DataTypes}

/*
    This transformer just adds an output column with content "bob"
    It exists only for testing purpose as the simplest possible custom Unary Transformer.
 */

class BobTestTf(override val uid: String)
  extends UnaryTransformer[String, String, BobTestTf] with DefaultParamsWritable {

  override protected def createTransformFunc: String => String = (text: String) => {

    "bob"
  }

  override protected def outputDataType: DataType = DataTypes.StringType
}
