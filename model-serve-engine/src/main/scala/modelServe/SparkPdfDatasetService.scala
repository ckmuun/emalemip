package modelServe

import de.koware.flowersmodelserve.api.documentApi.{DocumentContainer, IncomingDocumentDto}
import documentSparkTypes.PdDocumentSchemaImpl
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.spark.sql.Row
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.stereotype.Service

/*
    This class creates an Apache Spark SQL Dataset Row out of PDF Documents.
*/

class SparkPdfDatasetService {

  private val LOGGER: Logger = LoggerFactory.getLogger(this.getClass)


  def creatPdfDocumentSchemaImpl(documentContainer: DocumentContainer): PdDocumentSchemaImpl = {

    LOGGER.info("creating Spark Dataset Row out of PDF Document Container")

    PdDocumentSchemaImpl(documentContainer.getFilename, documentContainer.getBytes)
  }

  def createDocumentContaienrRow(documentContainer: DocumentContainer): Row = {
    Row(documentContainer.getFilename,  documentContainer.getMimeContentType, documentContainer.getBytes)
  }
}
