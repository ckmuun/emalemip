package de.koware.flowersmodelserve.api.controller;

import de.koware.flowersmodelserve.api.BaseReturnDto;
import de.koware.flowersmodelserve.api.documentApi.IncomingDocumentDto;
import de.koware.flowersmodelserve.api.functionalClassification.IncomingDocumentValidator;
import de.koware.flowersmodelserve.api.functionalClassification.NotProcessableErrorDto;
import de.koware.flowersmodelserve.api.functionalClassification.RxModelServeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/model-serve")
public class ModelServeController {

    /*
    TODO
           add request body / item evalutation side processors and return proper
           http return messages.
     */


    private static final Logger LOGGER = LoggerFactory.getLogger(ModelServeController.class);

    private final RxModelServeService rxModelServeService;
    private final IncomingDocumentValidator incomingDocumentValidator;

    @Autowired
    public ModelServeController(RxModelServeService rxModelServeService,
                                IncomingDocumentValidator incomingDocumentValidator) {
        this.rxModelServeService = rxModelServeService;
        this.incomingDocumentValidator = incomingDocumentValidator;
    }

    @GetMapping("/greeting")
    public String hello() {
        LOGGER.info("received GET Greeting request");
        return "hello";
    }

    /*
       Deprecated blocking implementation of document processing + classification.
       Used for reference in testing only.
     */
    @Deprecated
    @PostMapping("/classify-blocking")
    public Mono<? extends BaseReturnDto> processAndClassifyDocumentBlocking(@Valid @RequestBody IncomingDocumentDto iDto) {
        LOGGER.info("received request for reactive classification -> processing ");

        if (this.incomingDocumentValidator.isProcessable(iDto)) {
            LOGGER.info("is generally processable, beginning processing of document");
            return Mono.just(this.rxModelServeService.processIncomingDocumentBlocking(iDto));
        }
        LOGGER.info("returning Not Processable DTO");
        NotProcessableErrorDto nped = new NotProcessableErrorDto();
        return Mono.just(nped);
    }

    @PostMapping("/process-no-classification")
    public Mono<? extends BaseReturnDto> processDocumentNoClassification(@Valid @RequestBody IncomingDocumentDto iDto) {
        LOGGER.info("received request for doucment processing without classification");
        return this.rxModelServeService.processIncomingDocument(iDto);
    }


    @PostMapping("/classify-rx")
    public Mono<? extends BaseReturnDto> processAndClassifyDocumentRx(@Valid @RequestBody IncomingDocumentDto iDto) {
        LOGGER.info("received request for reactive classification -> processing ");

        if (this.incomingDocumentValidator.isProcessable(iDto)) {
            LOGGER.info("is generally processable, beginning processing of document");
            return this.rxModelServeService.processAndClassifyIncomingDocument(iDto);
        }
        LOGGER.info("returning Not Processable DTO");
        NotProcessableErrorDto nped = new NotProcessableErrorDto();
        return Mono.just(nped);
    }

}
