package de.koware.flowersmodelserve.api.controller;

import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsIncomingDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsReturnDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/document-labels")
public class DocumentLabelsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentLabelsController.class);

    private final DocumentLabelsService documentLabelsService;

    @Autowired
    public DocumentLabelsController(DocumentLabelsService documentLabelsService) {
        this.documentLabelsService = documentLabelsService;
    }

    @PostMapping("")
    public Mono<DocumentLabelsReturnDto> postNewDocumentLabels(@Valid @RequestBody DocumentLabelsIncomingDto incomingDto) {
        LOGGER.info("received request to create new documentLabels");
        return documentLabelsService.createNewDocumentLabels(incomingDto);

    }
}
