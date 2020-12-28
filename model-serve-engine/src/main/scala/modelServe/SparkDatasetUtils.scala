package modelServe

import org.apache.spark.sql.{Dataset, Row, SparkSession}

class SparkDatasetUtils(spark: SparkSession) {


  def getDatasetFromJson(jsonStr: String): Dataset[Row] = {
    import spark.implicits._ // spark is your SparkSession object

    val df = spark.read.json(Seq(jsonStr).toDS)

    df
  }
}
