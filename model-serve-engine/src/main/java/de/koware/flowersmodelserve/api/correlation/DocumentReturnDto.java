package de.koware.flowersmodelserve.api.correlation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import de.koware.flowersmodelserve.api.BaseReturnDto;
import de.koware.flowersmodelserve.api.SerializedValueContainer;

import java.util.UUID;

public class DocumentReturnDto extends BaseReturnDto implements SerializedValueContainer {

    private byte[] documentBytes;
    private UUID pGoalId;
    private UUID correlationKey;


    @JsonCreator
    private DocumentReturnDto() {
        super();
    }


    public DocumentReturnDto(
            byte[] documentBytes,
            UUID pGoalId,
            UUID correlationKey
    ) {
        this.correlationKey = correlationKey;
        this.pGoalId = pGoalId;
        this.documentBytes = documentBytes;
    }

    @Override
    @JsonGetter
    public byte[] getBytes() {
        return this.documentBytes;
    }


    @JsonGetter
    public byte[] getDocumentBytes() {
        return documentBytes;
    }

    @JsonSetter
    private void setDocumentBytes(byte[] documentBytes) {
        this.documentBytes = documentBytes;
    }

    @JsonGetter
    public UUID getpGoalId() {
        return pGoalId;
    }

    @JsonSetter
    private void setpGoalId(UUID pGoalId) {
        this.pGoalId = pGoalId;
    }

    @JsonGetter
    public UUID getCorrelationKey() {
        return correlationKey;
    }

    @JsonSetter
    private void setCorrelationKey(UUID correlationKey) {
        this.correlationKey = correlationKey;
    }
}
