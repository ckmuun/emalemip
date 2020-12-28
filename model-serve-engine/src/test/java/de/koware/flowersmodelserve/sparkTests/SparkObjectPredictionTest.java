package de.koware.flowersmodelserve.sparkTests;

import com.databricks.sparkdl.DeepImageFeaturizer;
import modelServe.ModelServeSparkConfig;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SparkObjectPredictionTest {

    private final Logger LOGGER = LoggerFactory.getLogger(SparkObjectPredictionTest.class);

    private SparkSession sparkSession;
    private LogisticRegressionModel lrm;
    private DeepImageFeaturizer imageFeaturizer;
    private HashMap<String , Vector> featureVectorsOfTestImages;
    private HashMap<String, Dataset<Row>> testDsOrderedbyLabel;

    private static final String DAISY = "daisy";
    private static final String ROSE = "rose";
    private static final String DANDELION = "dandelion";
    private static final String SUNFLOWER = "sunflower";
    private static final String TULIP = "tulip";


    @BeforeAll
    public void setupSparkSession() {
        this.sparkSession = ModelServeSparkConfig.sparkSession();
        featureVectorsOfTestImages = new HashMap<String, Vector>();
        this.testDsOrderedbyLabel = new HashMap<String, Dataset<Row>>();

        LOGGER.info("loading logistic regression model");
        LogisticRegressionModel lrm = LogisticRegressionModel.load("src/main/resources/cfModel");
        assert null != lrm;
        this.lrm = lrm;

        // setup featurizer
        DeepImageFeaturizer featurizer = new DeepImageFeaturizer();
        featurizer.setModelName("InceptionV3");
        featurizer.setInputCol("image");
        featurizer.setOutputCol("features");
        this.imageFeaturizer = featurizer;

    }


    @Test
    @Order(1)
    public void testLoadTestImage() {
        LOGGER.info("Loading test images");
//     val daisyDf: DataFrame = sparkSession.read.format("image").load(daisyDir)
        Dataset<Row> daisyDs = this.sparkSession.read().format("image").load("src/test/resources/singleDaisy");
        assert null != daisyDs;

        Dataset<Row> dandelionDs = this.sparkSession.read().format("image").load("src/test/resources/singleDandelion");
        assert null != dandelionDs;

        Dataset<Row> roseDs = this.sparkSession.read().format("image").load("src/test/resources/singleRose");
        assert null != roseDs;

        for(String column: roseDs.columns()) {
            LOGGER.info("column name: " + column);
        }
        roseDs.show(false);
        roseDs.show();
        roseDs.printSchema();

        Dataset<Row> sunflowerDs = this.sparkSession.read().format("image").load("src/test/resources/singleSunflower");
        assert null != sunflowerDs;

        Dataset<Row> tulipDs = this.sparkSession.read().format("image").load("src/test/resources/singleTulip");
        assert null != tulipDs;

        LOGGER.info("Featurizing test images");
        // test featurization
        Dataset<Row> daisyDsFeaturized = imageFeaturizer.transform(daisyDs);
        assert null != daisyDsFeaturized;
        assert null != daisyDsFeaturized.colRegex("features");

        Dataset<Row> roseDsFeaturized = imageFeaturizer.transform(roseDs);
        assert null != roseDsFeaturized;
        assert null != roseDsFeaturized.colRegex("features");

        Dataset<Row> dandelionDsFeaturized = imageFeaturizer.transform(dandelionDs);
        assert null != dandelionDsFeaturized;
        assert null != dandelionDsFeaturized.colRegex("features");

        for(String column: roseDsFeaturized.columns()) {
            LOGGER.info("column name: " + column);
        }
 //       roseDsFeaturized.show(false);


        Dataset<Row> tulipDsFeaturized = imageFeaturizer.transform(tulipDs);
        assert null != tulipDsFeaturized;
        assert null != tulipDsFeaturized.colRegex("features");

        Dataset<Row> sunflowerDsFeaturized = imageFeaturizer.transform(sunflowerDs);
        assert null != sunflowerDsFeaturized;
        assert null != sunflowerDsFeaturized.colRegex("features");

        this.testDsOrderedbyLabel.put(DAISY, daisyDsFeaturized);
        this.testDsOrderedbyLabel.put(ROSE, roseDsFeaturized);
        this.testDsOrderedbyLabel.put(DANDELION, dandelionDsFeaturized);
        this.testDsOrderedbyLabel.put(TULIP, tulipDsFeaturized);
        this.testDsOrderedbyLabel.put(SUNFLOWER, sunflowerDsFeaturized);


        LOGGER.info("Predicting all test images...");
        // test classification
        // Step 1 classify all test images
        Vector daisyFeatures = collectFeatureVector(daisyDsFeaturized);
        this.featureVectorsOfTestImages.put(DAISY, daisyFeatures);
        double resultDaisy = lrm.predict(collectFeatureVector(daisyDsFeaturized));
        LOGGER.debug("result for Daisy picture: " + resultDaisy);
        //assert getByNumericIdFromSparkModel((int) resultDaisy) == DAISY;

        Vector roseFeatures = collectFeatureVector(roseDsFeaturized);
        this.featureVectorsOfTestImages.put(ROSE, roseFeatures);
        double resultRose = lrm.predict(roseFeatures);
        LOGGER.debug("result for Rose picture: " + resultRose);
        //assert getByNumericIdFromSparkModel((int) resultRose) == ROSE;


        Vector dandelionFeatures = collectFeatureVector(dandelionDsFeaturized);
        this.featureVectorsOfTestImages.put(DANDELION, dandelionFeatures);
        double resultDandelion = lrm.predict(dandelionFeatures);
        LOGGER.debug("result for Dandelion picture: " + resultDandelion);
        //assert getByNumericIdFromSparkModel((int) resultDandelion) == DANDELION;

        Vector tulipFeatures = collectFeatureVector(tulipDsFeaturized);
        this.featureVectorsOfTestImages.put(TULIP, tulipFeatures);
        double resultTulip = lrm.predict(tulipFeatures);
        LOGGER.debug("result for Tulip picture: " + resultTulip);
        //assert getByNumericIdFromSparkModel((int) resultTulip) == TULIP;

        Vector sunflowerFeatures = collectFeatureVector(sunflowerDsFeaturized);
        this.featureVectorsOfTestImages.put(SUNFLOWER, sunflowerFeatures);
        double resultSunflower = lrm.predict(sunflowerFeatures);
        LOGGER.debug("result for sunflower picture: " + resultSunflower);
        //assert getByNumericIdFromSparkModel((int) resultSunflower) == SUNFLOWER;
    }

    @Test
    @Order(2)
    public void iterativePredictionTest() {
        LOGGER.info("Iterative Prediction Test");
        for (Map.Entry<String, Vector> entry : this.featureVectorsOfTestImages.entrySet()) {
            LOGGER.info("Testing prediciton for Enum value: " + entry.getKey());
            LOGGER.info("testing prediction for label "+ entry.getKey() );
            Vector detailedPrediction = this.lrm.predictRaw(entry.getValue());
            for (double d : detailedPrediction.toArray()) {
                LOGGER.info("prediction val: " + d);
            }
            //assert getByNumericIdFromSparkModel(detailedPrediction.argmax()) == entry.getKey();
        }
    }

    @Test
    @Order(3)
    public void iterativeProbabilitiesTest() {

        for(Map.Entry<String, Dataset<Row>> labeledTestDsEntry : this.testDsOrderedbyLabel.entrySet()) {
            LOGGER.info("testing probs for label: " + labeledTestDsEntry.getKey());
            Dataset<Row> tfDs = labeledTestDsEntry.getValue();
            Dataset<Row> transformedDs = lrm.transform(tfDs);
            Row[] rows = (Row[]) transformedDs.select("probability").collect(); // cast to Row[] is necessary
            Vector probabilities = rows[0].getAs("probability");
            for( double prob: probabilities.toArray()) {
                LOGGER.info("prob: " + prob);
            }
            //assert getByNumericIdFromSparkModel(probabilites.argmax()) == labeledTestDsEntry.getKey();
        }

    }


    @Test
    @Order(3)
    public void testPredictionOfHardToDetectImage() {

    }

    @Test
    @Order(3)
    public void testDetectionJunkDocument() {

    }

    private Vector collectFeatureVector(Dataset<Row> featurizedDs) {
        // this cast to Row[] is required, although IntelliJ says otherwise
        Row[] rows = (Row[]) featurizedDs.collect();
        Row row = rows[0];
        return row.getAs("features");
    }
}
