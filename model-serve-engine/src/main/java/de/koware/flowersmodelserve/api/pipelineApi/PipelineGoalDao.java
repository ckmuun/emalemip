package de.koware.flowersmodelserve.api.pipelineApi;

import org.apache.spark.ml.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class PipelineGoalDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineGoalDao.class);

    private final PipelineGoalRxRepo pipelineGoalRxRepo;


    @Autowired
    public PipelineGoalDao(PipelineGoalRxRepo pipelineGoalRxRepo) {
        this.pipelineGoalRxRepo = pipelineGoalRxRepo;
    }

    public Mono<PipelineGoalEntity> save(PipelineGoalEntity entity) {
        return this.pipelineGoalRxRepo.save(entity);
    }

    public Mono<PipelineGoalEntity> findById(UUID id) {
        return this.pipelineGoalRxRepo.findById(id);
    }


    public Flux<PipelineGoalEntity> findAll() {
        return this.pipelineGoalRxRepo.findAll();
    }

}
