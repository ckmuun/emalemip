package de.koware.flowersmodelserve.api.sparkDs;

import de.koware.flowersmodelserve.api.SparkDatasetReturnDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SparkDatasetMapper {

    private final Logger LOGGER = LoggerFactory.getLogger(SparkDatasetMapper.class);

    public SparkDatasetReturnDto convertSparkDsEtyToDto(SparkDatasetEntity sDsEty) {
        LOGGER.info("converting spark dataset entity to return dto");

        return new SparkDatasetReturnDto(
                sDsEty.getId(),
                sDsEty.getSparkDsJson()
        );
    }
}
