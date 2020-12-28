package modelServe

import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.UUID

import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream
import documentSparkTypes.ImageSchemaImpl
import javax.imageio.ImageIO
import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.image.ImageSchema
import org.apache.spark.ml.util.{DefaultParamsWritable, Identifiable}
import org.apache.spark.sql.types.DataType
import org.opencv.core.CvType

// if unary transformer is used without params in [], the error 'does not conform IN => OUT' arises
class Bin2ImageSchemaTransformer(override val uid: String)
  extends UnaryTransformer[Array[Byte], ImageSchemaImpl, Bin2ImageSchemaTransformer] with DefaultParamsWritable {

  def this() = this(Identifiable.randomUID("bin2ImageSchema"))

  override protected def createTransformFunc: Array[Byte] => ImageSchemaImpl = (imageBytes: Array[Byte]) => {
    println("converting")

    val imageInputStream = new ByteArrayImageInputStream(imageBytes)

    val buffy: BufferedImage = ImageIO.read(imageInputStream)

    CopiedImageUtils.spImageSchemaImplFromBufferedImage(buffy, "api")

  }

  override protected def outputDataType: DataType = ImageSchema.columnSchema
}
