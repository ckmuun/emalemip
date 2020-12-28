package documentSparkTypes

// the case class is very important for Encoding to and from Spark SQL Catalyst representation
case class ImageSchemaImpl(originInput: String,
                           heightInput: Int,
                           widthInput: Int,
                           modeInput: Int,
                           nChannelsInput: Int,
                           dataInput: Array[Byte]) {

  private val origin: String = originInput
  private val height: Int = heightInput
  private val width: Int = widthInput
  private val mode: Int = modeInput
  private val nChannels: Int = nChannelsInput
  private val data: Array[Byte] = dataInput


}
