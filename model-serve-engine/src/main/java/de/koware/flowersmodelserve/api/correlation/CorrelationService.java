package de.koware.flowersmodelserve.api.correlation;

import de.koware.flowersmodelserve.api.documentApi.DocumentEntityDao;
import de.koware.flowersmodelserve.api.functionalClassification.ClassificationResultDao;
import de.koware.flowersmodelserve.api.functionalClassification.ClassificationResultMapper;
import de.koware.flowersmodelserve.api.functionalClassification.ClassificationResultReturnDto;
import de.koware.flowersmodelserve.api.sparkDs.SparkDatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class CorrelationService {


    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationService.class);


    private final DocumentEntityDao documentEntityDao;
    private final CorrelationReturnDtoMapper correlationReturnDtoMapper;
    private final ClassificationResultDao classificationResultDao;
    private final ClassificationResultMapper creMapper;
    private final SparkDatasetService sparkDatasetService;


    @Autowired
    public CorrelationService(DocumentEntityDao documentEntityDao,
                              CorrelationReturnDtoMapper correlationReturnDtoMapper,
                              ClassificationResultDao classificationResultDao,
                              ClassificationResultMapper creMapper,
                              SparkDatasetService sparkDatasetService) {
        this.documentEntityDao = documentEntityDao;
        this.correlationReturnDtoMapper = correlationReturnDtoMapper;
        this.classificationResultDao = classificationResultDao;
        this.creMapper = creMapper;
        this.sparkDatasetService = sparkDatasetService;
    }

    public Mono<CorrelationReturnDto> correlateDocumentAndResult(UUID correlationKey) {
        LOGGER.info("creating correlation dto");

        DocumentReturnDto[] documentCache = new DocumentReturnDto[1];
        final ClassificationResultReturnDto[] creCache = new ClassificationResultReturnDto[1];


        return this.documentEntityDao.findAllByCorrelationKey(correlationKey)
                .transform(this.correlationReturnDtoMapper::transformDocEtyToReturnDto)
                .last()
                .map(documentReturnDto -> {
                    documentCache[0] = documentReturnDto;
                    return documentCache[0];
                })
                .flatMap(documentReturnDto -> {
                    return this.classificationResultDao.findAllByCorrelationKey(correlationKey)
                            .last()
                            .map(this.creMapper::mapCreEtyToReturnDto);
                })
                .map(creDto -> {
                    creCache[0] = creDto;
                    return creCache[0];
                })
                .flatMap(creDto -> this.sparkDatasetService.findDsByCorrelationKey(correlationKey)
                )
                .map(sparkDs ->
                        new CorrelationReturnDto(
                                documentCache[0],
                                creCache[0],
                                sparkDs
                        )
                );
    }
}
