package de.koware.flowersmodelserve.unit;

import customTransformers.TessOcrTf;
import de.koware.flowersmodelserve.api.documentApi.DocumentContainer;
import documentSparkTypes.DocumentContainerSchema;
import modelServe.ModelServeSparkConfig;
import modelServe.SparkPdfDatasetService;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OcrTextractorTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(OcrTextractorTest.class);

    private final SparkSession sparkSession = ModelServeSparkConfig.sparkSession();

    private final SparkPdfDatasetService pdfDatasetService = new SparkPdfDatasetService();

    @Test
    public void testOcrTextractor() throws IOException {

        // FIXME check out why relative path ain't working here and rm the absolute one.
        FileInputStream fis = new FileInputStream(new File("src/test/resources/jobBulletinPdfs/1605436304178-layout.pdf"));

        DocumentContainer pdfContainer = new DocumentContainer(
                StreamUtils.copyToByteArray(fis),
                "testfile-with-text",
                "application/pdf"
        );

        List<Row> list = new ArrayList<>(1);
        list.add(pdfDatasetService.createDocumentContaienrRow(pdfContainer));

        Dataset<Row> ds = sparkSession.createDataFrame(list, DocumentContainerSchema.columnSchema());

        assert null != ds;
        ds.show();

        TessOcrTf tessOcrTf = new TessOcrTf("tesseract-ocr");
        tessOcrTf.setInputCol("data");
        tessOcrTf.setOutputCol("ocr-text");

        Dataset<Row> ocrDs = tessOcrTf.transform(ds);
        ocrDs.show();

    }
}
