package de.koware.flowersmodelserve.api.pipelineApi;


import de.koware.flowersmodelserve.api.BaseEntity;

import java.util.UUID;

public class PipelineGoalEntity extends BaseEntity {

    private final UUID[] idsOfModelsUsed;
    private final String description;
    private final String pipelineDisplayName;
    private final UUID documentLabelsId;


    public PipelineGoalEntity(
            UUID[] idsOfModelsUsed,
            String description,
            String pipelineDisplayName, UUID documentLabelsId) {
        this.idsOfModelsUsed = idsOfModelsUsed;
        this.pipelineDisplayName = pipelineDisplayName;
        this.documentLabelsId = documentLabelsId;

        if (null != description) {
            this.description = description;
            return;
        }
        this.description = "";
    }

    public UUID[] getIdsOfModelsUsed() {
        return idsOfModelsUsed;
    }

    public String getDescription() {
        return description;
    }

    public String getPipelineDisplayName() {
        return pipelineDisplayName;
    }

    public UUID getDocumentLabelsId() {
        return documentLabelsId;
    }
}
