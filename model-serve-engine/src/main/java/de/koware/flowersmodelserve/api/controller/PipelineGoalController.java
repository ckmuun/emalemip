package de.koware.flowersmodelserve.api.controller;

import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalIncomingDto;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalReturnDto;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/pipeline-goal")
public class PipelineGoalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineGoalController.class);

    private final PipelineGoalService pipelineGoalService;

    @Autowired
    public PipelineGoalController(PipelineGoalService pipelineGoalService) {

        this.pipelineGoalService = pipelineGoalService;
    }

    // TODO add convenience Endpoint that allows posting of a complete pipeline (e.g. the spark model in an array)

    @PostMapping("")
    public Mono<PipelineGoalReturnDto> createProcedualGoal(@Valid @RequestBody PipelineGoalIncomingDto iDto) {
        LOGGER.info("received POST to create new pipeline goal");
        return this.pipelineGoalService.createNewProcedualGoal(iDto);
    }

    @GetMapping("")
    public Mono<PipelineGoalReturnDto> getProcedualGoal(@Valid @RequestParam UUID id) {
        LOGGER.info("received get request for Pipeline Goal with id: {}", id);
        return this.pipelineGoalService.findProcedualGoalByid(id);
    }

    @GetMapping("/all")
    public Flux<PipelineGoalReturnDto> getAllPipelineGoals() {
        LOGGER.info("received GET for all pGoals");
        return this.pipelineGoalService.findAllPGoals();
    }
}
