package documentSparkTypes

case class DocumentContainerSchemaImpl(
                                        filename: String,
                                        documentBytes: Array[Byte],
                                        mimetype: String = "application/pdf"
                                      )
