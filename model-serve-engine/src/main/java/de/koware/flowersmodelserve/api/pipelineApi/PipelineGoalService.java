package de.koware.flowersmodelserve.api.pipelineApi;

import de.koware.flowersmodelserve.event.KafkaEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class PipelineGoalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineGoalService.class);

    private final PipelineGoalDao pipelineGoalDao;
    private final PipelineGoalMapper pipelineGoalMapper;
    private final KafkaEventProducer kafkaEventProducer;

    @Autowired
    public PipelineGoalService(PipelineGoalDao pipelineGoalDao,
                               PipelineGoalMapper pipelineGoalMapper,
                               KafkaEventProducer kafkaEventProducer
    ) {
        this.pipelineGoalDao = pipelineGoalDao;
        this.pipelineGoalMapper = pipelineGoalMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    public Mono<PipelineGoalReturnDto> createNewProcedualGoal(PipelineGoalIncomingDto incomingDto) {
        LOGGER.info("creating new Procedual goal from dto");
        return this.pipelineGoalDao
                .save(this.pipelineGoalMapper.dto2EntityProcedualGoal(incomingDto))
                .doOnSuccess(ety -> this.kafkaEventProducer.publishObjectPersistence(ety, "new-procedual-goals"))
                .map(this.pipelineGoalMapper::entity2DtoProcedualGoal);
    }

    public Mono<PipelineGoalReturnDto> findProcedualGoalByid(UUID id) {
        return this.pipelineGoalDao
                .findById(id)
                .map(this.pipelineGoalMapper::entity2DtoProcedualGoal);
    }

    public Flux<PipelineGoalReturnDto> findAllPGoals() {
        return this.pipelineGoalDao
                .findAll()
                .map(this.pipelineGoalMapper::entity2DtoProcedualGoal);
    }
}

