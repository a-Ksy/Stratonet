package Stratonet.Infrastructure.Utils;

import Stratonet.Core.Helpers.StratonetLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Level;

public class HashValidator
{
    public static boolean ValidateJSONHash(String hash, Object o)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            String json = objectMapper.writeValueAsString(o);
            int jsonHash = json.hashCode();

            if (jsonHash == Integer.valueOf(hash))
            {
                StratonetLogger.getInstance().log(Level.INFO, "Hash is valid");
                return true;
            }
        }
        catch (JsonProcessingException ex)
        {
        }

        StratonetLogger.getInstance().log(Level.INFO, "Hash is not valid");
        return false;
    }
}
