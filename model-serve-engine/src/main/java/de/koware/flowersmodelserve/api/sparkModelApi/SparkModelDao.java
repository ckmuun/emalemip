package de.koware.flowersmodelserve.api.sparkModelApi;

import de.koware.flowersmodelserve.api.functionalClassification.PGoalBasedSparkEtyComparator;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelRxRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

@Service
public class SparkModelDao {

    private final SparkModelRxRepo modelRxRepo;

    @Autowired
    public SparkModelDao(SparkModelRxRepo modelRxRepo) {
        this.modelRxRepo = modelRxRepo;
    }


    public Mono<SparkModelEntity> save(SparkModelEntity sparkModelEntity) {
        return this.modelRxRepo.save(sparkModelEntity);
    }

    public Mono<SparkModelEntity> findById(UUID id) {
        return this.modelRxRepo.findById(id);
    }

    public Flux<SparkModelEntity> findAllByPgGoalIdCollection(Collection<UUID> uuidsFromPGoal) {


        PGoalBasedSparkEtyComparator<SparkModelEntity> comparator = new PGoalBasedSparkEtyComparator<SparkModelEntity>();
        comparator.setOrder(uuidsFromPGoal.toArray(new UUID[0]));

        return this.modelRxRepo
                .findSparkModelEntitiesByIdIsIn(uuidsFromPGoal)
                .sort(comparator);
    }

    public Flux<SparkModelEntity> findAll() {
        return this.modelRxRepo.findAll();
    }

}
