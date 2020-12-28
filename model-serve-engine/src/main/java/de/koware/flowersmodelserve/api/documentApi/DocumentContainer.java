package de.koware.flowersmodelserve.api.documentApi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.koware.flowersmodelserve.api.SerializedValueContainer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DocumentContainer implements SerializedValueContainer {


    private byte[] documentBytes;
    private String filename;
    private String mimeContentType;


    public DocumentContainer(byte[] documentBytes, String filename, String mimeContentType) {
        this.documentBytes = documentBytes;
        this.filename = filename;
        this.mimeContentType = mimeContentType;
    }

    public byte[] getBytes() {
        return documentBytes;
    }

    public void setDocumentBytes(byte[] documentBytes) {
        this.documentBytes = documentBytes;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeContentType() {
        return mimeContentType;
    }

    public void setMimeContentType(String mimeContentType) {
        this.mimeContentType = mimeContentType;
    }

    @JsonIgnore
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.documentBytes);
    }
}
