package de.koware.flowersmodelserve.api.controller;

import de.koware.flowersmodelserve.api.BaseReturnDto;
import de.koware.flowersmodelserve.api.correlation.CorrelationReturnDto;
import de.koware.flowersmodelserve.api.correlation.CorrelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/correlate")
public class DocumentCorrelationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentCorrelationController.class);

    private final CorrelationService correlationService;

    @Autowired
    public DocumentCorrelationController(CorrelationService correlationService) {
        this.correlationService = correlationService;
    }

    @GetMapping("/{correlationKey}")
    public Mono<CorrelationReturnDto> getAllByCorrelationKey(@PathVariable UUID correlationKey) {

        LOGGER.info("received correlation service request");
        return this.correlationService.correlateDocumentAndResult(correlationKey);
    }
}
