package de.koware.flowersmodelserve.api.sparkDs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class SparkDatasetDao {

    private final Logger LOGGER = LoggerFactory.getLogger(SparkDatasetDao.class);

    private final SparkDatasetRxRepo datasetRxRepo;

    public SparkDatasetDao(SparkDatasetRxRepo datasetRxRepo) {
        this.datasetRxRepo = datasetRxRepo;
    }

    public Mono<SparkDatasetEntity> saveSparkDsEntity(SparkDatasetEntity sparkDatasetEntity) {
        LOGGER.info("persisting new Spark Dataset entity");
        return this.datasetRxRepo.save(sparkDatasetEntity);
    }

    public Mono<SparkDatasetEntity> findDatasetById(UUID id) {
        LOGGER.info("retrieving spark dataset with id {}", id);

        return this.datasetRxRepo.findById(id);
    }

    public Mono<SparkDatasetEntity> findByCorrelationkey(UUID correlationKey) {
        LOGGER.info("finding dataset by correlation key : {}", correlationKey);
        return this.datasetRxRepo.findByCorrelationKey(correlationKey);
    }
}
