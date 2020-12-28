package de.koware.flowersmodelserve.api.documentLabelsApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DocumentLabelsDao {


    private final DocumentLabelRxRepo rxRepo;

    @Autowired
    public DocumentLabelsDao(DocumentLabelRxRepo rxRepo) {
        this.rxRepo = rxRepo;
    }

    public Mono<DocumentLabelsEntity> createNewDocumentLabels(DocumentLabelsEntity documentLabelsEntity) {

        return this.rxRepo.save(documentLabelsEntity);
    }

    public Mono<DocumentLabelsEntity> findById(UUID id ) {
        return this.rxRepo.findById(id);
    }
}
