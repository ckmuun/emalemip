package de.koware.flowersmodelserve.api.documentLabelsApi;

import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentLabelRxRepo extends ReactiveMongoRepository<DocumentLabelsEntity, UUID> {
}
