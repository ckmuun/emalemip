package de.koware.flowersmodelserve.unit;

import customTestTransformer.BobTestTf;
import modelServe.ModelServeSparkConfig;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/*
    @author ckmuun
    a super simple test to verify the also super simple bob unary spark transformer
 */
public class TestSimpleBobCustomTf {


    private static final Logger LOGGER = LoggerFactory.getLogger(TestSimpleBobCustomTf.class);

    private SparkSession sparkSession = ModelServeSparkConfig.sparkSession();

    @Test
    public void testBobTf() {
        LOGGER.info("testing bob tf");

        ArrayList<String> list = new ArrayList<>();
        list.add("this is a test string");

        Dataset<String> ds = sparkSession.createDataset(list, Encoders.STRING());

        ds.show();

        BobTestTf bobTestTf = new BobTestTf("bob-test-transfomer");

        bobTestTf.setInputCol("value");
        bobTestTf.setOutputCol("bobcol");

        Dataset<Row> tfds = bobTestTf.transform(ds);

        tfds.show();

        assert null != tfds.select("bobcol");

    }
}
