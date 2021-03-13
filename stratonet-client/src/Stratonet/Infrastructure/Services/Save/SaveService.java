package Stratonet.Infrastructure.Services.Save;

import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Services.Save.ISaveService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class SaveService implements ISaveService
{
    private StratonetLogger logger;

    public SaveService()
    {
        logger = StratonetLogger.getInstance();
    }

    public void SaveObjectAsJSON(Object object, String fileName)
    {
        ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            objectMapper.writeValue(new File("save/" + fileName), object);
            logger.log(Level.INFO, "JSON file is saved");
        }
        catch (IOException ex)
        {
            logger.log(Level.WARNING, "Exception while saving JSON file");
        }
    }
}
