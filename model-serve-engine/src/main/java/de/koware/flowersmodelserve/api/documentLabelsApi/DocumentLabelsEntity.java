package de.koware.flowersmodelserve.api.documentLabelsApi;

import de.koware.flowersmodelserve.api.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
public class DocumentLabelsEntity extends BaseEntity {


    private final Map<String, Double> stringLabel2NumericLabelMapping;
    private final String displayName;

    public DocumentLabelsEntity(
            Map<String, Double> stringLabel2NumericLabelMapping,
            String displayName) {
        this.stringLabel2NumericLabelMapping = stringLabel2NumericLabelMapping;
        this.displayName = displayName;
    }

        public Collection<Double> getNumericRepOfStringLabels() {
        return this.stringLabel2NumericLabelMapping.values();
    }


    public Map<String, Double> getLabelName2NumericRep() {
        return this.stringLabel2NumericLabelMapping;
    }

    public Set<String> getLabelNames() {
        return this.stringLabel2NumericLabelMapping.keySet();
    }

    public Map<String, Double> getStringLabel2NumericLabelMapping() {
        return stringLabel2NumericLabelMapping;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Map<Double, String> getNumericReps2StringLabels() {
        Map<Double, String> inverse = new HashMap<Double, String>(this.stringLabel2NumericLabelMapping.size());

        this.stringLabel2NumericLabelMapping.forEach((label, numeric) -> inverse.put(numeric, label));

        return inverse;
    }
}
