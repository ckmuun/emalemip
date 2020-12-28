package de.koware.flowersmodelserve.entityMappingTests;

import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelIncomingDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelReturnDto;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import de.koware.flowersmodelserve.api.sparkModelApi.ClassificationModelMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SparkModelMappingTest {

    @Autowired
    private ClassificationModelMapper classificationModelMapper;

    @BeforeAll
    public void setup() {

    }


    @Test
    public void testClassificationModelDto2Ety() throws IOException {

        SparkModelIncomingDto incomingDto = ModelServeTestUtils.getClassificationModelDtoWithDocLabelId(
                UUID.randomUUID()
        );


        SparkModelEntity ety = this.classificationModelMapper.dto2entity(incomingDto);


        assert Arrays.equals(incomingDto.getSparkModelContainer().getBytes(), ety.getSparkModelContainer().getBytes());
        assert incomingDto.getSparkModelContainer().isClassifier() == ety.getSparkModelContainer().isClassifier();
        assert incomingDto.getSparkModelContainer().getClassAsString().equals(ety.getSparkModelContainer().getClassAsString());
        assert incomingDto.getSparkModelContainer().getInputColumn().equals(ety.getSparkModelContainer().getInputColumn());
        assert incomingDto.getSparkModelContainer().getOutputColumn().equals(ety.getSparkModelContainer().getOutputColumn());
        assert incomingDto.getSparkModelContainer().getDescription().equals(ety.getSparkModelContainer().getDescription());

        assert incomingDto.getSparkModelContainer().getParameters().keySet().containsAll(ety.getParameters().keySet());
        assert incomingDto.getSparkModelContainer().getParameters().values().containsAll(ety.getParameters().values());
    }

    @Test
    public void testClassificationModelEty2Dto() throws IOException {
        SparkModelEntity ety = ModelServeTestUtils.getSimpleClassificationModelEty();

        assert ety != null;
        SparkModelReturnDto dto = this.classificationModelMapper.entity2Dto(ety);

        assert Arrays.equals(dto.getBytes(), ety.getBytes());
    }
}
