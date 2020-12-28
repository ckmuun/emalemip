package de.koware.flowersmodelserve.api.sparkModelApi;

import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface SparkModelRxRepo extends ReactiveMongoRepository<SparkModelEntity, UUID> {


    public Flux<SparkModelEntity> findSparkModelEntitiesByIdIsIn(Collection<UUID> uuids);


}
