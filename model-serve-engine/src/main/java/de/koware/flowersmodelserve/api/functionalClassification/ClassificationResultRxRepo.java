package de.koware.flowersmodelserve.api.functionalClassification;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ClassificationResultRxRepo extends ReactiveMongoRepository<ClassificationResultEntity, UUID> {

    public Flux<ClassificationResultEntity> findAllByCorrelationKey(UUID correlationKey);

}
