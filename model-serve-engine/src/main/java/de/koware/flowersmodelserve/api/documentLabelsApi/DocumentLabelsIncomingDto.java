package de.koware.flowersmodelserve.api.documentLabelsApi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import de.koware.flowersmodelserve.api.BaseDto;
import org.codehaus.jackson.annotate.JsonSetter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DocumentLabelsIncomingDto extends BaseDto {

    private Map<String, Double> stringLabel2NumericLabelMapping;
    private String labelName;


    @JsonCreator
    private DocumentLabelsIncomingDto() {
        super();
    }

    public DocumentLabelsIncomingDto(
            String labelName,
            Map<String, Double> stringLabel2NumericLabelMapping
    ) {
        super();
        this.labelName = labelName;
        this.stringLabel2NumericLabelMapping = stringLabel2NumericLabelMapping;
    }

    @JsonGetter
    public Map<String, Double> getStringLabel2NumericLabelMapping() {
        return stringLabel2NumericLabelMapping;
    }

    @JsonSetter
    private void setStringLabel2NumericLabelMapping(Map<String, Double> stringLabel2NumericLabelMapping) {
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


}
