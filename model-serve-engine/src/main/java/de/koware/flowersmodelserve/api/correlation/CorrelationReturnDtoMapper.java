package de.koware.flowersmodelserve.api.correlation;

import de.koware.flowersmodelserve.api.BaseReturnDto;
import de.koware.flowersmodelserve.api.documentApi.DocumentEntity;
import de.koware.flowersmodelserve.api.functionalClassification.ClassificationResultEntity;
import de.koware.flowersmodelserve.api.functionalClassification.ClassificationResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CorrelationReturnDtoMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationReturnDtoMapper.class);

    private final ClassificationResultMapper creMapper;

    @Autowired
    public CorrelationReturnDtoMapper(ClassificationResultMapper creMapper) {
        this.creMapper = creMapper;
    }

    public Flux<DocumentReturnDto> transformDocEtyToReturnDto(Flux<DocumentEntity> documentEntityFlux) {
        LOGGER.info("transforming document ety flux to DTO");
        return documentEntityFlux.map(documentEntity -> new DocumentReturnDto(
                documentEntity.getBytes(),
                documentEntity.getpGoalId(),
                documentEntity.getCorrelationKey()
        ));
    }
/*
    public Flux<ClassificationResultEntity> tfClassResultEtyToRetDto(Flux<ClassificationResultEntity> creFlux) {
        LOGGER.info("transforming classification");

        return creFlux.map(cre -> this.creMapper.createReturnDto(
                cre.getDocumentLabelId(),
                cre.getDocumentEtyId(),
                cre.getCorrelationKey(),
                cre.getTextLabel(),
                cre.getNumericLabel(),
                cre.getpGoalId(),
                cre.isSuccessful(),
                cre.getFeatures()
        ));
    }*/

}
