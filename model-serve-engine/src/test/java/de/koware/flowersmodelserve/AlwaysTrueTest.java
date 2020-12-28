package de.koware.flowersmodelserve;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class AlwaysTrueTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlwaysTrueTest.class);


    @Test
    public void alwaysTrue() {
        LOGGER.info("Running an always true test to smoke test the environment");

        assert true;
    }



    @Test
    public void reactorErrorTest() throws Exception {

        StepVerifier.create(AlwaysTrueTest.exceptionThrower())
                .expectError(Exception.class)
                .verify();
    }

    private static Mono<Object> exceptionThrower() throws Exception {

            return Mono.error(new Exception());
    }
}
