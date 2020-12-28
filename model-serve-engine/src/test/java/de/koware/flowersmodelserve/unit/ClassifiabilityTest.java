package de.koware.flowersmodelserve.unit;

import de.koware.flowersmodelserve.api.functionalClassification.ClassifiabilityService;
import de.koware.flowersmodelserve.api.documentApi.DocumentContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
public class ClassifiabilityTest {

    @Autowired
    private ClassifiabilityService classifiabilityService;

    @Test
    public void testImageClassifiability() throws IOException {
        FileInputStream fis = new FileInputStream(new File("src/test/resources/tikaTestDocs/testImage.jpg"));


        DocumentContainer container = new DocumentContainer(
                StreamUtils.copyToByteArray(fis),
                "a file.jpg",
                "application/jpeg"
        );

        assert classifiabilityService.isProcessable(container);

    }
}
