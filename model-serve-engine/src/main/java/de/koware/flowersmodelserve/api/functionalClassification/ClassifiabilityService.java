package de.koware.flowersmodelserve.api.functionalClassification;

import de.koware.flowersmodelserve.api.documentApi.DocumentContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ClassifiabilityService {

    /*
        TODO right now this is very basic. Classifyability may not only be determined via mimetype but also content-wise
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifiabilityService.class);
    private final Set<String> supportedMimes;
    private final TikaService tikaService;

    @Autowired
    public ClassifiabilityService(Set<String> supportedMimes, TikaService tikaService) {

        this.supportedMimes = supportedMimes;
        this.tikaService = tikaService;
    }


    public boolean isProcessable(DocumentContainer documentContainer) {
        return this.isSupportedMimeType(documentContainer);
    }


    private boolean isSupportedMimeType(DocumentContainer container) {

        String mimeOfDoc = tikaService.getMimeType(container.getInputStream());
        LOGGER.info("checking type {}", mimeOfDoc + " if it is supported");
        // explicit handling of "null equivalent"
        if (mimeOfDoc.equals("application/octet-stream")) {
            LOGGER.info("detected an unlabeled octet-stream");
            return false;
        }


        return true;
    }
}
