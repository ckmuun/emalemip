package de.koware.flowersmodelserve.api.documentLabelsApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DocumentLabelsMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentLabelsMapper.class);

    public DocumentLabelsReturnDto documentLabelsEntity2Dto(DocumentLabelsEntity entity) {
        LOGGER.debug("mapping documentLabelsEntity 2 Dto");

        return new DocumentLabelsReturnDto(
                entity.getDisplayName(),
                entity.getLabelName2NumericRep(),
                entity.getId()
        );
    }

    public DocumentLabelsEntity documentLabelsDto2Entity(DocumentLabelsIncomingDto incomingDto) {

        LOGGER.debug("mapping DocumentLabelsDTO 2 Entity");
        return new DocumentLabelsEntity(
                incomingDto.getStringLabel2NumericLabelMapping(),
                incomingDto.getLabelName()
        );
    }
}
