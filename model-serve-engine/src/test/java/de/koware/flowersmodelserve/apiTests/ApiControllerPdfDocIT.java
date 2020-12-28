package de.koware.flowersmodelserve.apiTests;


import customTestTransformer.BobTestTf;
import customTransformers.PdfBoxTextractorTf;
import customTransformers.TessOcrTf;
import de.koware.flowersmodelserve.api.SparkDatasetReturnDto;
import de.koware.flowersmodelserve.api.documentApi.DocumentContainer;
import de.koware.flowersmodelserve.api.documentApi.IncomingDocumentDto;
import de.koware.flowersmodelserve.api.functionalClassification.ClassificationResultReturnDto;
import de.koware.flowersmodelserve.api.functionalClassification.SparkModelSerializer;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalIncomingDto;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalReturnDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelContainer;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelIncomingDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelReturnDto;
import modelServe.ModelServeSparkConfig;
import modelServe.SparkDatasetUtils;
import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StreamUtils;
import scala.collection.Seq;

import java.io.*;
import java.time.Duration;
import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebFlux
@AutoConfigureWebTestClient(timeout = "36000")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class ApiControllerPdfDocIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiControllerPdfDocIT.class);
    private static final String baseurl = "http://localhost:";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SparkModelSerializer modelSerdes;

    @LocalServerPort
    private String port;


    private SparkSession sparkSession = ModelServeSparkConfig.sparkSession();

    // temp fields / vars
    private UUID pGoalId;
    private UUID tokenizerId;
    private UUID bobTfId;
    private UUID pdfboxTextractorId;
    private UUID ocrTextractorId;
    private UUID lrModelId;
    private UUID docLabelId;
    private UUID correlationKey;


    @BeforeAll
    public void setup() {
        LOGGER.info("setting up web test client");
        this.webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofMillis(36000))
                .build();
    }


    @Test
    @Order(1)
    public void testStatus() {
        LOGGER.info("testing status of greeting endpoint for status 200 ");
        this.webTestClient
                .get()
                .uri(baseurl + port + "/model-serve/greeting")
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .consumeWith(resp -> Objects.equals(resp.getResponseBody(), "hello"));
    }


    @Test
    @Order(3)

    public void testPostTokenizer() throws IOException {
        LOGGER.info("posting tokenizer to system");

        Tokenizer tokenizer = new Tokenizer();
        tokenizer.setInputCol("text");
        tokenizer.setOutputCol("token");

        final SparkModelContainer tokenizerContainer = new SparkModelContainer(
                this.modelSerdes.serializeTransformer(tokenizer),
                tokenizer.getClass().toString(),
                "text-tokenizer",
                "text",
                "tokens",
                new HashMap<>(),
                false
        );
        final SparkModelIncomingDto tokenizerIncDto = new SparkModelIncomingDto(
                tokenizerContainer
        );


        this.webTestClient
                .post()
                .uri(baseurl + port + "/classification-model")
                .bodyValue(tokenizerIncDto)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(SparkModelReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    SparkModelReturnDto sparkModelReturnDto = resp.getResponseBody();
                    assert null != sparkModelReturnDto;
                    assert Arrays.equals(sparkModelReturnDto.getBytes(), tokenizerIncDto.getSparkModelContainer().getBytes());
                    this.tokenizerId = sparkModelReturnDto.getId();
                });

    }

    @Test
    @Order(5)
    public void testPostCustomTransformer() throws IOException {
        final BobTestTf bobTestTf = new BobTestTf("bob-test-tf");

        HashMap<String, String> params = new HashMap<>();

        params.put("inputCol", "filename");
        params.put("outputCol", "bob");


        final SparkModelContainer bobTfContainer = new SparkModelContainer(
                this.modelSerdes.serializeTransformer(bobTestTf),
                bobTestTf.getClass().toString(),
                "the-bob",
                "filename",
                "bob",
                params,
                false
        );

        final SparkModelIncomingDto bobTfIncDto = new SparkModelIncomingDto(
                bobTfContainer
        );

        this.webTestClient
                .post()
                .uri(baseurl + port + "/classification-model")
                .bodyValue(bobTfIncDto)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(SparkModelReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    SparkModelReturnDto sparkModelReturnDto = resp.getResponseBody();
                    assert null != sparkModelReturnDto;
                    assert Arrays.equals(sparkModelReturnDto.getBytes(), bobTfIncDto.getSparkModelContainer().getBytes());
                    this.bobTfId = sparkModelReturnDto.getId();
                });
    }


    @Test
    @Order(10)
    public void testPostPdfTextExtractor() throws IOException {

        PdfBoxTextractorTf textractorTf = new PdfBoxTextractorTf("pdfbox-textractor");
        HashMap<String, String> params = new HashMap<>();

        params.put("inputCol", "data");
        params.put("outputCol", "pdfbox-text");

        final SparkModelContainer textractorTfContainer = new SparkModelContainer(
                this.modelSerdes.serializeTransformer(textractorTf),
                textractorTf.getClass().toString(),
                "pdfbox-textractor",
                "data",
                "pdfbox-text",
                params,
                false
        );

        SparkModelIncomingDto pdfboxTextractorIDto = new SparkModelIncomingDto(
                textractorTfContainer
        );

        this.webTestClient
                .post()
                .uri(baseurl + port + "/classification-model")
                .bodyValue(pdfboxTextractorIDto)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(SparkModelReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    SparkModelReturnDto sparkModelReturnDto = resp.getResponseBody();
                    assert null != sparkModelReturnDto;
                    assert Arrays.equals(sparkModelReturnDto.getBytes(), pdfboxTextractorIDto.getSparkModelContainer().getBytes());
                    this.pdfboxTextractorId = sparkModelReturnDto.getId();
                });
    }


    @Test
    @Order(15)
    public void testPostOcrTextractor() throws IOException {
        TessOcrTf tessOcrTf = new TessOcrTf("tess-ocr-textractor");

        HashMap<String, String> params = new HashMap<>();

        params.put("inputCol", "data");
        params.put("outputCol", "ocr-text");

        final SparkModelContainer textractorTfContainer = new SparkModelContainer(
                this.modelSerdes.serializeTransformer(tessOcrTf),
                tessOcrTf.getClass().toString(),
                "tess-ocr-textractor",
                "data",
                "ocr-text",
                params,
                false
        );

        SparkModelIncomingDto pdfboxTextractorIDto = new SparkModelIncomingDto(
                textractorTfContainer
        );

        this.webTestClient
                .post()
                .uri(baseurl + port + "/classification-model")
                .bodyValue(pdfboxTextractorIDto)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(SparkModelReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    SparkModelReturnDto sparkModelReturnDto = resp.getResponseBody();
                    assert null != sparkModelReturnDto;
                    assert Arrays.equals(sparkModelReturnDto.getBytes(), pdfboxTextractorIDto.getSparkModelContainer().getBytes());
                    this.ocrTextractorId = sparkModelReturnDto.getId();
                });
    }

    @Test
    @Order(25)
    public void testPostAndApplyPgoalFromTfs() throws IOException {
        UUID[] tfIds = new UUID[3];
        tfIds[0] = this.bobTfId;
        tfIds[1] = this.pdfboxTextractorId;
        tfIds[2] = this.ocrTextractorId;


        PipelineGoalIncomingDto pipelineGoalIncomingDto = new PipelineGoalIncomingDto(
                "simple-text-transformation",
                "simple-text-transformation",
                tfIds
        );

        this.webTestClient
                .post()
                .uri(baseurl + port + "/pipeline-goal")
                .bodyValue(pipelineGoalIncomingDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PipelineGoalReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    assert null != resp.getResponseBody();

                    assert Arrays.equals(
                            pipelineGoalIncomingDto.getModelIds(),
                            Objects.requireNonNull(resp.getResponseBody()).getIdsOfModelsUsed());
                    this.pGoalId = resp.getResponseBody().getId();
                });


        DocumentContainer documentContainer = new DocumentContainer(
                StreamUtils.copyToByteArray(new FileInputStream(new File("src/test/resources/jobBulletinPdfs/1605436304257-layout.pdf"))),
                "some-file",
                "application/pdf"
        );

        IncomingDocumentDto incomingDocumentDto = new IncomingDocumentDto(
                documentContainer,
                this.pGoalId
        );


        this.webTestClient
                .post()
                .uri(baseurl + port + "/model-serve/process-no-classification")
                .bodyValue(incomingDocumentDto)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(SparkDatasetReturnDto.class)
                .consumeWith(
                        resp -> {
                            assert null != resp;
                            SparkDatasetReturnDto datasetReturnDto = resp.getResponseBody();
                            assert null != datasetReturnDto;
                            LOGGER.info("Printing Dataset Raw JSON Content");
                            Arrays.stream(datasetReturnDto.getSparkDsJson()).sequential().forEach(System.out::println);

                            StringBuilder sb = new StringBuilder();

                            Arrays.stream(datasetReturnDto.getSparkDsJson()).sequential().forEachOrdered(
                                    row -> sb.append(row)
                            );


                            SparkDatasetUtils datasetUtils = new SparkDatasetUtils(this.sparkSession);

                            Dataset<Row> ds = datasetUtils.getDatasetFromJson(sb.toString());

                            LOGGER.info("showing returned dataset");
                            ds.show();
                        });


    }

}
