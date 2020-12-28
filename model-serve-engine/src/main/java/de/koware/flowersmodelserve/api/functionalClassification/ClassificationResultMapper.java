package de.koware.flowersmodelserve.api.functionalClassification;

import org.apache.spark.ml.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClassificationResultMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationResultMapper.class);


    public ClassificationResultEntity createClassificationResultEty(
            UUID correlationKey,
            UUID documentEtyId,
            UUID documentLabelId,
            Double numericLabel,
            UUID pGoalId,
            boolean successful,
            Vector features,
            String textLabel

    ) {
        LOGGER.info("creating new Classification Result");
        return new ClassificationResultEntity(
                correlationKey,
                documentLabelId,
                documentEtyId,
                numericLabel,
                pGoalId,
                successful,
                features,
                textLabel);
    }

    public ClassificationResultReturnDto mapCreEtyToReturnDto(ClassificationResultEntity cre) {
        LOGGER.info("mapping classification result entity to returnDto ");

        return new ClassificationResultReturnDto(
                cre.getDocumentLabelId(),
                cre.getDocumentEtyId(),
                cre.getCorrelationKey(),
                cre.getTextLabel(),
                cre.getNumericLabel(),
                cre.getpGoalId(),
                cre.isSuccessful(),
                cre.getFeatures()
        );
    }

    public ClassificationResultReturnDto createReturnDto(
            UUID documentLabelId,
            UUID documentId,
            UUID correlationKey,
            String textLabel,
            double numericLabel,
            UUID pGoalId,
            boolean successful,
            Vector features
    ) {
        return new ClassificationResultReturnDto(
                documentLabelId, documentId, correlationKey, textLabel, numericLabel, pGoalId, successful, features);
    }
}
