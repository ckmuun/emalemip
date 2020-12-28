package de.koware.flowersmodelserve.api.sparkModelApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class SparkModelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkModelService.class);

    private final SparkModelDao sparkModelDao;
    private final ClassificationModelMapper classificationModelMapper;


    @Autowired
    public SparkModelService(SparkModelDao sparkModelDao,
                             ClassificationModelMapper classificationModelMapper) {
        this.sparkModelDao = sparkModelDao;
        this.classificationModelMapper = classificationModelMapper;
    }

    public Mono<SparkModelReturnDto> createNewClassificationModel(SparkModelIncomingDto dto) {
        LOGGER.info("received new Classification Model DTO to persist");
        return this.sparkModelDao
                .save(
                        this.classificationModelMapper.dto2entity(dto)
                )
                .log("mapping the saved entity back to a dto")
                .map(this.classificationModelMapper::entity2Dto);
    }

    public Mono<SparkModelReturnDto> findByModelId(UUID modelId) {
        return this.sparkModelDao.findById(modelId)
                .map(this.classificationModelMapper::entity2Dto);
    }

    public Flux<SparkModelReturnDto> findAll() {
        return this.sparkModelDao
                .findAll()
                .map(this.classificationModelMapper::entity2Dto);
    }
}
