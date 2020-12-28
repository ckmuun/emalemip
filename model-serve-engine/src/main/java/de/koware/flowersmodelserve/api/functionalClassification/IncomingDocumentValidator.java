package de.koware.flowersmodelserve.api.functionalClassification;

import de.koware.flowersmodelserve.api.documentApi.IncomingDocumentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class IncomingDocumentValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncomingDocumentValidator.class);

    public boolean isProcessable(IncomingDocumentDto iDto) {
        LOGGER.info("validating incoming document dto");
        try {

            if (null == iDto.getDocumentContainer()) {
                LOGGER.info("Document Container is null");
                return false;
            }
            if (250 >= iDto.getDocumentContainer().getBytes().length) {
                LOGGER.info("length of byte array container below 250 -- deciding it's junk");
                return false;
            }

            LOGGER.info("creating input stream");
            ByteArrayInputStream bazi = new ByteArrayInputStream(iDto.getDocumentContainer().getBytes());
            if(iDto.getDocumentContainer().getMimeContentType().contains("image")) {
                BufferedImage image = ImageIO.read(iDto.getDocumentContainer().getInputStream());

                // image probing via create graphics. If this fails, the image is not processabble.
                image.createGraphics();
            }


            LOGGER.info("probing read");
            int probe = bazi.read();


        } catch (IOException ioe) {
            LOGGER.info("IO Exception during validation -> document can not be processed");
            return false;

        } catch (Exception e) {
            LOGGER.info("exception occured during validation, document can not be processed");
            return false;
        }

        return true;
    }


    public Mono<IncomingDocumentDto> validateIncomingDto(IncomingDocumentDto iDto) {


        return Mono.just(iDto);
    }
}
