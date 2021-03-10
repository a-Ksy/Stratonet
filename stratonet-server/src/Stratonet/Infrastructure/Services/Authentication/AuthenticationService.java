package Stratonet.Infrastructure.Services.Authentication;

import Stratonet.Core.Entities.User;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Repositories.UserRepository.IUserRepository;
import Stratonet.Core.Services.Authentication.IAuthenticationService;
import Stratonet.Infrastructure.Data.Repositories.UserRepository.UserRepository;

public class AuthenticationService implements IAuthenticationService
{
    private StratonetLogger logger;
    private IUserRepository userRepository;

    public AuthenticationService()
    {
        logger = StratonetLogger.getInstance();
        userRepository = UserRepository.getInstance();
    }

    @Override
    public String GenerateToken(User user)
    {
        return null;
    }

    @Override
    public boolean ValidateToken()
    {
        return false;
    }

    @Override
    public boolean ValidateUsername(String username)
    {
        User user = userRepository.GetUserByUsername(username);
        if (user == null)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean ValidatePassword()
    {
        return false;
    }

    @Override
    public User GetUser(String username)
    {
       return userRepository.GetUserByUsername(username);
    }

    @Override
    public void ModifyUser(User user)
    {
        userRepository.ModifyUser(user);
    }
}
