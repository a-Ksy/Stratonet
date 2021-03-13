package Stratonet.Infrastructure.Utils;

import Stratonet.Core.Helpers.StratonetLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class HashValidator {
    public static boolean ValidateJSONHash(String hash, Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(o);
            int jsonHash = json.hashCode();

            if (jsonHash == Integer.valueOf(hash)) {
                StratonetLogger.getInstance().log(Level.INFO, "Hash is valid");
                return true;
            }
        } catch (JsonProcessingException ex) {
        }

        StratonetLogger.getInstance().log(Level.INFO, "Hash is not valid");
        return false;
    }


    public static boolean ValidateImageHash(String hash, String fileName) {
        try {
            File fi = new File("save/" + fileName);
            byte[] imageAsByteArray = Files.readAllBytes(fi.toPath());

            Checksum checksum = new Adler32();
            checksum.update(imageAsByteArray, 0, imageAsByteArray.length);
            long hashedImage = checksum.getValue();

            if (hash.equals(String.valueOf(hashedImage))) {
                StratonetLogger.getInstance().log(Level.INFO, "Hash is valid");
                return true;
            }

        } catch (IOException ex) {
        }

        StratonetLogger.getInstance().log(Level.INFO, "Hash is not valid");
        return false;
    }
}
