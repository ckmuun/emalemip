package de.koware.flowersmodelserve.api.sparkDs;

import de.koware.flowersmodelserve.api.SparkDatasetReturnDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class SparkDatasetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkDatasetService.class);


    private final SparkDatasetDao sparkDatasetDao;
    private final SparkDatasetMapper sparkDatasetMapper;


    public SparkDatasetService(SparkDatasetDao sparkDatasetDao,
                               SparkDatasetMapper sparkDatasetMapper
    ) {
        this.sparkDatasetDao = sparkDatasetDao;
        this.sparkDatasetMapper = sparkDatasetMapper;
    }

    public Mono<SparkDatasetReturnDto> findDatasetById(UUID id) {
        LOGGER.info("finding spark ds by Id: {}", id);
        return this.sparkDatasetDao.findDatasetById(id)
                .map(this.sparkDatasetMapper::convertSparkDsEtyToDto);
    }

    public Mono<SparkDatasetReturnDto> findDsByCorrelationKey(UUID correlationKey) {
        return this.sparkDatasetDao.findByCorrelationkey(correlationKey)
                .map(this.sparkDatasetMapper::convertSparkDsEtyToDto);
    }

}
