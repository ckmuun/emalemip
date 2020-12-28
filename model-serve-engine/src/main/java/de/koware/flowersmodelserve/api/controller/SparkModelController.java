package de.koware.flowersmodelserve.api.controller;

import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelIncomingDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelReturnDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("classification-model")
public class SparkModelController {


    private static final Logger LOGGER = LoggerFactory.getLogger(SparkModelController.class);


    private final SparkModelService sparkModelService;

    @Autowired
    public SparkModelController(SparkModelService sparkModelService) {
        this.sparkModelService = sparkModelService;
    }

    @PostMapping("")
    public Mono<SparkModelReturnDto> createNewClassificationModel(
            @Valid @RequestBody
                    SparkModelIncomingDto incomingDto) {

        LOGGER.info("received new Classification Model at Controller to persist");
        return this.sparkModelService.createNewClassificationModel(incomingDto);
    }

    @GetMapping("/{modelId}")
    public Mono<SparkModelReturnDto> findById(@PathVariable UUID modelId) {
        LOGGER.info("Received Request for Model with id {}", modelId.toString());
        return this.sparkModelService.findByModelId(modelId);
    }

    @GetMapping("/all")
    public Flux<SparkModelReturnDto> getAll() {
        LOGGER.info("received GET for all currently available Spark Models");

        return this.sparkModelService
                .findAll();
    }


}
