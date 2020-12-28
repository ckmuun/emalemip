package de.koware.flowersmodelserve.api.documentApi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.UUID;

public class IncomingDocumentDto {

    private UUID pGoalId;
    private byte[] documentBytes;
    private String filename;
    private String mimeContentType;

    @JsonCreator
    private IncomingDocumentDto() {

    }

    public IncomingDocumentDto(DocumentContainer documentContainer, UUID pGoalId) {
        this.documentBytes = documentContainer.getBytes();
        this.filename = documentContainer.getFilename();
        this.mimeContentType = documentContainer.getMimeContentType();
        this.pGoalId = pGoalId;
    }

    @JsonIgnore
    public DocumentContainer getDocumentContainer() {
        return new DocumentContainer(
                this.documentBytes,
                this.filename,
                this.mimeContentType
        );
    }

    @JsonGetter
    public UUID getpGoalId() {
        return pGoalId;
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
    public String getFilename() {
        return filename;
    }

    @JsonSetter
    private void setFilename(String filename) {
        this.filename = filename;
    }

    @JsonGetter
    public String getMimeContentType() {
        return mimeContentType;
    }

    @JsonSetter
    private void setMimeContentType(String mimeContentType) {
        this.mimeContentType = mimeContentType;
    }

    @JsonSetter
    private void setpGoalId(UUID pGoalId) {
        this.pGoalId = pGoalId;
    }
}
