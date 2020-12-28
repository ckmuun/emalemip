package modelServe

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SparkSession
import org.springframework.context.annotation.{Bean, Configuration}

object ModelServeSparkConfig {

  private val masterUri = "local"

  private val memory = "1200000000000"

  def sparkConf: SparkConf = {
    System.setProperty("hadoop.home.dir", "\\") // some dir to suppress warning
    System.setProperty("spark.driver.memory", "8g")


    val sparkConf = new SparkConf()
      .setAppName("flowers-model-serve")
      .setMaster(masterUri)
      .set("spark.driver.memory", memory)
      .set("spark.testing.memory", memory)
      .set("spark.eventLog.enabled", "false")
      //.set("spark.eventLog.dir", "src/main/resources/sparkEvents")
      .set("spark.rdd.compress", "false")
      .set("spark.dynamicAllocation.enabled", "true")
      .set("spark.executor.memory", "16g") // this needs to be specified as a string
      .set("spark.executor.cores", "8") // this needs to be specified as a string
      .set("spark.executor.memoryOverhead", "8g") // this needs to be specified as a string
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .setSparkHome("/home/cornelius/.sdkman/candidates/spark/current")
    //   .set("spark.kryo.registrationRequired", "true")
    //     .set("--driver-memory", memory);
    sparkConf
  }

  def javaSparkContext: JavaSparkContext = {
    // shitty workaround because of the openjdk specifying java version as, for example "10", while sparks wants three digits
    val javav = System.getProperty("java.version")
    if (javav.length == 2) System.setProperty("java.version", "1." + javav)

    val sparkContext = new JavaSparkContext(sparkConf)
    sparkContext.setCheckpointDir("flowers-model-serve/src/main/resources/checkpoints")

    sparkContext
  }

  def scalaSparkContext: SparkContext = {
    val scalaSc = new SparkContext(sparkConf)
    scalaSc
  }

  @Bean
  def sparkSession: SparkSession = {
    SparkSession.builder().config(sparkConf).getOrCreate()
  }


}
