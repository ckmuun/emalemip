package de.koware.flowersmodelserve.api.functionalClassification;

import de.koware.flowersmodelserve.api.sparkModelApi.SparkModelEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.UUID;

/*
    This class serves for a background task of the api.
    Spark models are returned from the persistence layer in no particular order.
    But we need the order specified in the PipelineGoal ("pGoal") for the pipeline to work.
    this class takes the array UUID[] and orders the SparkModels
 */
public class PGoalBasedSparkEtyComparator<SparkModelEntityParam> implements Comparator<SparkModelEntityParam> {


    private static final Logger LOGGER = LoggerFactory.getLogger(PGoalBasedSparkEtyComparator.class);
    private UUID[] order;

    public void setOrder(UUID[] order) {
        this.order = order;
    }


    @Override
    public int compare(SparkModelEntityParam t0, SparkModelEntityParam t1) {
        LOGGER.info("comparing spark model entities");

        SparkModelEntity entity = (SparkModelEntity) t0;
        SparkModelEntity entity1 = (SparkModelEntity) t1;

        if (entity.getId().equals(entity1.getId())) {
            return 0;
        }

        for (UUID id : order) {

            if (entity.getId().equals(id)) {
                return -1;
            }
            if (entity1.getId().equals(id)) {
                return 1;
            }
        }
        return 0;
    }
}
