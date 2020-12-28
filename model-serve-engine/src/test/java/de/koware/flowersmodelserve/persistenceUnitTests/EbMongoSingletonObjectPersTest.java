package de.koware.flowersmodelserve.persistenceUnitTests;


import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelDao;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsDao;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalDao;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import de.koware.flowersmodelserve.api.functionalClassification.SparkModelSerializer;
import org.apache.spark.ml.classification.ClassificationModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EbMongoSingletonObjectPersTest {


    @Autowired
    private SparkModelDao sparkModelDao;

    @Autowired
    private DocumentLabelsDao documentLabelsDao;

    @Autowired
    private PipelineGoalDao pipelineGoalDao;

    @Autowired
    private SparkModelSerializer mlModelSerializer;


    @BeforeAll
    public void setup() {

    }


    @Test
    public void testPersistClassificationModel() throws IOException, ClassNotFoundException {

        SparkModelEntity sparkModelEntity = ModelServeTestUtils.getSimpleClassificationModelEty();

        SparkModelEntity ety = this.sparkModelDao.save(sparkModelEntity).block();


        assert null != ety;
        ClassificationModel<?,?> classificationModel = mlModelSerializer.simpleDeserializationClaModel(ety);



    }


}
