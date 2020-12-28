package de.koware.flowersmodelserve.api.functionalClassification;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
public class TikaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TikaService.class);
    private final Tika tika;

    public TikaService() {
        this.tika = new Tika();
    }


    public String getMimeType(InputStream inputStream) {
        try {
            return this.tika.detect(inputStream);


        } catch (IOException e) {
            LOGGER.error("Tika could not detect mime type: " + Arrays.toString(e.getStackTrace()));
            return "";
        }
    }
}
