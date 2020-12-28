package de.koware.flowersmodelserve.apiTests;

import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelDao;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelIncomingDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelReturnDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FluxSortingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FluxSortingTest.class);

    @Autowired
    private SparkModelDao sparkModelDao;

    @Autowired
    private SparkModelService sparkModelService;

    SparkModelReturnDto[] sparkModelReturnDtos = new SparkModelReturnDto[2];


    @BeforeAll
    public void setup() throws IOException {

        LOGGER.info("step 1.4.1 create featurizer transformer");
        SparkModelIncomingDto featurizerDto = ModelServeTestUtils.createFeaturizerDto();
        SparkModelIncomingDto sparkModelIncomingDto = ModelServeTestUtils.getClassificationModelDto();

        StepVerifier.create(this.sparkModelService.createNewClassificationModel(featurizerDto))
                .expectSubscription()
                .thenConsumeWhile(featurizerReturnDto -> {
                    sparkModelReturnDtos[0] = featurizerReturnDto;
                    return true;
                })
                .verifyComplete();
        LOGGER.info("step 1.4.2 query featurizer");
        StepVerifier.create(this.sparkModelService.findByModelId(sparkModelReturnDtos[0].getId()))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();


        LOGGER.info("step 1.1 persist classification model");
        StepVerifier.create(this.sparkModelService.createNewClassificationModel(sparkModelIncomingDto))
                .expectSubscription()
                .thenConsumeWhile(persistedSparkModel -> {
                    sparkModelReturnDtos[1] = persistedSparkModel;
                    return true;
                })
                .thenConsumeWhile(persisted -> Arrays.equals(persisted.getBytes(), sparkModelIncomingDto.getSparkModelContainer().getBytes()))
                .verifyComplete();

        LOGGER.info("step 1.2 retrieve the model just persisted");
        StepVerifier.create(this.sparkModelService.findByModelId(sparkModelReturnDtos[1].getId()))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

    }


    @Test
    @Order(5)
    public void testReturnDtosCorrectOrdering() {
        assert this.sparkModelReturnDtos[0]
                .getSparkModelContainer()
                .getClassAsString()
                .contains("DeepImageFeaturizer");
        assert this.sparkModelReturnDtos[1]
                .getSparkModelContainer()
                .getClassAsString()
                .contains("LogisticRegressionModel");
    }

    @Test
    @Order(15)
    public void testFluxOrderingFromDao() {

        LOGGER.info("testing flux ordering whoop");

        UUID[] uuids = new UUID[2];

        uuids[0] = this.sparkModelReturnDtos[0].getId();
        uuids[1] = this.sparkModelReturnDtos[1].getId();


        StepVerifier.create(this.sparkModelDao.findAllByPgGoalIdCollection(Arrays.asList(uuids)))
                .expectSubscription()
                .expectNextCount(2)
                .verifyComplete();

        StepVerifier.create(this.sparkModelDao.findAllByPgGoalIdCollection(Arrays.asList(uuids)))
                .expectSubscription()
                .consumeNextWith(ety -> {
                    LOGGER.info("asserting first element id in right order");
                    assert ety.getId().equals(uuids[0]);
                })
                .consumeNextWith(ety -> {
                    LOGGER.info("asserting second element id is in right order");
                    assert ety.getId().equals(uuids[1]);
                })
                .verifyComplete();
    }

}
