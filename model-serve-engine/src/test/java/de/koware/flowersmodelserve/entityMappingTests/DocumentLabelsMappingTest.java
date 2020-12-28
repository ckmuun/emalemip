package de.koware.flowersmodelserve.entityMappingTests;

import de.koware.flowersmodelserve.ModelServeTestUtils;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsIncomingDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsReturnDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsEntity;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DocumentLabelsMappingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentLabelsMappingTest.class);

    @Autowired
    private DocumentLabelsMapper documentLabelsMapper;

    @Test
    public void testEty2Dto() {
        LOGGER.info("test entity 2 dto for document labels");

        DocumentLabelsEntity entity = ModelServeTestUtils.getFLowersLabelsForTest();

        DocumentLabelsReturnDto dto = this.documentLabelsMapper.documentLabelsEntity2Dto(entity);

        assert dto.getLabelsAsString().equals(entity.getLabelNames());
        assert dto.getStringLabel2NumericLabelMapping().equals(entity.getLabelName2NumericRep());

        assert dto.getId() == entity.getId();
    }

    @Test
    public void testDto2Ety() {
        LOGGER.info("test dto 2 entity for document labels");

        DocumentLabelsIncomingDto dto = ModelServeTestUtils.getDocumentLabelsIncomingDto();

        DocumentLabelsEntity entity = this.documentLabelsMapper.documentLabelsDto2Entity(dto);

        assert dto.getStringLabel2NumericLabelMapping().keySet().equals(entity.getLabelNames());
        assert dto.getStringLabel2NumericLabelMapping().equals(entity.getLabelName2NumericRep());
    }
}
