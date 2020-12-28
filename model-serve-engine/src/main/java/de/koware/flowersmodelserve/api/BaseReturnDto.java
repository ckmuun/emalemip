package de.koware.flowersmodelserve.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.UUID;

public abstract class BaseReturnDto extends BaseDto {

    private UUID id;


    public BaseReturnDto(UUID id) {
        this.id = id;
    }


    @JsonCreator
    public BaseReturnDto() {

    }


    @JsonSetter
    private void setId(UUID id) {
        this.id = id;
    }

    @JsonGetter
    public UUID getId() {
        return this.id;
    }
}
