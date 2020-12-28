package de.koware.flowersmodelserve.api.functionalClassification;

import com.databricks.sparkdl.DeepImageFeaturizer;
import customTransformers.TessOcrTf;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelContainer;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.classification.ClassificationModel;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.*;


@Service
public class SparkModelSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkModelSerializer.class);

    /*
        currently we actively differentiate between the serDes methods for Classificaiton Models
        and Transformers, as it is not clear whether that's actually required.
     */

    public byte[] serializeTransformer(Transformer tf) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(baos);

        oos.writeObject(tf);
        return baos.toByteArray();
    }


    public byte[] simpleSerializationClaModel(ClassificationModel<?, ?> model) throws IOException {

        return serializeTransformer(model);

        /*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(baos);
        oos.writeObject(model);
        return baos.toByteArray();


         */
    }


    public Transformer deserialize2Transformer(SparkModelEntity modelEntity) throws IOException, ClassNotFoundException {
        LOGGER.info("deserialize to Spark Transformer");
        ByteArrayInputStream bais = new ByteArrayInputStream(modelEntity.getBytes());

        ObjectInputStream ois = new ObjectInputStream(bais);
        Transformer tf = (Transformer) ois.readObject();

        SparkModelContainer sparkModelContainer = modelEntity.getSparkModelContainer();

        LOGGER.info("input col: {}", sparkModelContainer.getInputColumn());
        LOGGER.info("output col: {}", sparkModelContainer.getOutputColumn());

        if (!sparkModelContainer.isClassifier()) {
            sparkModelContainer.getParameters().forEach(tf::set);
        }

        if (tf instanceof DeepImageFeaturizer) {
            LOGGER.info("Deep Image Featurizer");
            tf.set("inputCol", sparkModelContainer.getInputColumn());
            tf.set("outputCol", sparkModelContainer.getOutputColumn());
            return tf;
        }

        if (tf instanceof LogisticRegressionModel) {
            LOGGER.info("Logistic Regression Model");
            return tf;
        }


        return tf;

    }

    @SuppressWarnings("unchecked")
    public ClassificationModel<Vector, ?> simpleDeserializationClaModel(SparkModelEntity modelEntity) throws IOException, ClassNotFoundException {

        LOGGER.info("deserializing to Classification Model");
        ByteArrayInputStream bais = new ByteArrayInputStream(modelEntity.getBytes());

        ObjectInputStream ois = new ObjectInputStream(bais);


        return (ClassificationModel<Vector, ?>) ois.readObject();


    }

}
