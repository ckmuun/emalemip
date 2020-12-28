package de.koware.flowersmodelserve.api.documentApi;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import de.koware.flowersmodelserve.api.BaseEntity;
import de.koware.flowersmodelserve.api.correlation.Correlatable;
import de.koware.flowersmodelserve.api.SerializedValueContainer;

import java.util.UUID;

public class DocumentEntity extends BaseEntity implements SerializedValueContainer, Correlatable {

    private DocumentContainer container;
    private UUID pGoalId;
    private UUID correlationKey;

    public DocumentEntity(DocumentContainer container,
                          UUID pGoalId
    ) {
        this.container = container;
        this.pGoalId = pGoalId;
        this.correlationKey = UUID.randomUUID();
    }

    public DocumentContainer getContainer() {
        return container;
    }

    @Override
    @JsonGetter
    public byte[] getBytes() {
        return this.container.getBytes();
    }

    @JsonGetter
    public UUID getCorrelationKey() {
        return this.correlationKey;
    }


    @JsonGetter
    public UUID getpGoalId() {
        return pGoalId;
    }

    @JsonSetter
    private void setContainer(DocumentContainer container) {
        this.container = container;
    }

    @JsonSetter
    private void setpGoalId(UUID pGoalId) {
        this.pGoalId = pGoalId;
    }

    @JsonSetter
    private void setCorrelationKey(UUID correlationKey) {
        this.correlationKey = correlationKey;
    }
}
