package de.koware.flowersmodelserve.apiTests;


import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.correlation.CorrelationService;
import de.koware.flowersmodelserve.api.documentApi.IncomingDocumentDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsIncomingDto;
import de.koware.flowersmodelserve.api.functionalClassification.ClassificationResultReturnDto;
import de.koware.flowersmodelserve.api.functionalClassification.IncomingDocumentValidator;
import de.koware.flowersmodelserve.api.functionalClassification.RxModelServeService;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalIncomingDto;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalReturnDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelService;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsService;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalService;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelIncomingDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelReturnDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsReturnDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiServicesImagDocTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServicesImagDocTest.class);

    @Autowired
    private PipelineGoalService pipelineGoalService;

    @Autowired
    private SparkModelService sparkModelService;

    @Autowired
    private DocumentLabelsService documentLabelsService;

    @Autowired
    private RxModelServeService sparkModelServeService;


    @Autowired
    private CorrelationService correlationService;

    @Autowired
    private IncomingDocumentValidator documentValidator;

    //fields populated during runtime
    private PipelineGoalReturnDto pGoalDto;

    private DocumentLabelsReturnDto documentLabelsReturnDto;

    private UUID correlationKey;

    @BeforeAll
    public void setup() throws IOException {
        LOGGER.info("setup Document Label Ety");

        LOGGER.info("setting up prerequisites");
        LOGGER.info("step 0 create Document Labels");
        DocumentLabelsIncomingDto documentLabelsDto = ModelServeTestUtils.getDocumentLabelsIncomingDto();

        DocumentLabelsReturnDto[] persistedlabels = new DocumentLabelsReturnDto[1];

        StepVerifier.create(this.documentLabelsService.createNewDocumentLabels(documentLabelsDto))
                .expectSubscription()
                .thenConsumeWhile(persisted -> {
                    persistedlabels[0] = persisted;
                    return true;
                })
                .thenConsumeWhile(persisted -> persisted
                        .getLabelsAsString()
                        .equals(documentLabelsDto.getStringLabel2NumericLabelMapping()
                                .keySet()))
                .verifyComplete();

        LOGGER.info("step 1 create Classification Model");
        SparkModelIncomingDto sparkModelIncomingDto = ModelServeTestUtils.getClassificationModelDtoWithDocLabelId(
                persistedlabels[0]
                        .getId()
        );

        SparkModelReturnDto[] sparkModelReturnDtos = new SparkModelReturnDto[2];

        LOGGER.info("step 1.4.1 create featurizer transformer");
        SparkModelIncomingDto featurizerDto = ModelServeTestUtils.createFeaturizerDto
                ();
        StepVerifier.create(this.sparkModelService.createNewClassificationModel(featurizerDto))
                .expectSubscription()
                .thenConsumeWhile(featurizerReturnDto -> {
                    sparkModelReturnDtos[1] = featurizerReturnDto;
                    return true;
                })
                .verifyComplete();
        LOGGER.info("step 1.4.2 query featurizer");
        StepVerifier.create(this.sparkModelService.findByModelId(sparkModelReturnDtos[1].getId()))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();


        LOGGER.info("step 1.1 persist classification model");
        StepVerifier.create(this.sparkModelService.createNewClassificationModel(sparkModelIncomingDto))
                .expectSubscription()
                .thenConsumeWhile(persistedSparkModel -> {
                    sparkModelReturnDtos[0] = persistedSparkModel;
                    return true;
                })
                .thenConsumeWhile(persisted -> Arrays.equals(persisted.getBytes(), sparkModelIncomingDto.getSparkModelContainer().getBytes()))
                .verifyComplete();

        LOGGER.info("step 1.2 retrieve the model just persisted");
        StepVerifier.create(this.sparkModelService.findByModelId(sparkModelReturnDtos[0].getId()))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();


        LOGGER.info("step 1.3 retrieve document labels");
        LOGGER.info("retrieving doc label with id: {}", persistedlabels[0].getId());
        StepVerifier.create(this.documentLabelsService.findById(persistedlabels[0].getId()))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();


        LOGGER.info("step 2: create Procedual Goal");
        PipelineGoalIncomingDto incomingDto = new PipelineGoalIncomingDto(
                "this is an informative description",
                "one-step-flower-classification",
                new UUID[]{
                        sparkModelReturnDtos[1].getId(),
                        sparkModelReturnDtos[0].getId()},
                persistedlabels[0].getId()
        );

        StepVerifier.create(this.sparkModelService.findByModelId(sparkModelReturnDtos[0].getId()))
                .expectSubscription()
                .thenConsumeWhile(modelDto -> modelDto.getId().equals(sparkModelReturnDtos[0].getId()))
                .verifyComplete();

        PipelineGoalReturnDto[] pipelineGoalReturnDtos = new PipelineGoalReturnDto[1];
        StepVerifier.create(this.pipelineGoalService.createNewProcedualGoal(incomingDto))
                .expectSubscription()
                .thenConsumeWhile(pGoalDto -> {
                    pipelineGoalReturnDtos[0] = pGoalDto;
                    return true;
                })
                .thenConsumeWhile(dto -> Arrays.equals(dto.getIdsOfModelsUsed(), incomingDto.getModelIds()))
                .verifyComplete();

        StepVerifier.create(this.pipelineGoalService.findProcedualGoalByid(pipelineGoalReturnDtos[0].getId()))
                .expectSubscription()
                .consumeNextWith(pGoal -> {
                    assert pGoal.getIdsOfModelsUsed().length == 2;
                    assert persistedlabels[0].getId().equals(pGoal.getDocLabelId());
                    LOGGER.info("doc label id : {}", pGoal.getDocLabelId());
                })
                .verifyComplete();

        this.pGoalDto = pipelineGoalReturnDtos[0];
    }

    @Test
    @Order(5)
    public void testPipelineGoalEtyCreation() {
        PipelineGoalIncomingDto dto = ModelServeTestUtils.createProceudalGoalDtoNoModels();

        StepVerifier.create(this.pipelineGoalService.createNewProcedualGoal(dto))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @Order(10)
    public void testPersistAndRetrieve() {
        PipelineGoalIncomingDto dto = ModelServeTestUtils.createProceudalGoalDtoNoModels();

        StepVerifier.create(this.pipelineGoalService.createNewProcedualGoal(dto))
                .expectSubscription()
                .thenConsumeWhile(e -> Arrays.equals(e.getIdsOfModelsUsed(), dto.getModelIds()))
                .verifyComplete();
    }

    @Test
    @Order(15)
    public void testClassificationModelPersistAndRetrieve() throws IOException {
        SparkModelIncomingDto classificationModelDto = ModelServeTestUtils.getClassificationModelDto();

        StepVerifier.create(this.sparkModelService.createNewClassificationModel(classificationModelDto))
                .expectSubscription()
                .thenConsumeWhile(e -> Arrays.equals(e.getBytes(), classificationModelDto.getSparkModelContainer().getBytes()))
                .verifyComplete();
    }

    @Test
    @Order(20)
    public void testSparkModelServeServiceRx() {

        LOGGER.info("step 3 : combine into call for classification");
        LOGGER.info("step 3.1 create unclassfied dto");
        String referenceLabel = "daisy";
        IncomingDocumentDto notYetClassifiedDocDto = ModelServeTestUtils.
                createNotYetClassifiedDocDto(
                        referenceLabel,
                        pGoalDto.getId()
                );

        StepVerifier.create(
                this.sparkModelServeService.processAndClassifyIncomingDocument(notYetClassifiedDocDto))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();


        StepVerifier.create(
                this.sparkModelServeService.processAndClassifyIncomingDocument(notYetClassifiedDocDto))
                .expectSubscription()
                .consumeNextWith(dto -> {
                    assert null != dto;
                    evaluateClassifiedDto((ClassificationResultReturnDto) dto,
                            referenceLabel,
                            1.0);
                })
                .verifyComplete();
        LOGGER.info("done testing spark model Rx ");
    }

    @Test
    @Order(25)
    public void testCorrelationService() {

        assert null != this.correlationKey;

        StepVerifier.create(this.correlationService.correlateDocumentAndResult(this.correlationKey))
                .expectSubscription()
                .consumeNextWith(dto -> {
                    LOGGER.info("evaluating returned correlation dto");
                    assert null != dto;
                    assert null != dto.getCreDto();
                    assert null != dto.getDocReturnDto();
                    assert null != dto.getSparkDatasetReturnDto();
                })
                .verifyComplete();

    }


    @Test
    @Order(100)
    public void testClassificationOfJunkDocument() {
        LOGGER.info("testing with unreadable junk document");

        IncomingDocumentDto nycDtoJunk = ModelServeTestUtils.createNycDtoJunk(this.pGoalDto.getId());

        assert !(this.documentValidator.isProcessable(nycDtoJunk));
        // no further testing here, as the inputDocumentValidator is located within the controller

    }

    @Test
    @Order(110)
    public void testHardToDetectFlower() {
        LOGGER.info("testing with hard to detect flower");

        IncomingDocumentDto hardToDetectDandelion = ModelServeTestUtils.createNycDtoHardToDetectDandelion(this.pGoalDto.getId());
        StepVerifier.create(this.sparkModelServeService.processAndClassifyIncomingDocument(hardToDetectDandelion))
                .expectSubscription()
                .consumeNextWith(result -> {
                    ClassificationResultReturnDto crrd = (ClassificationResultReturnDto) result;
                    LOGGER.info("hard to detect image successful: {}", crrd.isSuccessful());
                    assert !(crrd.isSuccessful());
                    assert crrd.getpGoalId().equals(hardToDetectDandelion.getpGoalId());
                })
                .verifyComplete();
    }

    @Test
    @Order(115)
    public void testGenerationOfNotFlowerImage() {
        LOGGER.info("testing wiht image that is definitely not a Flower");

    }


    private void evaluateClassifiedDto(ClassificationResultReturnDto classificationResultReturnDto,
                                       String referenceLabel,
                                       double numericRefLabel
    ) {
        LOGGER.info("evaluating Classfied document dto, REFERENCE LABEL: {}", referenceLabel);

        assert null != classificationResultReturnDto;

        assert classificationResultReturnDto.getNumericLabel() == numericRefLabel;
        this.correlationKey = classificationResultReturnDto.getCorrelationKey();
    }
}

