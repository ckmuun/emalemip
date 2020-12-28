package de.koware.flowersmodelserve.api.sparkDs;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SparkDatasetRxRepo extends ReactiveMongoRepository<SparkDatasetEntity, UUID> {

public Mono<SparkDatasetEntity> findByCorrelationKey(UUID correlationKey);
}
