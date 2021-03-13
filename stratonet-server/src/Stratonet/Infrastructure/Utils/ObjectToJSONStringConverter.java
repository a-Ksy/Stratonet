package Stratonet.Infrastructure.Utils;

import Stratonet.Core.Helpers.StratonetLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Level;

public class ObjectToJSONStringConverter {
    public static String Convert(Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(o);
            return json;
        } catch (JsonProcessingException ex) {
            StratonetLogger.getInstance().log(Level.WARNING, "Failed to convert object to string.");
        }

        return null;
    }
}
