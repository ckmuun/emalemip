package documentSparkTypes

case class PdDocumentSchemaImpl(
                                 filename: String,
                                 documentBytes: Array[Byte],
                                 mimetype: String = "application/pdf"
                               )
