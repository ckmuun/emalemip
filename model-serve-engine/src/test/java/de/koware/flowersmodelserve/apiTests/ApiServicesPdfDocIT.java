package de.koware.flowersmodelserve.apiTests;

import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.documentApi.DocumentContainer;
import de.koware.flowersmodelserve.api.documentApi.DocumentEntity;
import de.koware.flowersmodelserve.api.functionalClassification.RxModelServeService;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiServicesPdfDocIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServicesPdfDocIT.class);

    @Autowired
    private RxModelServeService rxModelServeService;

    @Test
    public void testDatasetCreationFromPdf() throws IOException {

        LOGGER.info("testing creation of dataset from PDF file");

        DocumentContainer pdfContainer = ModelServeTestUtils.createPdfDocumentContainer();

        DocumentEntity documentEntity = new DocumentEntity(
                pdfContainer,
                UUID.randomUUID()
        );

        Dataset<Row> pdfDs = this.rxModelServeService
                .createDatasetFromInputDocument(documentEntity)
                .block();
        pdfDs.show();
    }

}
