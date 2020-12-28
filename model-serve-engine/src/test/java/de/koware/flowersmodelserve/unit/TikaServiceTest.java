package de.koware.flowersmodelserve.unit;


import de.koware.flowersmodelserve.api.functionalClassification.TikaService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest(classes = TikaService.class)
public class TikaServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TikaServiceTest.class);

    @Autowired
    private TikaService tikaService;

    @Test
    public void testTikaMimeJpg() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(new File("src/test/resources/tikaTestDocs/testImage.jpg"));
        String mime = tikaService.getMimeType(fis);
        LOGGER.info("detected mime: " + mime);
        assert mime.contains("image");
        assert mime.equals("image/jpeg");
    }

    @Test
    public void testTikaMimeJpgAsPng() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(new File("src/test/resources/tikaTestDocs/testImageJpgAsPng.png"));
        String mime = tikaService.getMimeType(fis);
        LOGGER.info("detected mime: " + mime);
        assert mime.contains("image");
        assert mime.equals("image/jpeg");
    }

    @Test
    public void testTikaMimePdf() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(new File("src/test/resources/tikaTestDocs/testPdf.pdf"));
        String mime = tikaService.getMimeType(fis);
        LOGGER.info("detected mime: " + mime);
        assert mime.contains("application");
        assert mime.contains("pdf");
        assert mime.equals("application/pdf");

    }

    @Test
    public void testTikaMimePng() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(new File("src/test/resources/tikaTestDocs/testPngImage.png"));
        String mime = tikaService.getMimeType(fis);
        LOGGER.info("detected mime: " + mime);
        assert mime.contains("image");
        assert mime.equals("image/png");

    }

    @Test
    public void testTikaMimeTiff() {
        LOGGER.info("tiff support not implemented yet");
        // TODO implement Tiff support... (Low priority)
    }
}
