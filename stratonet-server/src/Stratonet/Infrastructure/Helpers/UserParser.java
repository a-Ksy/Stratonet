package Stratonet.Infrastructure.Helpers;

import Stratonet.Core.Entities.User;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.Users;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.*;
import java.util.logging.Level;

public class UserParser
{
    private StratonetLogger logger;

    public UserParser()
    {
        logger = StratonetLogger.getInstance();
    }

    public ArrayList<User> ParseUsersFromFile()
    {
        ArrayList<User> userList = new ArrayList();
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            File jsonFile = Paths.get("save/users.json").toFile();

            Users users = mapper.readValue(jsonFile, Users.class);
            userList = users.users;
        }
        catch (FileNotFoundException ex)
        {
            logger.log(Level.SEVERE, "Exception while reading JSON file: " + ex);
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Exception while reading JSON file: " + ex);
        }
        return userList;
    }
}
