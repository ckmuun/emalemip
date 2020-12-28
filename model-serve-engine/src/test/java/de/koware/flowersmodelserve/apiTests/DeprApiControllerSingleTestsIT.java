package de.koware.flowersmodelserve.apiTests;

/*
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
*/
@Deprecated
public class DeprApiControllerSingleTestsIT {

    /*
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiControllerSingleTestsIT.class);

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private String port;
    private static final String baseurl = "http://localhost:";

    private UUID documentLabelsId;
    private UUID pGoalId;


    private UUID[] sparkModelIds = new UUID[2];

    @BeforeAll
    public void setup() {
        this.webTestClient = webTestClient
                .mutate()
                .build();
    }


    @Test
    @Order(1)
    public void testHealth() {
        LOGGER.info("testing health of service");

        webTestClient
                .get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("{\"status\":\"UP\"}");
    }

    @Test
    @Order(2)
    public void testGreeting() {
        LOGGER.info("testing greeting");
        webTestClient
                .get()
                .uri("/model-serve/greeting")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("hello");
    }

    @Test
    @Order(10)
    public void testCreateDocumentLabels() {
        LOGGER.info("testing creation of document label via API");
        LOGGER.info("create and post new document labels ");

        DocumentLabelsIncomingDto incomingDto = FichteTestUtils.getDocumentLabelsIncomingDto();
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
                    this.documentLabelsId = returnDto.getId();
                });
    }


    @Test
    @Order(15)
    public void testCreateSparkModel() throws IOException {
        LOGGER.info("testing creation of spark model");

        LOGGER.info("creating featurizer model");
        SparkModelIncomingDto featurizerDto = FichteTestUtils.createFeaturizerDto();
        this.webTestClient.post()
                .uri("/classification-model")
                .body(BodyInserters.fromValue(featurizerDto))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SparkModelReturnDto.class)
                .consumeWith(response -> {
                    SparkModelReturnDto dto = response.getResponseBody();
                    assert null != dto;
                    assert null != dto.getId();
                    assert null != dto.getBytes();
                    assert Arrays.equals(dto.getBytes(), featurizerDto.getSparkModelContainer().getBytes());
                    this.sparkModelIds[0] = dto.getId();

                });
        LOGGER.info("testing creation of classification Model (Logistic Regression)");

        SparkModelIncomingDto lrmDto = FichteTestUtils.getClassificationModelDto();
        this.webTestClient.post()
                .uri("/classification-model")
                .body(BodyInserters.fromValue(lrmDto))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SparkModelReturnDto.class)
                .consumeWith(response -> {
                    SparkModelReturnDto dto = response.getResponseBody();
                    assert null != dto;
                    assert null != dto.getId();
                    assert null != dto.getBytes();
                    assert Arrays.equals(dto.getBytes(), lrmDto.getSparkModelContainer().getBytes());
                    this.sparkModelIds[1] = dto.getId();
                });
    }

    @Test
    @Order(20)
    public void testCreatePipelineGoal() {

        assert null != sparkModelIds[0];
        assert null != sparkModelIds[1];

        PipelineGoalIncomingDto incomingDto = new PipelineGoalIncomingDto(
                "this is an informative description",
                "one-step-flower-classification",
                new UUID[]{
                        sparkModelIds[0],
                        sparkModelIds[1]},
                this.documentLabelsId
        );

        webTestClient
                .post()
                .uri("/pipeline-goal")
                .body(BodyInserters.fromValue(incomingDto))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PipelineGoalReturnDto.class)
                .consumeWith(resp -> {
                    assert null != resp;
                    assert null != resp.getResponseBody();
                    assert resp.getResponseBody().getIdsOfModelsUsed()[0].equals(sparkModelIds[0]);
                    assert resp.getResponseBody().getIdsOfModelsUsed()[1].equals(sparkModelIds[1]);
                    this.pGoalId = resp.getResponseBody().getId();
                });
    }

    @Test
    @Order(25)
    public void testPostUnclassedDocDtoBasic() {
        IncomingDocumentDto iDto = FichteTestUtils.createNotYetClassifiedDocDto("DAISY", this.pGoalId);

        webTestClient
                .post()
                .uri("/model-serve/classify-rx")
                .body(BodyInserters.fromValue(iDto))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(ClassificationResultReturnDto.class)
                .consumeWith(
                        serverResp -> {
                   assert null != serverResp;
                   ClassificationResultReturnDto resultReturnDto = serverResp.getResponseBody();
                   assert null != resultReturnDto;

                });
    }



    @Test
    @Order(30)
    public void testPostUnclassedDocDtoCheckContent() {

    }

    @Test
    @Order(35)
    public void testPostUnclassedDocDtoJunkdoc() {

    }
*/
}
