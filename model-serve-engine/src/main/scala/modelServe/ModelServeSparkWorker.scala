package modelServe

import java.awt.image.BufferedImage
import java.util.Collections

import com.databricks.sparkdl.DeepImageFeaturizer
import de.koware.flowersmodelserve.api.documentApi.DocumentContainer
import documentSparkTypes.DocumentContainerSchema
import javax.imageio.ImageIO
import org.apache.spark.ml.image.ImageSchema
import org.apache.spark.sql.functions.struct
import org.apache.spark.sql.{Dataset, Row}
import org.slf4j.LoggerFactory
import org.apache.spark.ml.linalg.Vector


/*

  class is responsible for low-level spark operations
 */
class ModelServeSparkWorker {

  private val LOGGER = LoggerFactory.getLogger(this.getClass)

  private val sparkSession = ModelServeSparkConfig.sparkSession

  private val sparkPdfDatasetService: SparkPdfDatasetService = new SparkPdfDatasetService

  def createImageDataset(documentContainer: DocumentContainer): Dataset[Row] = {
    val image: BufferedImage = ImageIO.read(documentContainer.getInputStream)

    val row = CopiedImageUtils.spImageFromBufferedImage(image, "api")

    val dataset = sparkSession.createDataFrame(Collections.singletonList(row), ImageSchema.columnSchema)

    LOGGER.info("showing raw image dataset")
    dataset.show()

    concatImageDsColumns(dataset)
  }

  def createDatasetByMimetype(documentContainer: DocumentContainer): Dataset[Row] = {

    if (documentContainer.getMimeContentType.contains("image")) {
      return this.createImageDataset(documentContainer)
    }

    if (documentContainer.getMimeContentType.contains("pdf")) {
      return this.createPdfDataset(documentContainer)
    }
    this.createJsonDataset(documentContainer)
  }


  def createJsonDataset(documentContainer: DocumentContainer): Dataset[Row] = {
    LOGGER.error("JSON Dataset creation is not yet implemented...")
    return null // TODO implement creation of datasets for JSON text
  }

  def createPdfDataset(documentContainer: DocumentContainer): Dataset[Row] = {
    LOGGER.info("creating dataset from pdf document container")

    val ds = sparkSession.createDataFrame(
      Collections.singletonList(sparkPdfDatasetService.createDocumentContaienrRow(documentContainer)),
      DocumentContainerSchema.columnSchema)

    return ds
  }

  def concatImageDsColumns(ds: Dataset[Row]): Dataset[Row] = {

    LOGGER.info("merging columsn into one and rm the original ones")

    // concat all columns into new input column 'image', then remove the original columns
    val imageds = ds.withColumn("image", struct(
      ds("origin"),
      ds("height"),
      ds("width"),
      ds("nChannels"),
      ds("mode"),
      ds("data")
    ))
      .drop("origin")
      .drop("height")
      .drop("width")
      .drop("nChannels")
      .drop("mode")
      .drop("data")
    LOGGER.info("showing transformed ds")
    imageds.show()

    imageds
  }

  def featurizeImageDs(ds: Dataset[Row]): Vector = {
    val featurizer = new DeepImageFeaturizer()
    featurizer.setModelName("InceptionV3")
    featurizer.setInputCol("image")
    featurizer.setOutputCol("features")

    val featurizedDs = featurizer.transform(ds)
    // this cast to Row[] is required, although IntelliJ says otherwise
    val rows = featurizedDs.collect.asInstanceOf[Array[Row]]
    val row = rows(0)
    row.getAs("features")

  }


}
