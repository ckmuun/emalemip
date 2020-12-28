package de.koware.flowersmodelserve.api.correlation;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.koware.flowersmodelserve.api.SparkDatasetReturnDto;
import de.koware.flowersmodelserve.api.functionalClassification.ClassificationResultReturnDto;


public class CorrelationReturnDto {


    private DocumentReturnDto docReturnDto;
    private ClassificationResultReturnDto creDto;
    private SparkDatasetReturnDto sparkDatasetReturnDto;


    @JsonCreator
    private CorrelationReturnDto() {
    }


    public CorrelationReturnDto(DocumentReturnDto docReturnDto,
                                ClassificationResultReturnDto creDto,
                                SparkDatasetReturnDto sparkDatasetReturnDto

    ) {
        this.sparkDatasetReturnDto = sparkDatasetReturnDto;
        this.docReturnDto = docReturnDto;
        this.creDto = creDto;
    }

    public DocumentReturnDto getDocReturnDto() {
        return docReturnDto;
    }

    public void setDocReturnDto(DocumentReturnDto docReturnDto) {
        this.docReturnDto = docReturnDto;
    }

    public ClassificationResultReturnDto getCreDto() {
        return creDto;
    }

    public void setCreDto(ClassificationResultReturnDto creDto) {
        this.creDto = creDto;
    }

    public SparkDatasetReturnDto getSparkDatasetReturnDto() {
        return sparkDatasetReturnDto;
    }

    public void setSparkDatasetReturnDto(SparkDatasetReturnDto sparkDatasetReturnDto) {
        this.sparkDatasetReturnDto = sparkDatasetReturnDto;
    }
}
