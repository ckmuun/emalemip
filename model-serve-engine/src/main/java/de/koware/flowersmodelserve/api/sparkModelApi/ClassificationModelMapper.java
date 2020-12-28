package de.koware.flowersmodelserve.api.sparkModelApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClassificationModelMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationModelMapper.class);

    public SparkModelReturnDto entity2Dto(SparkModelEntity entity) {
        LOGGER.info(" mapping spakr model enttiy to corresponding DTO");

        return new SparkModelReturnDto(
                entity.getSparkModelContainer(),
                entity.getId()
        );
    }

    public SparkModelEntity dto2entity(SparkModelIncomingDto dto) {
        LOGGER.info("mapping spark model incoming dto 2 entity ");

        return new SparkModelEntity(
                dto.getSparkModelContainer());
    }
}
