package customTransformers

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.util.DefaultParamsWritable
import org.apache.spark.sql.types.{DataType, DataTypes}

class PdfBoxTextractorTf(override val uid: String)
  extends UnaryTransformer[Array[Byte], String, PdfBoxTextractorTf] with DefaultParamsWritable {


  override protected def createTransformFunc: Array[Byte] => String = (documentBytes: Array[Byte]) => {

    val textStripper: PDFTextStripper = new PDFTextStripper()
    val text: String = textStripper.getText(PDDocument.load(documentBytes))

    text
  }

  override protected def outputDataType: DataType = DataTypes.StringType
}
