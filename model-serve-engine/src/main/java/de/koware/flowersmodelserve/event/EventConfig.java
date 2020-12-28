package de.koware.flowersmodelserve.event;

import de.koware.flowersmodelserve.api.documentApi.IncomingDocumentDto;
import de.koware.flowersmodelserve.api.documentLabelsApi.DocumentLabelsEntity;
import de.koware.flowersmodelserve.api.pipelineApi.PipelineGoalEntity;
import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class EventConfig {

    @Bean
    public Map<Class<?>, String> classToKafkaTopic() {

        /*
            TODO outsource this to external configuration, add proper pre and suffixes
         */

        HashMap<Class<?>, String> classToKafkaTopic = new HashMap<>();

        classToKafkaTopic.put(IncomingDocumentDto.class, "new-document-entity-persisted");
        classToKafkaTopic.put(SparkModelEntity.class, "new-classifying-model-persisted");
        classToKafkaTopic.put(DocumentLabelsEntity.class, "new-document-labels-entity-persisted");
        classToKafkaTopic.put(PipelineGoalEntity.class, "new-procedual-goal-persisted");


        return classToKafkaTopic;
    }

}
