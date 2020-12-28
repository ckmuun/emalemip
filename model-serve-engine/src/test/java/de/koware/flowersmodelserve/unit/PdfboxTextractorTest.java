package de.koware.flowersmodelserve.unit;

import customTransformers.PdfBoxTextractorTf;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfboxTextractorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfboxTextractorTest.class);

    private final SparkSession sparkSession = ModelServeSparkConfig.sparkSession();

    private final SparkPdfDatasetService pdfDatasetService = new SparkPdfDatasetService();

    @Test
    public void testPdfDatasetCreation() throws IOException {
        LOGGER.info("testing the creation of PDF datasets");

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

        PdfBoxTextractorTf textractorTf = new PdfBoxTextractorTf("textractor");

        textractorTf.setInputCol("data");
        textractorTf.setOutputCol("pdfbox-text");

        Dataset<Row> textDs = textractorTf.transform(ds);

        textDs.show();
    }



}
