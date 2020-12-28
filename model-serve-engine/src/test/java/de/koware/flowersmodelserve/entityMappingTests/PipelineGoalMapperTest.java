package de.koware.flowersmodelserve.entityMappingTests;

import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalIncomingDto;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalReturnDto;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalMapper;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PipelineGoalMapperTest {

    @Autowired
    private PipelineGoalMapper pipelineGoalMapper;

    @Test
    public void testDto2Entity() {
        PipelineGoalIncomingDto dto = ModelServeTestUtils.createProceudalGoalDtoNoModels();

        PipelineGoalEntity entity = this.pipelineGoalMapper.dto2EntityProcedualGoal(dto);

        assert entity.getDescription().equals(dto.getDescription());
        assert entity.getPipelineDisplayName().equals(dto.getPipelineDisplayName());
        assert Arrays.equals(entity.getIdsOfModelsUsed(), dto.getModelIds());

    }

    @Test
    public void testEntity2Dto() {
        PipelineGoalEntity entity = ModelServeTestUtils.createProcedualGoalEntityNoModels();

        PipelineGoalReturnDto dto = this.pipelineGoalMapper.entity2DtoProcedualGoal(entity);
        assert entity.getDescription().equals(dto.getDescription());
        assert entity.getPipelineDisplayName().equals(dto.getPipelineDisplayName());
        assert Arrays.equals(entity.getIdsOfModelsUsed(), dto.getIdsOfModelsUsed());
    }

}
