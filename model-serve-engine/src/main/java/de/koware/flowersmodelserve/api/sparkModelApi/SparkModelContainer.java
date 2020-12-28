package de.koware.flowersmodelserve.api.sparkModelApi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import de.koware.flowersmodelserve.api.SerializedValueContainer;

import java.util.Map;

public class SparkModelContainer implements SerializedValueContainer {

    private String classAsString;
    private byte[] serializedModel;
    private String description;
    private String inputColumn;
    private String outputColumn;
    private boolean isClassifier;
    private boolean isFeaturizer;

    private Map<String, String> parameters;

    @JsonCreator
    private SparkModelContainer() {

    }

    public SparkModelContainer(
            byte[] serializedModel,
            String classAsString,
            String description,
            String inputColumn,
            String outputColumn,
            Map<String, String> parameters,
            boolean isClassifier
    ) {
        this.serializedModel = serializedModel;
        this.classAsString = classAsString;
        this.description = description;
        this.inputColumn = inputColumn;
        this.outputColumn = outputColumn;
        this.parameters = parameters;
        this.isClassifier = isClassifier;


        if (parameters.isEmpty()) {
            parameters.put("inputCol", this.inputColumn);
            parameters.put("outputCol", this.outputColumn);
        }
    }

    public String getInputColumn() {
        return inputColumn;
    }

    public String getOutputColumn() {
        return outputColumn;
    }

    public boolean isClassifier() {
        return isClassifier;
    }

    public boolean isFeaturizer() {
        return isFeaturizer;
    }


    public Map<String, String> getParameters() {
        return parameters;
    }

    @JsonGetter
    public byte[] getBytes() {
        return serializedModel;
    }

    @JsonSetter
    private void setSerializedModel(byte[] serializedModel) {
        this.serializedModel = serializedModel;
    }

    @JsonGetter
    public String getDescription() {
        return description;
    }

    @JsonSetter
    private void setDescription(String description) {
        this.description = description;
    }

    @JsonGetter
    public String getClassAsString() {
        return classAsString;
    }

    @JsonSetter
    private void setClassAsString(String classAsString) {
        this.classAsString = classAsString;
    }

    @JsonGetter
    public byte[] getSerializedModel() {
        return serializedModel;
    }

    @JsonSetter
    public void setInputColumn(String inputColumn) {
        this.inputColumn = inputColumn;
    }

    @JsonSetter
    public void setOutputColumn(String outputColumn) {
        this.outputColumn = outputColumn;
    }

    @JsonSetter
    public void setClassifier(boolean classifier) {
        isClassifier = classifier;
    }

    @JsonSetter
    public void setFeaturizer(boolean featurizer) {
        isFeaturizer = featurizer;
    }


    @JsonSetter
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
