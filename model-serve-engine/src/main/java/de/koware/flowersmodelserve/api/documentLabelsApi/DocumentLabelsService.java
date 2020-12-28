package de.koware.flowersmodelserve.api.documentLabelsApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DocumentLabelsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentLabelsService.class);


    private final DocumentLabelsDao documentLabelsDao;
    private final DocumentLabelsMapper documentLabelsMapper;


    @Autowired
    public DocumentLabelsService(DocumentLabelsDao documentLabelsDao, DocumentLabelsMapper documentLabelsMapper) {
        this.documentLabelsDao = documentLabelsDao;
        this.documentLabelsMapper = documentLabelsMapper;
    }

    public Mono<DocumentLabelsReturnDto> createNewDocumentLabels(DocumentLabelsIncomingDto incomingDto) {
        LOGGER.info("craeting new Document Labels from DTO");
        return this.documentLabelsDao.createNewDocumentLabels(
                this.documentLabelsMapper.documentLabelsDto2Entity(incomingDto))
                .map(this.documentLabelsMapper::documentLabelsEntity2Dto);
    }

    public Mono<DocumentLabelsReturnDto> findById(UUID id) {
        LOGGER.info("finding DocumentLabels with id : {}", id);
        return this.documentLabelsDao.findById(id)
                .map(this.documentLabelsMapper::documentLabelsEntity2Dto);
    }

}
