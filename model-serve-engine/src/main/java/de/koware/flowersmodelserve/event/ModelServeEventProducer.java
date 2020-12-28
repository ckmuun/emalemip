package de.koware.flowersmodelserve.event;

import de.koware.flowersmodelserve.api.BaseEntity;
import reactor.core.publisher.Mono;

public interface ModelServeEventProducer {


    Mono<Void> publishEntityPersisted(BaseEntity entity);


}
