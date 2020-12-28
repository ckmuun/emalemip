package customTransformers

import net.sourceforge.tess4j.Tesseract
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.util.DefaultParamsWritable
import org.apache.spark.sql.types.{DataType, DataTypes}

/*
  A simple tesseract-based ocr Spark Transformer
  More advanced OCR functionality (Page Segments etc. may be added later)
 */

class TessOcrTf(override val uid: String)
  extends UnaryTransformer[Array[Byte], String, TessOcrTf] with DefaultParamsWritable {

  override protected def createTransformFunc: Array[Byte] => String = (data: Array[Byte]) => {


    val tessy: Tesseract = new Tesseract()
    tessy.setDatapath("src/main/resources/tessdata")

    val pdd: PDDocument = PDDocument.load(data)
    val renderer: PDFRenderer = new PDFRenderer(pdd)

    val sb: StringBuilder = new StringBuilder()

    val lastPageIndex = pdd.getNumberOfPages - 1

    for (index <- 0 to lastPageIndex) {
      val pageImage = renderer.renderImage(index)

      val pageText = tessy.doOCR(pageImage)
      sb.append(pageText)
    }

    sb.toString()
  }


  override protected def outputDataType: DataType = DataTypes.StringType
}
