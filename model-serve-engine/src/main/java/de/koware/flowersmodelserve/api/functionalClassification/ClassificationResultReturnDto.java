package de.koware.flowersmodelserve.api.functionalClassification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import de.koware.flowersmodelserve.api.BaseReturnDto;
import org.apache.spark.ml.linalg.DenseVector;
import org.apache.spark.ml.linalg.Vector;

import java.util.UUID;

public class ClassificationResultReturnDto extends BaseReturnDto {

    private UUID documentLabelId;
    private UUID documentId;
    private UUID correlationKey;
    private String textLabel;
    private double numericLabel;
    private UUID pGoalId;
    private boolean successful;
    private double[] featuresVectorArr;


    @JsonCreator
    private ClassificationResultReturnDto() {
        super();
    }

    public ClassificationResultReturnDto(
            UUID documentLabelId,
            UUID documentId,
            UUID correlationKey,
            String textLabel,
            double numericLabel,
            UUID pGoalId,
            boolean successful,
            Vector features
    ) {
        super();
        this.documentLabelId = documentLabelId;
        this.documentId = documentId;
        this.correlationKey = correlationKey;
        this.textLabel = textLabel;
        this.numericLabel = numericLabel;
        this.pGoalId = pGoalId;
        this.successful = successful;
        this.featuresVectorArr = features.toArray();
    }


    @JsonSetter
    private void setDocumentLabelId(UUID documentLabelId) {
        this.documentLabelId = documentLabelId;
    }

    @JsonSetter
    private void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    @JsonSetter
    private void setCorrelationKey(UUID correlationKey) {
        this.correlationKey = correlationKey;
    }

    @JsonSetter
    private void setTextLabel(String textLabel) {
        this.textLabel = textLabel;
    }

    @JsonSetter
    private void setNumericLabel(double numericLabel) {
        this.numericLabel = numericLabel;
    }

    @JsonSetter
    private void setpGoalId(UUID pGoalId) {
        this.pGoalId = pGoalId;
    }

    @JsonSetter
    private void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @JsonSetter
    private void setFeaturesVectorArr(double[] featuresVectorArr) {
        this.featuresVectorArr = featuresVectorArr;
    }

    @JsonGetter
    public UUID getDocumentLabelId() {
        return documentLabelId;
    }

    @JsonGetter
    public String getTextLabel() {
        return textLabel;
    }

    @JsonGetter
    public double getNumericLabel() {
        return numericLabel;
    }

    @JsonGetter
    public boolean isSuccessful() {
        return successful;
    }

    @JsonIgnore
    public Vector getFeatures() {
        return new DenseVector(this.featuresVectorArr);
    }

    @JsonGetter
    public UUID getDocumentId() {
        return documentId;
    }

    @JsonGetter
    public UUID getCorrelationKey() {
        return correlationKey;
    }

    @JsonGetter
    public UUID getpGoalId() {
        return pGoalId;
    }

    @JsonGetter
    public double[] getFeaturesVectorArr() {
        return featuresVectorArr;
    }
}
