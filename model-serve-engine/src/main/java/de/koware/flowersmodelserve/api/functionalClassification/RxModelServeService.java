package de.koware.flowersmodelserve.api.functionalClassification;

import de.koware.flowersmodelserve.api.BaseReturnDto;
import de.koware.flowersmodelserve.api.SparkDatasetReturnDto;
import de.koware.flowersmodelserve.api.documentApi.DocumentEntityDao;
import de.koware.flowersmodelserve.api.documentApi.DocumentEntity;
import de.koware.flowersmodelserve.api.documentApi.DocumentMapper;
import de.koware.flowersmodelserve.api.documentApi.IncomingDocumentDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsDao;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsEntity;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalDao;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalEntity;
import de.koware.flowersmodelserve.api.sparkDs.SparkDatasetDao;
import de.koware.flowersmodelserve.api.sparkDs.SparkDatasetEntity;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelDao;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import modelServe.ModelServeSparkWorker;
import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.classification.ClassificationModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/*
    The Primary Service used for Document Processing and Classification Tasks.
    It draws together base functionality from various sources throughout the application.
    It has a bit of a God Class vibe to it, however due to the long mapping chains in the
    reactive implementation (e.g. joining data from different repositories) I went with one class.
 */

@Service
public class RxModelServeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RxModelServeService.class);

    private final SparkModelDao sparkModelDao;
    private final DocumentEntityDao documentEntityDao;
    private final DocumentMapper documentMapper;
    private final ClassificationResultDao classificationResultDao;
    private final PipelineGoalDao pipelineGoalDao;
    private final SparkModelSerializer sparkModelSerializer;
    private final ModelServeSparkWorker sparkWorker = new ModelServeSparkWorker();
    private final DocumentLabelsDao documentLabelsDao;
    private final ClassificationResultMapper classificationResultMapper;
    private final SparkDatasetDao sparkDatasetDao;


    @Autowired
    public RxModelServeService(SparkModelDao sparkModelDao,
                               DocumentEntityDao documentEntityDao,
                               DocumentMapper documentMapper,
                               ClassificationResultDao classificationResultDao,
                               PipelineGoalDao pipelineGoalDao,
                               SparkModelSerializer sparkModelSerializer,
                               DocumentLabelsDao documentLabelsDao,
                               ClassificationResultMapper classificationResultMapper,
                               SparkDatasetDao sparkDatasetDao
    ) {
        this.sparkModelDao = sparkModelDao;
        this.documentEntityDao = documentEntityDao;
        this.documentMapper = documentMapper;
        this.classificationResultDao = classificationResultDao;
        this.pipelineGoalDao = pipelineGoalDao;
        this.sparkModelSerializer = sparkModelSerializer;
        this.documentLabelsDao = documentLabelsDao;
        this.classificationResultMapper = classificationResultMapper;
        this.sparkDatasetDao = sparkDatasetDao;
    }

    /*
        The old blocking implementation for the model-apply functionality.
        Deprecated, but still present as reference functionality to compare with the
        reactive implementation.
        Reason: Blocking is more predictable and easier to debug.
     */

    @Deprecated
    public BaseReturnDto processIncomingDocumentBlocking(IncomingDocumentDto incomingDocumentDto) {
        DocumentEntity ety = this.documentEntityDao.save(documentMapper.incomingDoc2Ety(incomingDocumentDto)).block();
        assert null != ety;
        PipelineGoalEntity pGoal = this.pipelineGoalDao.findById(ety.getpGoalId()).block();
        assert null != pGoal;
        DocumentLabelsEntity dle = this.documentLabelsDao.findById(pGoal.getDocumentLabelsId()).block();
        assert null != dle;

        initDocProcessingBlk(ety, pGoal);
        ClassificationResultEntity cre = this.persistClassificationResult(
                initDocProcessingBlk(ety, pGoal),
                ety.getCorrelationKey(),
                pGoal.getId(),
                ety.getId(),
                dle.getId(),
                dle
        ).block();

        assert null != cre;
        return this.createDtoFromClassificationResult(dle, cre).block();
    }

    // same as above, blocking implementation for reference / testing purposes.
    // not intended for production.
    @Deprecated
    public BaseReturnDto processingIncomingDocumentBlocking(IncomingDocumentDto incomingDocumentDto) {
        DocumentEntity ety = this.documentEntityDao.save(documentMapper.incomingDoc2Ety(incomingDocumentDto)).block();
        assert null != ety;
        PipelineGoalEntity pGoal = this.pipelineGoalDao.findById(ety.getpGoalId()).block();
        assert null != pGoal;

        initDocProcessingBlk(ety, pGoal);
        Dataset<Row> processedDs = initDocProcessingBlk(ety, pGoal);
        processedDs.show();

        SparkDatasetEntity datasetEntity = this.sparkDatasetDao.saveSparkDsEntity(
                new SparkDatasetEntity(
                        processedDs,
                        ety.getCorrelationKey()
                )
        ).block();

        assert null != datasetEntity;
        return new SparkDatasetReturnDto(
                datasetEntity.getId(),
                datasetEntity.getSparkDsJson()
        );
    }

    // helper method for blocking testing/ reference impl
    @Deprecated
    private Dataset<Row> initDocProcessingBlk(DocumentEntity ety, PipelineGoalEntity pGoal) {
        Dataset<Row> rawDs = this.createDatasetFromInputDocument(ety).block();
        List<Transformer> transformers = this.getSparkModels(pGoal.getIdsOfModelsUsed())
                .transform(this::getModelFluxy)
                .collectList()
                .block();

        assert null != rawDs;
        assert null != transformers;
        Dataset<Row> processedDs = this.applyTf(transformers, rawDs).block();

        assert null != processedDs;

        return processedDs;
    }


    /*
        Main Entrypoint for processing of documents (e.g. applying transformers) without classifying them.
     */
    public Mono<? extends BaseReturnDto> processIncomingDocument(IncomingDocumentDto iDto) {

        LOGGER.debug("setting up caches for processing of document without classfication");
        @SuppressWarnings("unchecked") final Dataset<Row>[] datasets = new Dataset[2];
        final DocumentEntity[] documentEntityCache = new DocumentEntity[1];
        final PipelineGoalEntity[] pipelineGoalCache = new PipelineGoalEntity[1];

        // save the input
        return this.documentMapper.incomingDocDto2EntityRx(iDto)
                .flatMap(this.documentEntityDao::save)

                //create the helper objects
                .flatMap(documentEntity -> {
                    LOGGER.info("getting PGoal with id: " + documentEntity.getpGoalId());
                    documentEntityCache[0] = documentEntity;
                    return this.pipelineGoalDao.findById(documentEntity.getpGoalId());
                })
                .map(pipelineGoalEntity -> {
                    LOGGER.debug("setting pipeline goal cache to pGoal with id: {}", pipelineGoalEntity.getId());
                    pipelineGoalCache[0] = pipelineGoalEntity;
                    return pipelineGoalEntity;
                })
                // step 1 -> actually process something
                .flatMap(documentEntity -> {
                    LOGGER.info("creating dataset from doc ety with id {}", documentEntity);
                    return createDatasetFromInputDocument(documentEntityCache[0]);

                })
                .flatMapMany(dataset -> {
                    datasets[0] = dataset;
                    LOGGER.info("getting spark models with ids: " + Arrays.toString(pipelineGoalCache[0].getIdsOfModelsUsed()));
                    return getSparkModels(pipelineGoalCache[0].getIdsOfModelsUsed());
                })
                .transform(this::getModelFluxy)
                .collectList()
                .flatMap(tf -> {
                    LOGGER.info("applying tf...");
                    return applyTf(tf, datasets[0]);
                })
                .flatMap(processedDs -> this.sparkDatasetDao.saveSparkDsEntity(
                        new SparkDatasetEntity(
                                processedDs,
                                documentEntityCache[0].getCorrelationKey()
                        )
                ))
                .map(sparkDatasetEntity -> new SparkDatasetReturnDto(
                        sparkDatasetEntity.getId(),
                        sparkDatasetEntity.getSparkDsJson()
                ));
    }


    /*
        Main Entrypoint method for classification of Documents
     */
    public Mono<? extends BaseReturnDto> processAndClassifyIncomingDocument(IncomingDocumentDto iDto) {
        LOGGER.debug("setting up entity cache arrays");

        @SuppressWarnings("unchecked") final Dataset<Row>[] datasets = new Dataset[2];
        final DocumentEntity[] documentEntityCache = new DocumentEntity[1];
        final PipelineGoalEntity[] pipelineGoalCache = new PipelineGoalEntity[1];
        final ClassificationResultEntity[] resultEntityCache = new ClassificationResultEntity[1];
        final DocumentLabelsEntity[] labelCache = new DocumentLabelsEntity[1];

        /*
            Methods should be ten lines long at most. Well, not today.
         */
        // step 0 create or get all preparation objects and put them in array caches
        return this.documentMapper.incomingDocDto2EntityRx(iDto)
                // save the input
                .flatMap(this.documentEntityDao::save)

                //create the helper objects
                .flatMap(documentEntity -> {
                    LOGGER.info("getting PGoal with id: " + documentEntity.getpGoalId());
                    documentEntityCache[0] = documentEntity;
                    return this.pipelineGoalDao.findById(documentEntity.getpGoalId());
                })
                .map(pipelineGoalEntity -> {
                    LOGGER.debug("setting pipeline goal cache to pGoal with id: {}", pipelineGoalEntity.getId());
                    pipelineGoalCache[0] = pipelineGoalEntity;
                    return pipelineGoalEntity;
                })
                .flatMap(pipelineGoalEntity -> this.queryDocLabelsToGetStringLabel(pipelineGoalEntity.getDocumentLabelsId()))
                .map(documentLabelsEntity -> {
                    labelCache[0] = documentLabelsEntity;
                    LOGGER.info("setting docLabelCache to label with id : {}", labelCache[0]);
                    return documentEntityCache[0];
                })
                // step 1 -> actually process something
                .flatMap(documentEntity -> {
                    LOGGER.info("creating dataset from doc ety with id {}", documentEntity);
                    return createDatasetFromInputDocument(documentEntityCache[0]);

                })
                .flatMapMany(dataset -> {
                    datasets[0] = dataset;
                    LOGGER.info("getting spark models with ids: " + Arrays.toString(pipelineGoalCache[0].getIdsOfModelsUsed()));
                    return getSparkModels(pipelineGoalCache[0].getIdsOfModelsUsed());
                })
                .transform(this::getModelFluxy)
                .collectList()
                .flatMap(tf -> {
                    LOGGER.info("applying tf...");
                    return applyTf(tf, datasets[0]);
                })

                .flatMap(processedDataset -> {
                    datasets[1] = processedDataset;

                    return this.sparkDatasetDao.saveSparkDsEntity(
                            new SparkDatasetEntity(
                                    processedDataset,
                                    documentEntityCache[0].getCorrelationKey()
                            )
                    );
                })
                // create return dto here
                .flatMap(sparkDatasetEntity -> {
                            LOGGER.info("docEty cache: " + documentEntityCache[0].getCorrelationKey());
                            LOGGER.info("pGoal cache: " + pipelineGoalCache[0].getId());

                            return persistClassificationResult(
                                    datasets[1],
                                    documentEntityCache[0].getCorrelationKey(),
                                    pipelineGoalCache[0].getId(),
                                    documentEntityCache[0].getId(),
                                    pipelineGoalCache[0].getDocumentLabelsId(),
                                    labelCache[0]
                            );
                        }
                )
                .flatMap(resultEntity -> {
                    LOGGER.info("caching result entity");
                    resultEntityCache[0] = resultEntity;
                    LOGGER.info("querying doc label with id: {}", resultEntity.getDocumentLabelId());
                    return this.documentLabelsDao.findById(resultEntity.getDocumentLabelId());
                })
                .flatMap(documentLabelsEntity -> createDtoFromClassificationResult(
                        documentLabelsEntity,
                        resultEntityCache[0]
                ));
    }

    public Mono<DocumentLabelsEntity> queryDocLabelsToGetStringLabel(UUID documentLabelId) {
        LOGGER.info("querying document label");
        return this.documentLabelsDao.findById(documentLabelId);
    }


    public Mono<ClassificationResultReturnDto> createDtoFromClassificationResult(
            DocumentLabelsEntity documentLabelsEntity,
            ClassificationResultEntity resultEntity) {
        LOGGER.info("creating the classification result dto");
        return Mono.just(
                new ClassificationResultReturnDto(
                        resultEntity.getDocumentLabelId(),
                        resultEntity.getDocumentEtyId(),
                        resultEntity.getCorrelationKey(),
                        documentLabelsEntity.getNumericReps2StringLabels().get(resultEntity.getNumericLabel()),
                        resultEntity.getNumericLabel(),
                        resultEntity.getpGoalId(),
                        resultEntity.isSuccessful(),
                        resultEntity.getFeatures()
                )
        );
    }

    @SuppressWarnings({"rawtypes"})
    public Mono<Dataset<Row>> applyTf(List<Transformer> tfList, Dataset<Row> dataset) {
        LOGGER.info("Dataset before applying Transformer");
        dataset.show();

        final Dataset[] temp = new Dataset[tfList.size()];

        temp[0] = dataset;

        for (int i = 0; i < tfList.size(); i++) {
            Transformer tf = tfList.get(i);


            // TODO insert a bit more detailed logging on used models, however that's a low-prio feature.
            if (tf instanceof ClassificationModel<?, ?>) {
                LOGGER.info("classification model in list at position: " + i);
            }
            if (tf != null) {
                LOGGER.info("transformer in list at position: " + i);
            }


            temp[0] = tfList.get(i).transform(temp[0]);
            LOGGER.info("Dataset AFTER applying transformer");
            temp[0].show();
        }

        @SuppressWarnings("unchecked")
        Dataset<Row> result = temp[0];
        return Mono.just(result);
    }


    private Flux<Transformer> getModelFluxy(Flux<SparkModelEntity> etyFlux) {
        LOGGER.info("getting transformers from models");

        return etyFlux
                .flatMapSequential(this::rxDeserialize2Transformer);
    }

    private Mono<Transformer> rxDeserialize2Transformer(SparkModelEntity sparkModelEntity) {
        return Mono.just(sparkModelEntity)
                .map(ety -> {
                    LOGGER.info("Reative delegate to MOdel SerDes to get Spark Transformer");
                    try {
                        return this.sparkModelSerializer.deserialize2Transformer(ety);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        return Mono.error(e);
                    }
                })
                .cast(Transformer.class);
    }

    public Mono<ClassificationResultEntity> persistClassificationResult(
            Dataset<Row> processedDs,
            UUID correlationKey,
            UUID pGoalId,
            UUID documentEtyId,
            UUID documentLabelId,
            DocumentLabelsEntity dle
    ) {
        //  boolean successful = Arrays.stream(processedDs.columns())
        //          .anyMatch(column -> column.toLowerCase().equals("prediction"));

        LOGGER.info("doc label id for CRE: {}", documentLabelId);

        LOGGER.info("creating and persisting classifcation Result");
        LOGGER.info("showion dataset result is created from");
        processedDs.show();

        Row[] rows = (Row[]) processedDs.collect();
        Row row = rows[0];


        final Vector features = row.getAs("features");
        final Double numericLabel = row.getAs("prediction");
        final Vector rawPred = row.getAs("rawPrediction");

        final boolean successful = confidenceHighEnough(rawPred);

        String textLabel = dle.getNumericReps2StringLabels().get(numericLabel);

        LOGGER.info("querying document labels to interpret numeric classification results");
        return this.classificationResultDao.save(this.classificationResultMapper.createClassificationResultEty(
                correlationKey,
                documentEtyId,
                documentLabelId,
                numericLabel,
                pGoalId,
                successful,
                features,
                textLabel
        ));
    }


    // TODO this confidence calculation is relatively simple. Research some improvement here.
    private boolean confidenceHighEnough(Vector rawPrediction) {

        double[] rawPredictionValues = rawPrediction.toArray();
        double highest = rawPredictionValues[rawPrediction.argmax()];
        LOGGER.info("highest value in rawPrediction = {}", highest);

        for (double d : rawPredictionValues) {
            if (d == highest) {
                continue;
            }
            if (d <= 0) {
                continue;
            }
            if (highest / d <= 1.5) {
                LOGGER.info("confidence too low compared to value: {}", d);
                return false;
            }
        }
        LOGGER.info("confidence is sufficient");
        return true;
    }


    private Flux<SparkModelEntity> getSparkModels(UUID[] pGoalSparkModelIds) {
        return this.sparkModelDao.findAllByPgGoalIdCollection(Arrays.asList(pGoalSparkModelIds));
    }

    public Mono<Dataset<Row>> createDatasetFromInputDocument(DocumentEntity documentEntity) {
        LOGGER.info("creating raw ds");
        return Mono.just(this.sparkWorker.createDatasetByMimetype(documentEntity.getContainer()));
    }

}
