package de.koware.flowersmodelserve.unit;

import com.databricks.sparkdl.DeepImageFeaturizer;
import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.functionalClassification.SparkModelSerializer;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelContainer;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import modelServe.ModelServeSparkConfig;
import org.apache.spark.ml.classification.ClassificationModel;
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

import java.io.IOException;
import java.util.HashMap;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ModelSerializationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelSerializationTest.class);

    private LogisticRegressionModel lrm;
    private SparkSession sparkSession;
    private Vector daiysImageFeatures;
    private Dataset<Row> daisyDs;


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

    @Test
    public void testSerializationOfSparkModelExplicitCast() throws IOException, ClassNotFoundException {
        LOGGER.info("creating raw entity to be persisted");
        SparkModelEntity modelEntity = ModelServeTestUtils.getSimpleClassificationModelEty();
        mlModelSerializer.simpleSerializationClaModel(this.lrm);

        LogisticRegressionModel serializedLrm = (LogisticRegressionModel)
                mlModelSerializer.simpleDeserializationClaModel(modelEntity);

        double result = this.lrm.predict(this.daiysImageFeatures);
        LOGGER.info("result from LRM loaded from disk: {}", result);

        double resultFromPersisted = serializedLrm.predict(this.daiysImageFeatures);

        LOGGER.info("result from LRM loaded from bytes:  {}", resultFromPersisted);

        assert result == resultFromPersisted;
        assert result == 1.0;
    }

    @Test
    public void testSerializationSparkModelNoCast() throws IOException, ClassNotFoundException {

        LOGGER.info("creating raw entity to be persisted");
        SparkModelEntity modelEntity = ModelServeTestUtils.getSimpleClassificationModelEty();
        mlModelSerializer.simpleSerializationClaModel(this.lrm);


        ClassificationModel<Vector, ?> model = (ClassificationModel<Vector, ?>) mlModelSerializer
                .simpleDeserializationClaModel(modelEntity);

        assert model instanceof LogisticRegressionModel;

        LogisticRegressionModel lrm = (LogisticRegressionModel) model;

        double result1 = model.predict(this.daiysImageFeatures);
        assert 1.0 == result1;

        double result = lrm.predict(this.daiysImageFeatures);

        assert 1.0 == result;

    }

    @Test
    public void errorBehaviourTestMlSerdes() {

        byte[] junk = new byte[5];

        SparkModelContainer smc = new SparkModelContainer(
                junk,
                "junk",
                "junk name",
                "no such column",
                "no such column",
                new HashMap<String, String>(),
                true
        );

        ClassificationModel<Vector, ?> model = null;
        SparkModelEntity sme = new SparkModelEntity(
                smc
        );

        try {
            model = this.mlModelSerializer.simpleDeserializationClaModel(
                    sme
            );

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.info("catching");
            e.printStackTrace();
            assert true;
            return;
        }
        LOGGER.error("Expected exception to occur, did not occur -> asserting false as ");
        assert false;
    }

}
