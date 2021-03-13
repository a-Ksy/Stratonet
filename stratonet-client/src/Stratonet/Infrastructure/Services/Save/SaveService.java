package Stratonet.Infrastructure.Services.Save;

import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Services.Save.ISaveService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
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
            logger.log(Level.INFO, "Successfully saved the JSON file");
        }
        catch (IOException ex)
        {
            logger.log(Level.WARNING, "Exception while saving JSON file");
        }
    }

    public void SaveImageFromByteArray(byte[] response, String fileName)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream("save/" + fileName);
            fos.write(response);
            fos.close();
            logger.log(Level.INFO, "Successfully saved the Image file");
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "Exception while saving Image file");
        }
    }

    public void DeleteImage(String fileName)
    {
        File image = new File("save/" + fileName);
        image.delete();
    }
}
