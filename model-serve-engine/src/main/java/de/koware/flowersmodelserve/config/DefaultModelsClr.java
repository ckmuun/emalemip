package de.koware.flowersmodelserve.config;


import com.databricks.sparkdl.DeepImageFeaturizer;
import customTransformers.PdfBoxTextractorTf;
import customTransformers.TessOcrTf;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsIncomingDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsService;
import de.koware.flowersmodelserve.api.functionalClassification.SparkModelSerializer;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalIncomingDto;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalService;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelContainer;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelIncomingDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelService;
import nlp.LanguageDetector;
import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.feature.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/*
    This command line runner provides a set of hard-coded and predefined Spark Transfomrmers
    and puts them into two pipelines. These include the super-simple flower classification pipeline
    and a rather basic pipe to do some nlp-related preprcessing of pdfs.
    They showcase the two general types of pipelines:
        1. Classification pipes with a specific result label at the end
        2. Processing pipelines that return a spark dataset
 */

@Component
public class DefaultModelsClr implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultModelsClr.class);


    private final SparkModelService sparkModelService;
    private final PipelineGoalService pipelineGoalService;
    private final SparkModelSerializer sparkModelSerializer;
    private final DocumentLabelsService documentLabelsService;


    @Autowired
    public DefaultModelsClr(SparkModelService sparkModelService,
                            PipelineGoalService pipelineGoalService,
                            SparkModelSerializer sparkModelSerializer,
                            DocumentLabelsService documentLabelsService) {
        this.sparkModelService = sparkModelService;
        this.pipelineGoalService = pipelineGoalService;
        this.sparkModelSerializer = sparkModelSerializer;
        this.documentLabelsService = documentLabelsService;
    }

    @Override
    public void run(String... args) throws Exception {

        LOGGER.info("creating default models within the system ");

        new InitialTfsRunnable(
                this.sparkModelService,
                this.pipelineGoalService,
                this.sparkModelSerializer,
                this.documentLabelsService
        ).run();
    }


    /*
           Blocking implementation of probing the datastore for currently availabĺe
           spark models and pipelineGoals.
           If the returned sizes / amounts are zero, it adds a hardcoded set of transformers.
     */
    static class InitialTfsRunnable implements Runnable {

        private final SparkModelService sparkModelService1;
        private final PipelineGoalService pipelineGoalService1;
        private final SparkModelSerializer sparkModelSerializer;
        private final DocumentLabelsService documentLabelsService;

        public InitialTfsRunnable(SparkModelService sparkModelService1,
                                  PipelineGoalService pipelineGoalService1,
                                  SparkModelSerializer sparkModelSerializer,
                                  DocumentLabelsService documentLabelsService

        ) {
            this.sparkModelService1 = sparkModelService1;
            this.pipelineGoalService1 = pipelineGoalService1;
            this.sparkModelSerializer = sparkModelSerializer;
            this.documentLabelsService = documentLabelsService;
        }

        @Override
        public void run() {

            LOGGER.info("checking number of currently deployed pipes and models");

            Long numberPGoals = this.pipelineGoalService1.findAllPGoals().count().block();
            Long numberSparkModels = this.sparkModelService1.findAll().count().block();


            if (numberPGoals == 0 && numberSparkModels == 0) {
                LOGGER.info("no models and pipes currently deployed -> adding default ones");
                // create custom transformers, which are just code and not trained models
                PdfBoxTextractorTf pdfBoxTextractorTf = new PdfBoxTextractorTf("pdfbox-textractor");
                TessOcrTf tessOcrTf = new TessOcrTf("tesseract-ocr-textractor");
                Tokenizer tokenizer = new Tokenizer();
                LanguageDetector languageDetector = new LanguageDetector("simple-language-datector");
                DeepImageFeaturizer deepImageFeaturizer = new DeepImageFeaturizer();
                deepImageFeaturizer.setModelName("InceptionV3");


                // load trained models from disk
                LogisticRegressionModel flowersLrm = getFlowersLrm();

                UUID flowerLrmId = persistTf(
                        flowersLrm,
                        "logistic regression model to classify 5 different flower images",
                        "features",
                        "prediction"
                );
                UUID tessTextractorId = persistTf(
                        tessOcrTf,
                        "tesseract-based ocr transformer, extracts text",
                        "data",
                        "tess-ocr-text"
                );
                UUID pdfboxTextractorId = persistTf(
                        pdfBoxTextractorTf,
                        "pdfbox-based transformer, extracts text via pdf library",
                        "data",
                        "pdfbox-text"
                );
                UUID tessOcrtokenizerId = persistTf(
                        tokenizer,
                        "simple whitespace-based tokenizer, -> consumes tess ocr text",
                        "tess-ocr-text",
                        "tess-ocr-tokens"
                );
                UUID pdftextTokenizerId = persistTf(
                        tokenizer,
                        "simple whitespace-based tokenizer -> consumes text generated by pdfbox",
                        "pdfbox-text",
                        "pdfbox-tokens"
                );
                UUID languageDetectorId = persistTf(
                        languageDetector,
                        "a simple language detector",
                        "text",
                        "language"
                );
                UUID deepImageFeatId = persistTf(
                        deepImageFeaturizer,
                        "featurizer for images",
                        "image",
                        "features"
                );

                // create document labels
                HashMap<String, Double> mapping = new HashMap<String, Double>();

                mapping.put("tulip", 0.0);
                mapping.put("daisy", 1.0);
                mapping.put("rose", 2.0);
                mapping.put("sunflower", 3.0);
                mapping.put("dandelion", 4.0);


                DocumentLabelsIncomingDto doclabelsDto = new DocumentLabelsIncomingDto(
                        "flower-labels",
                        mapping
                );

                UUID doclabelId = this.documentLabelsService.createNewDocumentLabels(doclabelsDto).block().getId();

                // create pipelines
                PipelineGoalIncomingDto flowerPipe = new PipelineGoalIncomingDto(
                        "pipeline to classify five types of flowers",
                        "flower-classfification",
                        new UUID[]{deepImageFeatId, flowerLrmId},
                        doclabelId
                );

                PipelineGoalIncomingDto pdfPreparationPipe = new PipelineGoalIncomingDto(
                        "pipeline to do basic nlp preprocessing from a pdf",
                        "pdf-prep-pipe",
                        new UUID[]{tessTextractorId, pdfboxTextractorId, pdftextTokenizerId, tessOcrtokenizerId},
                        UUID.randomUUID()
                );

                this.pipelineGoalService1.createNewProcedualGoal(flowerPipe).block();
                this.pipelineGoalService1.createNewProcedualGoal(pdfPreparationPipe).block();

            }


            return;
        }

        private UUID persistTf(Transformer transformer, String description, String inputCol, String outputCol) {
            // persist all of it
            try {

                SparkModelContainer flowerLrmContainer = new SparkModelContainer(
                        this.sparkModelSerializer.serializeTransformer(transformer),
                        transformer.getClass().toString(),
                        description,
                        inputCol,
                        outputCol,
                        new HashMap<>(),
                        true
                );

                final SparkModelIncomingDto modelIncomingDto = new SparkModelIncomingDto(
                        flowerLrmContainer
                );
                return this.sparkModelService1
                        .createNewClassificationModel(modelIncomingDto)
                        .block()
                        .getId();


            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new IllegalStateException("Exception while inserting standard models -- check DB connection");
        }
    }

    private static LogisticRegressionModel getFlowersLrm() {
        LOGGER.info("loading logistic regression model");
        //LogisticRegressionModel lrm = LogisticRegressionModel.load("/home/cornelius/SoftwareProjects/fichte/fichte-model-serve/src/main/resources/cfModel");
        LogisticRegressionModel lrm = LogisticRegressionModel.load("src/main/resources/cfModel");
        assert null != lrm;
        return lrm;
    }
}
