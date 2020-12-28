package de.koware.flowersmodelserve.api.pipelineApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
    Dto / Persistence Mapper for Procedual Goals, no methods for Iterables as the entity is a singleton entity
 */

@Service
public class PipelineGoalMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineGoalMapper.class);

    public PipelineGoalReturnDto entity2DtoProcedualGoal(PipelineGoalEntity entity) {
        LOGGER.info("mapping procedual goal entity to dto");
        return new PipelineGoalReturnDto(
                entity.getIdsOfModelsUsed(),
                entity.getDescription(),
                entity.getPipelineDisplayName(),
                entity.getId(),
                entity.getDocumentLabelsId()
        );
    }

    public PipelineGoalEntity dto2EntityProcedualGoal(PipelineGoalIncomingDto incomingDto) {
        LOGGER.info("mapping procedual goal DTO to ENTITY");

        return new PipelineGoalEntity(
                incomingDto.getModelIds(),
                incomingDto.getDescription(),
                incomingDto.getPipelineDisplayName(),
                incomingDto.getDocumentLabelsId()
        );
    }


}
