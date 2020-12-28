package de.koware.flowersmodelserve.api.sparkDs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import de.koware.flowersmodelserve.api.BaseEntity;
import de.koware.flowersmodelserve.api.correlation.Correlatable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.UUID;

public class SparkDatasetEntity extends BaseEntity implements Correlatable {


    private String[] sparkDsJson;

    private UUID correlationKey;

    @JsonCreator
    private SparkDatasetEntity() {

    }

    public SparkDatasetEntity(
            Dataset<Row> dataset,
            UUID correlationKey
    ) {
        this.correlationKey = correlationKey;
        this.sparkDsJson = ((String[]) dataset.toJSON().collect());
    }

    @JsonGetter
    public String[] getSparkDsJson() {
        return sparkDsJson;
    }

    @JsonSetter
    private void setSparkDsJson(String[] sparkDsJson) {
        this.sparkDsJson = sparkDsJson;
    }


    @JsonSetter
    public void setCorrelationKey(UUID correlationKey) {
        this.correlationKey = correlationKey;
    }

    @Override
    @JsonGetter
    public UUID getCorrelationKey() {
        return this.correlationKey;
    }
}
