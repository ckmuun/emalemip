package de.koware.flowersmodelserve.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mongo.ReactiveMongoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoDbConfig {


    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbConfig.class);

    @Bean
    public MongoClient mongoClient() {
        LOGGER.info("creating mongo db client");
        return MongoClients.create("mongodb://localhost:27017/fichtedb");
    }



}
