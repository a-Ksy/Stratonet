package Stratonet.Infrastructure.Data.Repositories.UserRepository;

import Stratonet.Core.Entities.User;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Repositories.UserRepository.IUserRepository;
import Stratonet.Infrastructure.Helpers.UserParser;

import java.util.ArrayList;
import java.util.logging.Level;

public class UserRepository implements IUserRepository
{
    private StratonetLogger logger;
    private static UserRepository userRepository;
    private UserParser userParser;
    private ArrayList<User> users;

    private UserRepository()
    {
        logger = StratonetLogger.getInstance();
        userParser = new UserParser();
        InitializeRepository();
    }

    public static UserRepository getInstance()
    {
        if (userRepository == null)
        {
            userRepository = new UserRepository();
        }

        return userRepository;
    }

    public void InitializeRepository()
    {
        users = userParser.ParseUsersFromFile();
    }

    public ArrayList<User> GetUsers()
    {
        return users;
    }

    public void ModifyUser(User user)
    {
        for (User u : users)
        {
            if (user.username == u.username)
            {
                u = user;
            }
        }
    }
}
