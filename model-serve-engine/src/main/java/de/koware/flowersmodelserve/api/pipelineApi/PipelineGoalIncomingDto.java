package de.koware.flowersmodelserve.api.pipelineApi;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.koware.flowersmodelserve.api.BaseIncomingDto;

import java.util.UUID;

public class PipelineGoalIncomingDto extends BaseIncomingDto {

    private String description;
    private String pipelineDisplayName;
    private UUID[] modelIds;
    private UUID documentLabelsId;

    @JsonCreator
    private PipelineGoalIncomingDto() {

    }

    public PipelineGoalIncomingDto(String description,
                                   String pipelineDisplayName,
                                   UUID[] modelIds,
                                   UUID documentLabelsId) {
        this.description = description;
        this.pipelineDisplayName = pipelineDisplayName;
        this.modelIds = modelIds;
        this.documentLabelsId = documentLabelsId;
    }

    public PipelineGoalIncomingDto(String description, String pipelineDisplayName, UUID[] modelIds) {
        this.description = description;
        this.pipelineDisplayName = pipelineDisplayName;
        this.modelIds = modelIds;
        // for transformation-only pGoal that do not contain a classification step.
        this.documentLabelsId = UUID.randomUUID();
    }

    public String getDescription() {
        return description;
    }

    public String getPipelineDisplayName() {
        return pipelineDisplayName;
    }

    public UUID[] getModelIds() {
        return modelIds;
    }

    public UUID getDocumentLabelsId() {
        // some pipelines are not used for label-based classification, just insert something
        // technically, this should be an optional, but this will suffice.
        if (null == this.documentLabelsId) {
            this.documentLabelsId = UUID.randomUUID();
        }
        return documentLabelsId;
    }
}
