package de.koware.flowersmodelserve.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.UUID;

public class SparkDatasetReturnDto extends BaseReturnDto {


    private String[] sparkDsJson;


    @JsonCreator
    private SparkDatasetReturnDto() {

    }

    public SparkDatasetReturnDto(UUID id,
                                 String[] sparkDsJson
    ) {
        super(id);
        this.sparkDsJson = sparkDsJson;
    }

    @JsonGetter
    public String[] getSparkDsJson() {
        return sparkDsJson;
    }

    @JsonSetter
    public void setSparkDsJson(String[] sparkDsJson) {
        this.sparkDsJson = sparkDsJson;
    }
}
