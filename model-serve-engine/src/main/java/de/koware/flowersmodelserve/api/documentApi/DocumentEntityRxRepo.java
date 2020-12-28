package de.koware.flowersmodelserve.api.documentApi;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface DocumentEntityRxRepo extends ReactiveMongoRepository<DocumentEntity, UUID> {


    public Flux<DocumentEntity> findAllByCorrelationKey(UUID correlationKey) ;
}
