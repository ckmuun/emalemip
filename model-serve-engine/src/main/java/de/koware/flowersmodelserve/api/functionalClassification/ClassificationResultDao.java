package de.koware.flowersmodelserve.api.functionalClassification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ClassificationResultDao {


    private final ClassificationResultRxRepo resultRxRepo;

    @Autowired
    public ClassificationResultDao(ClassificationResultRxRepo resultRxRepo) {
        this.resultRxRepo = resultRxRepo;
    }

    public Mono<ClassificationResultEntity> save(ClassificationResultEntity resultEntity) {
        return this.resultRxRepo.save(resultEntity);
    }

    public Flux<ClassificationResultEntity> findAllByCorrelationKey(UUID correlationKey) {
        return this.resultRxRepo.findAllByCorrelationKey(correlationKey);
    }

}
