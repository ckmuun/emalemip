package de.koware.flowersmodelserve.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ClassifiabilityConfig {


    @Bean
    public Set<String> supportedMimeTypes( ) {

        HashSet<String> supportedMimeTypes = new HashSet<>();
        supportedMimeTypes.add("image/jpg");
        supportedMimeTypes.add("image/png");
        supportedMimeTypes.add("image/jpeg");

        return supportedMimeTypes;
    }
}

