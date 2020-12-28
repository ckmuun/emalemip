package de.koware.flowersmodelserve.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Document
public abstract class BaseEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseEntity.class);

    /*
        note that these fields should technically be final, but
        as we're using JSON serialization with MongoDB, we use prive @JsonSetter
     */

    @Id
    private UUID id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private double version;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @JsonCreator
    private BaseEntity(UUID id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @JsonSetter
    private void setId(UUID id) {
        this.id = id;
    }

    @JsonSetter
    private void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void update() {
        this.updatedAt = LocalDateTime.now();
    }


    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        // wrapped in catch block due to application server classloading weirdnesses
        try {
            if (other.getClass() != this.getClass()) {
                return false;
            }
            BaseEntity ohterEntity = (BaseEntity) other;

            if (this.id == ohterEntity.id) {
                return true;
            }
        } catch (ClassCastException cce) {
            LOGGER.warn("class cast exception while performing equality check on entity" + Arrays.toString(cce.getStackTrace()));
        }
        return false;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }
}
