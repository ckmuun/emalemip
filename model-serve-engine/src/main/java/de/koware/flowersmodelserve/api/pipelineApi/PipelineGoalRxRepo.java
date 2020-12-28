package de.koware.flowersmodelserve.api.pipelineApi;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PipelineGoalRxRepo extends ReactiveMongoRepository<PipelineGoalEntity, UUID> {
}
