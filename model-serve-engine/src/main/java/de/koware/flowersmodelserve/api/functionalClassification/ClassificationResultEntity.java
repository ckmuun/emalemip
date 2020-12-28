package de.koware.flowersmodelserve.api.functionalClassification;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.koware.flowersmodelserve.api.correlation.Correlatable;
import org.apache.spark.ml.linalg.Vector;

import java.util.UUID;

public class ClassificationResultEntity implements Correlatable {

    private UUID documentLabelId;
    private UUID documentEtyId;
    private UUID correlationKey;
    private double numericLabel;
    private UUID pGoalId;
    private boolean successful;
    private Vector features;
    private String textLabel;


    @JsonCreator
    public ClassificationResultEntity() {

    }

    public ClassificationResultEntity(
            UUID correlationKey,
            UUID documentLabelId,
            UUID documentEtyId,
            double numericLabel,
            UUID pGoalId,
            boolean successful,
            Vector features,
            String textLabel) {
        this.correlationKey = correlationKey;
        this.documentLabelId = documentLabelId;
        this.documentEtyId = documentEtyId;
        this.numericLabel = numericLabel;
        this.successful = successful;
        this.features = features;
        this.pGoalId = pGoalId;
        this.textLabel = textLabel;
    }

    @Override
    public UUID getCorrelationKey() {
        return this.correlationKey;
    }


    public UUID getDocumentLabelId() {
        return documentLabelId;
    }

    public UUID getDocumentEtyId() {
        return documentEtyId;
    }


    public double getNumericLabel() {
        return numericLabel;
    }

    public UUID getpGoalId() {
        return pGoalId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Vector getFeatures() {
        return features;
    }

    public String getTextLabel() {
        return textLabel;
    }

    public void setDocumentLabelId(UUID documentLabelId) {
        this.documentLabelId = documentLabelId;
    }

    public void setDocumentEtyId(UUID documentEtyId) {
        this.documentEtyId = documentEtyId;
    }

    public void setCorrelationKey(UUID correlationKey) {
        this.correlationKey = correlationKey;
    }

    public void setNumericLabel(double numericLabel) {
        this.numericLabel = numericLabel;
    }

    public void setpGoalId(UUID pGoalId) {
        this.pGoalId = pGoalId;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public void setFeatures(Vector features) {
        this.features = features;
    }

    public void setTextLabel(String textLabel) {
        this.textLabel = textLabel;
    }
}
