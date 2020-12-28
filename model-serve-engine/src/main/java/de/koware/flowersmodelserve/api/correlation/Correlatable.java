package de.koware.flowersmodelserve.api.correlation;

import java.util.UUID;

public interface Correlatable {

    public UUID getCorrelationKey();
}
