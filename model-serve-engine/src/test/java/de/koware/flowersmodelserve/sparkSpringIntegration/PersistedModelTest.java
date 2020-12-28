package de.koware.flowersmodelserve.sparkSpringIntegration;

import com.databricks.sparkdl.DeepImageFeaturizer;
import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.functionalClassification.SparkModelSerializer;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelDao;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import modelServe.ModelServeSparkConfig;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.io.IOException;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PersistedModelTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistedModelTest.class);

    // spark fields
    private SparkSession sparkSession;
    private LogisticRegressionModel lrm;
    private Vector daiysImageFeatures;
    private Dataset<Row> daisyDs;

    //spring beans
    @Autowired
    private SparkModelDao modelDao;

    @Autowired
    private SparkModelSerializer mlModelSerializer;

    @BeforeAll
    public void setup() {
        this.sparkSession = ModelServeSparkConfig.sparkSession();
        LOGGER.info("loading logistic regression model");
        LogisticRegressionModel lrm = LogisticRegressionModel.load("src/main/resources/cfModel");
        assert null != lrm;
        this.lrm = lrm;

        // setup featurizer
        DeepImageFeaturizer featurizer = new DeepImageFeaturizer();
        featurizer.setModelName("InceptionV3");
        featurizer.setInputCol("image");
        featurizer.setOutputCol("features");

        Dataset<Row> daisyDs = this.sparkSession.read().format("image").load("src/test/resources/singleDaisy");
        assert null != daisyDs;
        this.daisyDs = daisyDs;

        this.daiysImageFeatures = ModelServeTestUtils.collectFeatureVector(featurizer.transform(daisyDs));
        assert null != daiysImageFeatures;
    }


    private void testPersistedModel(SparkModelEntity persistedModel) {
        LOGGER.info("testing persisted Model");
        LogisticRegressionModel persistedLrm = null;
        try {
            persistedLrm = (LogisticRegressionModel) mlModelSerializer
                    .simpleDeserializationClaModel(persistedModel);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
            return;
        }

        double prediction = persistedLrm.predict(this.daiysImageFeatures);

        assert prediction == 1.0;
        assert this.lrm.predictRaw(daiysImageFeatures).equals(persistedLrm.predictRaw(daiysImageFeatures));

    }

    @Test
    public void testPersistLrmSimple() throws IOException {
        LOGGER.info("creating raw entity to be persisted");

        LOGGER.info("simple test to see if something is returned from DAO");
        // just test if something is returned by save
        StepVerifier
                .create(this.modelDao.save(ModelServeTestUtils.getSimpleClassificationModelEty()))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void testPersistLrmAdvanced() throws IOException {
        LOGGER.info("creating raw entity to be persisted");

        LOGGER.info("content-wise test for model ");
        // content-wise test
        StepVerifier
                .create(this.modelDao.save(ModelServeTestUtils.getSimpleClassificationModelEty()))
                .expectSubscription()
                .assertNext(this::testPersistedModel)

                .verifyComplete();

    }

}
