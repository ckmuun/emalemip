package de.koware.flowersmodelserve.unit;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class TesseractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TesseractTest.class);

    @Test
    public void testTesseractLocally() throws TesseractException {
        LOGGER.info("performing tesseract / tess4j tests ont the local machine without spark");

        Tesseract tessy = new Tesseract();

        tessy.setDatapath("src/main/resources/tessdata");

        String text = tessy.doOCR(new File("/home/cornelius/SoftwareProjects/fichte/fichte-model-serve/src/test/resources/jobBulletinPdfs/1605436304178-layout.pdf"));

        LOGGER.info("text: {}", text);
    }
}
