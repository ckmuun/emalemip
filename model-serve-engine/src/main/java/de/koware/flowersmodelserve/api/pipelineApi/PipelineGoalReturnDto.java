package de.koware.flowersmodelserve.api.pipelineApi;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import de.koware.flowersmodelserve.api.BaseReturnDto;
import org.apache.spark.ml.PipelineStage;

import java.util.LinkedHashMap;
import java.util.UUID;

/*
    This class represents a concept similar to Spark ML's Pipeline.
    However, firstly, it's a DTO and more importantly, it serves as an entrypoint for
    a management client handling business processes.
    It does serve as a (main) entrypoint for a model training client service.

 */
public class PipelineGoalReturnDto extends BaseReturnDto {

    private String description;
    private String pipelineDisplayName;
    private UUID docLabelId;


    /*
        using just the IDs to save storage space and enforce that models need to exist in storage
         the idea behind this is that this DTO / Entity is used to combine existing models into some domain-specific
         workflow operation
     */

    private final UUID[] idsOfModelsUsed;

    public PipelineGoalReturnDto(UUID[] idsOfModelsUsed,
                                 String description,
                                 String pipelineDisplayName,
                                 UUID id,
                                 UUID docLabelId
    ) {
        super(id);
        this.description = description;
        this.idsOfModelsUsed = idsOfModelsUsed;
        this.pipelineDisplayName = pipelineDisplayName;
        this.docLabelId = docLabelId;
    }


    @JsonSetter
    public void setPipelineDisplayName(String pipelineDisplayName) {
        this.pipelineDisplayName = pipelineDisplayName;
    }

    @JsonGetter
    public String getDescription() {
        return description;
    }

    @JsonGetter
    public String getPipelineDisplayName() {
        return pipelineDisplayName;
    }


    @JsonGetter
    public UUID getDocLabelId() {
        return docLabelId;
    }

    @com.fasterxml.jackson.annotation.JsonSetter
    private void setDocLabelId(UUID docLabelId) {
        this.docLabelId = docLabelId;
    }

    @JsonGetter
    public UUID[] getIdsOfModelsUsed() {
        return idsOfModelsUsed;
    }
}
