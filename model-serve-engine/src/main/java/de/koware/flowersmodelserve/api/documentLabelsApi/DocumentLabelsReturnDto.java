package de.koware.flowersmodelserve.api.documentLabelsApi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.koware.flowersmodelserve.api.BaseReturnDto;
import org.codehaus.jackson.annotate.JsonSetter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DocumentLabelsReturnDto extends BaseReturnDto {

    private Map<String, Double> stringLabel2NumericLabelMapping;

    private String labelName;

    @JsonCreator
    private DocumentLabelsReturnDto() {
        super();
    }

    public DocumentLabelsReturnDto(
            String labelName,
            Map<String, Double> stringLabel2NumericLabelMapping,
            UUID uuid
    ) {
        super(uuid);
        this.labelName = labelName;
        this.stringLabel2NumericLabelMapping = stringLabel2NumericLabelMapping;
    }

    public Map<String, Double> getStringLabel2NumericLabelMapping() {
        return stringLabel2NumericLabelMapping;
    }

    @JsonSetter
    private void setStringLabel2NumericLabelMapping(HashMap<String, Double> stringLabel2NumericLabelMapping) {
        this.stringLabel2NumericLabelMapping = stringLabel2NumericLabelMapping;
    }

    @JsonGetter
    public String getLabelName() {
        return labelName;
    }

    @JsonSetter
    private void setLabelName(String labelName) {
        this.labelName = labelName;
    }


    @JsonIgnore
    public Collection<String> getLabelsAsString() {
        return this.stringLabel2NumericLabelMapping.keySet();
    }
}
