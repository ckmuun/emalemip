package de.koware.flowersmodelserve.api.documentApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DocumentEntityDao {

    private final DocumentEntityRxRepo documentEntityRxRepo;

    @Autowired
    public DocumentEntityDao(DocumentEntityRxRepo documentEntityRxRepo) {
        this.documentEntityRxRepo = documentEntityRxRepo;
    }

    public Mono<DocumentEntity> findById(UUID id) {
        return this.documentEntityRxRepo.findById(id);
    }

    public Mono<DocumentEntity> save(DocumentEntity documentEntity) {
        return this.documentEntityRxRepo.save(documentEntity);
    }

    public Flux<DocumentEntity> findAllByCorrelationKey(UUID correlationKey) {
        return this.documentEntityRxRepo.findAllByCorrelationKey(correlationKey);
    }
}
