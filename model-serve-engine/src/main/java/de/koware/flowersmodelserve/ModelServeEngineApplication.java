package de.koware.flowersmodelserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class ModelServeEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModelServeEngineApplication.class, args);
    }


}
