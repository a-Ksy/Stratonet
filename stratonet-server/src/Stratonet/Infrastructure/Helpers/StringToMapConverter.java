package Stratonet.Infrastructure.Helpers;

import Stratonet.Core.Helpers.StratonetLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;

public class StringToMapConverter extends ObjectMapper
{
    public Map<String, String> Convert(String content)
    {
        try
        {
            return this.readValue(content, new TypeReference<>(){});
        }
        catch (IOException ex)
        {
            StratonetLogger.getInstance().log(Level.SEVERE, "Exception occured while converting content to map");
            throw new CompletionException(ex);
        }
    }
}
