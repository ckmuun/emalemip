package de.koware.flowersmodelserve.api.documentApi;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DocumentMapper {

    public Mono<DocumentEntity> incomingDocDto2EntityRx(IncomingDocumentDto iDto) {

        return Mono.just(
                new DocumentEntity(
                        iDto.getDocumentContainer(),
                        iDto.getpGoalId())
        );
    }

    public DocumentEntity incomingDoc2Ety(IncomingDocumentDto incomingDocumentDto) {
        return new DocumentEntity(
                incomingDocumentDto.getDocumentContainer(),
                incomingDocumentDto.getpGoalId());
    }
}
