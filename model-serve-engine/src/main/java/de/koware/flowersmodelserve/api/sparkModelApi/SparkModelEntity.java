package de.koware.flowersmodelserve.api.sparkModelApi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import de.koware.flowersmodelserve.api.BaseEntity;
import de.koware.flowersmodelserve.api.SerializedValueContainer;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
public class SparkModelEntity extends BaseEntity implements SerializedValueContainer {

    // private final ClassificationModel<Vector, ? extends ClassificationModel<?,?>> classificationModel;
    //private final ClassificationModel<> classificationModel;



    private String classAsString;
    private byte[] serializedModel;
    private String description;
    private String inputColumn;
    private String outputColumn;
    private boolean isClassifier;
    private boolean isFeaturizer;

    private Map<String, String> parameters;

    @JsonCreator
    private SparkModelEntity() {

    }

    public SparkModelEntity(
            SparkModelContainer sparkModelContainer) {

        this.classAsString = sparkModelContainer.getClassAsString();
        this.serializedModel = sparkModelContainer.getSerializedModel();
        this.description = sparkModelContainer.getDescription();
        this.inputColumn = sparkModelContainer.getInputColumn();
        this.outputColumn = sparkModelContainer.getOutputColumn();
        this.isClassifier = sparkModelContainer.isClassifier();
        this.isFeaturizer = sparkModelContainer.isFeaturizer();
        this.parameters = sparkModelContainer.getParameters();
    }


    @Override
    public byte[] getBytes() {
        return this.serializedModel;
    }

    @JsonIgnore
    public SparkModelContainer getSparkModelContainer() {
        return new SparkModelContainer(
                this.serializedModel,
                this.classAsString,
                this.description,
                this.inputColumn,
                this.outputColumn,
                this.parameters,
                this.isClassifier
        );
    }

    @JsonGetter
    private String getClassAsString() {
        return classAsString;
    }

    @JsonSetter
    private void setClassAsString(String classAsString) {
        this.classAsString = classAsString;
    }

    @JsonGetter
    private byte[] getSerializedModel() {
        return serializedModel;
    }

    @JsonSetter
    private void setSerializedModel(byte[] serializedModel) {
        this.serializedModel = serializedModel;
    }

    @JsonGetter
    private String getDescription() {
        return description;
    }

    @JsonSetter
    private void setDescription(String description) {
        this.description = description;
    }

    @JsonGetter
    private String getInputColumn() {
        return inputColumn;
    }

    @JsonSetter
    private void setInputColumn(String inputColumn) {
        this.inputColumn = inputColumn;
    }

    @JsonGetter
    private String getOutputColumn() {
        return outputColumn;
    }

    @JsonSetter
    private void setOutputColumn(String outputColumn) {
        this.outputColumn = outputColumn;
    }

    @JsonGetter
    private boolean isClassifier() {
        return isClassifier;
    }

    @JsonSetter
    private void setClassifier(boolean classifier) {
        isClassifier = classifier;
    }

    @JsonGetter
    private boolean isFeaturizer() {
        return isFeaturizer;
    }

    @JsonSetter
    private void setFeaturizer(boolean featurizer) {
        isFeaturizer = featurizer;
    }

    @JsonGetter
    public Map<String, String> getParameters() {
        return parameters;
    }

    @JsonSetter
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
