package de.koware.flowersmodelserve.apiTests;

import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.correlation.CorrelationReturnDto;
import de.koware.flowersmodelserve.api.documentApi.IncomingDocumentDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsIncomingDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsReturnDto;
import de.koware.flowersmodelserve.api.functionalClassification.ClassificationResultReturnDto;
import de.koware.flowersmodelserve.api.functionalClassification.NotProcessableErrorDto;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalIncomingDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelIncomingDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelReturnDto;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalReturnDto;
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
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebFlux
@AutoConfigureWebTestClient(timeout = "36000")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiControllerImageDocIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiControllerImageDocIT.class);

    @Autowired
    private WebTestClient webTestClient;

    private static final String baseurl = "http://localhost:";

    @LocalServerPort
    private String port;


    // temp state in between tests
    private UUID pGoalId;
    private UUID featurizerId;
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
    public void testPostDocumentLabels() {
        LOGGER.info("create and post new document labels ");

        DocumentLabelsIncomingDto incomingDto = ModelServeTestUtils.getDocumentLabelsIncomingDto();
        this.webTestClient
                .post()
                .uri(baseurl + port + "document-labels")
                .body(BodyInserters.fromValue(incomingDto))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(DocumentLabelsReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    DocumentLabelsReturnDto returnDto = resp.getResponseBody();
                    assert null != returnDto;
                    assert returnDto.getLabelName().equals(incomingDto.getLabelName());
                    this.docLabelId = returnDto.getId();
                });
    }

    @Test
    @Order(4)
    public void testPostFeaturizer() throws IOException {
        LOGGER.info("posting featurizer DTO");
        final SparkModelIncomingDto featurizerDto = ModelServeTestUtils.createFeaturizerDto();

        this.webTestClient
                .post()
                .uri(baseurl + port + "/classification-model")
                .bodyValue(featurizerDto)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(SparkModelReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    SparkModelReturnDto sparkModelReturnDto = resp.getResponseBody();
                    assert null != sparkModelReturnDto;
                    assert Arrays.equals(sparkModelReturnDto.getBytes(), featurizerDto.getSparkModelContainer().getBytes());
                    this.featurizerId = sparkModelReturnDto.getId();
                });
    }

    @Test
    @Order(5)
    public void testPostLrmClassifier() throws IOException {
        LOGGER.info("uploading classification model as DTO to API");
        final SparkModelIncomingDto classificationModelDto = ModelServeTestUtils.getClassificationModelDto();

        this.webTestClient
                .post()
                .uri(baseurl + port + "/classification-model")
                .bodyValue(classificationModelDto)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(SparkModelReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    SparkModelReturnDto returnDto = resp.getResponseBody();
                    assert null != returnDto;
                    examinePersistedModel(returnDto, classificationModelDto);
                    this.lrModelId = returnDto.getId();
                });

    }

    @Test
    @Order(8)
    public void testPostPGoal() {
        LOGGER.info("step 0: create Procedual Goal");
        PipelineGoalIncomingDto incomingDto = new PipelineGoalIncomingDto(
                "this is an informative description",
                "one-step-flower-classification",
                new UUID[]{this.featurizerId, this.lrModelId},
                this.docLabelId
        );

        this.webTestClient
                .post()
                .uri(baseurl + port + "/pipeline-goal")
                .bodyValue(incomingDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PipelineGoalReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    PipelineGoalReturnDto pipelineGoalReturnDto = resp.getResponseBody();
                    assert null != pipelineGoalReturnDto;

                    assert pipelineGoalReturnDto.getIdsOfModelsUsed()[0].equals(this.featurizerId);
                    assert pipelineGoalReturnDto.getIdsOfModelsUsed()[1].equals(this.lrModelId);

                    this.pGoalId = pipelineGoalReturnDto.getId();
                });
    }


    @Test
    @Order(25)
    public void testPostPipelineGoalSimple() {
        final PipelineGoalIncomingDto incomingDto = ModelServeTestUtils.createProceudalGoalDtoNoModels();

        this.webTestClient
                .post()
                .uri(baseurl + port + "/pipeline-goal")
                .bodyValue(incomingDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PipelineGoalReturnDto.class)
                .consumeWith(resp -> Arrays.equals(
                        incomingDto.getModelIds(),
                        Objects.requireNonNull(resp.getResponseBody()).getIdsOfModelsUsed()));
    }

    @Test
    public void testPostAndRetrieveClassificationModel() throws IOException {
        LOGGER.info("uploading classification model as DTO to API to retrieve later");
        final SparkModelIncomingDto classificationModelDto = ModelServeTestUtils.getClassificationModelDto();

        final SparkModelReturnDto[] persisted = new SparkModelReturnDto[1];
        this.webTestClient
                .post()
                .uri(baseurl + port + "/classification-model")
                .bodyValue(classificationModelDto)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(SparkModelReturnDto.class)
                .consumeWith(resp -> examinePersistedModel(Objects.requireNonNull(resp.getResponseBody()), classificationModelDto))
                .consumeWith(resp1 -> persisted[0] = resp1.getResponseBody());

        LOGGER.info("id of persisted: " + persisted[0].getId());
        LOGGER.info("upload and check of upload return dto done ");
        LOGGER.info("querying the dto that was just updated");

        this.webTestClient
                .get()
                .uri(baseurl + port + "classification-model/" + persisted[0].getId())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SparkModelReturnDto.class)
                .consumeWith(resp -> examinePersistedModel(Objects.requireNonNull(resp.getResponseBody()), classificationModelDto));
    }

    @Test
    @Order(15)
    public void testPostValidDoc() {
        IncomingDocumentDto incomingDocumentDto = ModelServeTestUtils.createNotYetClassifiedDocDto(
                "daisy",
                this.pGoalId
        );

        this.webTestClient
                .post()
                .uri(baseurl + port + "/model-serve/classify-rx")
                .bodyValue(incomingDocumentDto)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(ClassificationResultReturnDto.class)
                .consumeWith(
                        resp -> {
                            assert null != resp;
                            ClassificationResultReturnDto resultReturnDto = resp.getResponseBody();
                            assert null != resultReturnDto;
                            assert resultReturnDto.isSuccessful();

                            assert resultReturnDto.getTextLabel().toLowerCase().equals("daisy");
                            assert resultReturnDto.getNumericLabel() == 1.0;
                            this.correlationKey = resultReturnDto.getCorrelationKey();
                        });

    }

    @Test
    @Order(20)
    public void testCorrelationOfDocAndResult() {
        assert null != correlationKey;
        LOGGER.info("testing correlation of doc + previous classification result with keyv {}", this.correlationKey);

        this.webTestClient
                .get()
                .uri(baseurl + port + "/correlate/" + this.correlationKey)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CorrelationReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    CorrelationReturnDto crDto = resp.getResponseBody();
                    assert null != crDto;
                    assert null != crDto.getDocReturnDto();
                    assert null != crDto.getCreDto();
                    assert null != crDto.getSparkDatasetReturnDto();

                    LOGGER.info("size ok, testing list items");
                    String[] sparkDs = crDto.getSparkDatasetReturnDto().getSparkDsJson();
                    LOGGER.info("Printing Dataset Raw JSON Content");
                    Arrays.stream(sparkDs).sequential().forEach(System.out::println);

                });
    }


    @Test
    public void testPostJunkDoc() {
        IncomingDocumentDto dtoJunk = ModelServeTestUtils.createNycDtoJunk(UUID.randomUUID());

        this.webTestClient
                .post()
                .uri(baseurl + port + "/model-serve/classify-rx")
                .bodyValue(dtoJunk)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(NotProcessableErrorDto.class);
    }


    private void examinePersistedModel(SparkModelReturnDto ref, SparkModelIncomingDto other) {
        LOGGER.info("Examining the persisted and returned model against the original one");
        assert Arrays.equals(ref.getBytes(), other.getSparkModelContainer().getBytes());
    }
}
