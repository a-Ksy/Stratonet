package Stratonet.Infrastructure.Data.Repositories.UserRepository;

import Stratonet.Core.Entities.User;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Repositories.UserRepository.IUserRepository;
import Stratonet.Infrastructure.Helpers.UserParser;

import java.util.ArrayList;

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

    @Override
    public ArrayList<User> GetUsers()
    {
        return users;
    }

    @Override
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

    @Override
    public User GetUserByUsername(String username) {
        for (User u : users)
        {
            if (u.username.equals(username))
            {
                return u;
            }
        }
        return null;
    }

    @Override
    public User GetUserByToken(String token) {
        for (User u : users)
        {
            if (u.getSession().getToken().equals(token))
            {
                return u;
            }
        }
        return null;
    }

}
