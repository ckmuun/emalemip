package de.koware.flowersmodelserve.sparkTests;

import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.linalg.DenseVector;
import org.apache.spark.ml.tree.LeafNode;
import org.apache.spark.mllib.tree.impurity.ImpurityCalculator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SparkByteCodeCompatibilityTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkByteCodeCompatibilityTest.class);

    @Test
    public void instantiateMLThing() {

        DecisionTreeClassificationModel classificationModel = new DecisionTreeClassificationModel(
                new LeafNode(1.0, 0.0, ImpurityCalculator.getCalculator("gini", new double[]{1.0})),
                1, 1);

        LOGGER.info("decision tree param: {}", classificationModel.getImpurity());

    }

    @Test
    public void instantiateRegression() {
        LogisticRegressionModel lrm = new LogisticRegressionModel("oi", new DenseVector(new double[]{1.0}), 1.0);
        lrm.setFeaturesCol("jegolo har megiddo");
        LOGGER.info("lrm param: " + lrm.getFeaturesCol());
    }


    @Test
    public void useLogisticRegressionObj() {
        LOGGER.info("using companion object of LogisticRegressionModel");
        LogisticRegressionModel.normalizeToProbabilitiesInPlace(new DenseVector(new double[]{1.0}));
    }

    @Test
    public void loadRegModelFromDisk() {
        LOGGER.info("loading LogisticRegressionModel without assigning");
        LogisticRegressionModel.load("src/main/resources/cfModel");
    }

    @Test
    public void loadRegModelFromDiskAssign() {
        LOGGER.info("loading LogisticRegressionModel without assigning");
        LogisticRegressionModel lrm = LogisticRegressionModel.load("src/main/resources/cfModel");
        lrm.setFeaturesCol("spöksonat");
        LOGGER.info("lrm junk feature col: " + lrm.getFeaturesCol());

    }

}
