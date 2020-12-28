package de.koware.flowersmodelserve;

import com.databricks.sparkdl.DeepImageFeaturizer;
import de.koware.flowersmodelserve.api.documentApi.IncomingDocumentDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsIncomingDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelIncomingDto;
import de.koware.flowersmodelserve.api.documentApi.DocumentContainer;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsEntity;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalIncomingDto;
import de.koware.flowersmodelserve.api.functionalClassification.SparkModelSerializer;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalEntity;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelContainer;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.DenseVector;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModelServeTestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelServeTestUtils.class);

    private static final SparkModelSerializer ser = new SparkModelSerializer();

    public static Vector collectFeatureVector(Dataset<Row> featurizedDs) {
        // this cast to Row[] is required, although IntelliJ says otherwise
        Row[] rows = (Row[]) featurizedDs.collect();
        Row row = rows[0];
        return row.getAs("features");
    }

    public static SparkModelIncomingDto createFeaturizerDto() throws IOException {
        LOGGER.info("creating deep image featurizer dto");

        HashMap<String, String> params = new HashMap<>();

        params.put("modelName", "InceptionV3");

        SparkModelContainer container = new SparkModelContainer(
                ser.serializeTransformer(new DeepImageFeaturizer()),
                DeepImageFeaturizer.class.toString(),
                "default-image-featurizer",
                "image",
                "features",
                params,
                false
        );

        return new SparkModelIncomingDto(
                container
        );
    }

    public static Vector getEmptyFeaturesVector() {
        return new DenseVector(new double[1]);
    }


    public static SparkModelEntity getSimpleClassificationModelEty() {
        try {
            return new SparkModelEntity(
                    ModelServeTestUtils.getLrmSparkModelContainer()
            );
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("io exception occured");
            return null;
        }
    }

    public static DocumentLabelsIncomingDto getDocumentLabelsIncomingDto() {
        HashMap<String, Double> mapping = new HashMap<String, Double>();

        mapping.put("tulip", 0.0);
        mapping.put("daisy", 1.0);
        mapping.put("rose", 2.0);
        mapping.put("sunflower", 3.0);
        mapping.put("dandelion", 4.0);

        return new DocumentLabelsIncomingDto(
                "a label name",
                mapping
        );
    }

    public static SparkModelIncomingDto getClassificationModelDto() throws IOException {
        return new SparkModelIncomingDto(
                getLrmSparkModelContainer()
        );
    }

    private static SparkModelContainer getLrmSparkModelContainer() throws IOException {

        return new SparkModelContainer(
                ser.simpleSerializationClaModel(getLrm()),
                LogisticRegressionModel.class.toString(),
                "flowers-log-classification",
                "features",
                "prediction",
                new HashMap<>(),
                true
        );
    }

    public static SparkModelIncomingDto getClassificationModelDtoWithDocLabelId(UUID doclabelId) throws IOException {
        return new SparkModelIncomingDto(
                getLrmSparkModelContainer()
        );
    }


    public static DocumentLabelsEntity getFLowersLabelsForTest() {
        String[] labelNames = {"tulip", "daisy", "rose", "sunflower", "dandelion"};
        Map<String, Double> stringLabel2NumericLabelMapping = new HashMap<>();

        stringLabel2NumericLabelMapping.put("tulip", 0.0);
        stringLabel2NumericLabelMapping.put("daiys", 1.0);
        stringLabel2NumericLabelMapping.put("rose", 2.0);
        stringLabel2NumericLabelMapping.put("sunflower", 3.0);
        stringLabel2NumericLabelMapping.put("dandelion", 4.0);

        return new DocumentLabelsEntity(
                stringLabel2NumericLabelMapping,
                "flowers-labels");
    }

    public static SparkModelEntity getDummyClassificationModelEty() throws IOException {
        return new SparkModelEntity(
                ModelServeTestUtils.getLrmSparkModelContainer());
    }


    public static PipelineGoalIncomingDto createProceudalGoalDtoNoModels() {

        return new PipelineGoalIncomingDto(
                "a sample description",
                "test procedual goal dto",
                new UUID[]{UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()},
                UUID.randomUUID()
        );
    }

    public static PipelineGoalEntity createProcedualGoalEntityNoModels() {

        String[] arr = new String[1];
        arr[0] = LogisticRegressionModel.class.getName();

        return new PipelineGoalEntity(
                new UUID[]{UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()},
                "test-entity",
                "a sample description",
                UUID.randomUUID());
    }

    public static IncomingDocumentDto createNotYetClassifiedDocDto(String label, UUID pGoalId) {
        label = label.toUpperCase();

        switch (label) {
            case "TULIP":
                return createDocImpl("src/test/resources/singleTulip/tulip.jpg", pGoalId);
            case "DAISY":
                return createDocImpl("src/test/resources/singleDaisy/daisy.jpg", pGoalId);
            case "ROSE":
                return createDocImpl("src/test/resources/singleRose/rose.jpg", pGoalId);
            case "SUNFLOWER":
                return createDocImpl("src/test/resources/singleSunflower/sunflower.jpg", pGoalId);
            case "DANDELION":
                return createDocImpl("src/test/resources/singleDandelion/dandelion.jpg", pGoalId);
            case "JUNK":
                return createDocImpl("src/test/resources/junkdoc/junk.jpg", pGoalId);
        }
        throw new IllegalArgumentException("invalid label for test docuemnt: " + label);
    }

    public static DocumentContainer createDocumentContainerFlower(String flowerName) throws IOException {
        switch (flowerName.toUpperCase()) {
            case "TULIP":
                return createDocumentContainter("src/test/resources/singleTulip/tulip.jpg");
            case "DAISY":
                return createDocumentContainter("src/test/resources/singleDaisy/daisy.jpg");
            case "ROSE":
                return createDocumentContainter("src/test/resources/singleRose/rose.jpg");
            case "SUNFLOWER":
                return createDocumentContainter("src/test/resources/singleSunflower/sunflower.jpg");
            case "DANDELION":
                return createDocumentContainter("src/test/resources/singleDandelion/dandelion.jpg");
            case "INVALID_JUNK_DOCUMENT":
                return createDocumentContainter("src/test/resources/junkdoc/junk.jpg");
        }
        throw new IllegalArgumentException("flower name: " + flowerName + " not found!");
    }

    public static DocumentContainer createPdfDocumentContainer() throws IOException {
        String filepath = "src/test/resources/jobBulletinPdfs/1605436304305-layout.pdf";
        return new DocumentContainer(
                StreamUtils.copyToByteArray(new FileInputStream(filepath)),
                filepath,
                "application/pdf"
        );
    }

    private static DocumentContainer createDocumentContainter(String filepath) throws IOException {
        return new DocumentContainer(
                StreamUtils.copyToByteArray(new FileInputStream(filepath)),
                getFlowerNameFromFilepath(filepath).concat(".jpeg"),
                "image/jpeg"
        );
    }

    private static IncomingDocumentDto createDocImplWithPgoal(UUID pGoalId, String filepath) throws IOException {
        return new IncomingDocumentDto(
                ModelServeTestUtils.createDocumentContainter(filepath),
                pGoalId
        );
    }

    public static IncomingDocumentDto createNycDtoHardToDetectDandelion(UUID pGoalId) {
        return createDocImpl("src/test/resources/singleDandelion/hard-to-detect-dandelion.jpg", pGoalId);
    }


    private static IncomingDocumentDto createDocImpl(String filepath, UUID pGoalId) {
        try {
            FileInputStream fis = new FileInputStream(filepath);

            if (null == pGoalId) {
                pGoalId = UUID.randomUUID();
            }

            DocumentContainer container = new DocumentContainer(
                    StreamUtils.copyToByteArray(fis),
                    getFlowerNameFromFilepath(filepath),
                    "image/jpeg"
            );

            return new IncomingDocumentDto(
                    container,
                    pGoalId
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static LogisticRegressionModel getLrm() {
        LOGGER.info("loading logistic regression model");
        LogisticRegressionModel lrm = LogisticRegressionModel.load("src/main/resources/cfModel");
        assert null != lrm;
        return lrm;
    }

    private static String getFlowerNameFromFilepath(String filepath) {
        String[] splits = filepath.split("/", 15);
        return splits[splits.length - 1];
    }

    public static IncomingDocumentDto createNycDtoJunk(UUID id) {

        return ModelServeTestUtils.createNotYetClassifiedDocDto("JUNK", id);
    }
}
