package de.koware.flowersmodelserve.apiTests;

import com.databricks.sparkdl.DeepImageFeaturizer;
import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.functionalClassification.RxModelServeService;
import modelServe.ModelServeSparkConfig;
import modelServe.ModelServeSparkWorker;
import org.apache.spark.ml.Transformer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ServeServiceUnitTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(ServeServiceUnitTest.class);


    private SparkSession sparkSession = ModelServeSparkConfig.sparkSession();

    @Autowired
    private RxModelServeService rxModelServeService;


    private ModelServeSparkWorker sparkWorker = new ModelServeSparkWorker();


    @Test
    public void testApplyTfToDataset() {
        List<Transformer> transformerList = new ArrayList<Transformer>(2);

        DeepImageFeaturizer featurizer = new DeepImageFeaturizer();
        featurizer.setModelName("InceptionV3");
        featurizer.setInputCol("image");
        featurizer.setOutputCol("features");


        transformerList.add(featurizer);
        transformerList.add(ModelServeTestUtils.getLrm());

        LOGGER.info("testing application of spark transformers");

        Dataset<Row> daisyDs = this.sparkSession.read().format("image").load("src/test/resources/singleDaisy");
        assert null != daisyDs;


        Dataset<Row> result = this.rxModelServeService.applyTf(transformerList, daisyDs).block();


        assert null != result;
        LOGGER.info("showing apply-tf result: ");
        result.show();
        // this cast to Row[] is required, although IntelliJ says otherwise
        Row[] rows = (Row[]) result.collect();
        Row row = rows[0];
        double prediction = row.getAs("prediction");

        LOGGER.info("prediction: {}", prediction);
        assert 1.0 == prediction;

        LOGGER.info("done testing  apply tf");
    }

}
