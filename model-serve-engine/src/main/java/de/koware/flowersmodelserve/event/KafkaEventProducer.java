package de.koware.flowersmodelserve.event;

import de.koware.flowersmodelserve.api.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class KafkaEventProducer implements ModelServeEventProducer {

    private static  final Logger LOGGER = LoggerFactory.getLogger(KafkaEventProducer.class);

    private final Map<Class<?>, String> class2KafkaTopic;

    @Autowired
    public KafkaEventProducer(Map<Class<?>, String> class2KafkaTopic) {
        this.class2KafkaTopic = class2KafkaTopic;
    }

    public Mono<Void> publishObjectPersistence(BaseEntity entity, String topic) {
        LOGGER.info("publishing entity creation event to kafka");
        // TODO implement this...

        return null;
    }

    @Override
    public Mono<Void> publishEntityPersisted(BaseEntity entity) {
        return null;
    }

}
