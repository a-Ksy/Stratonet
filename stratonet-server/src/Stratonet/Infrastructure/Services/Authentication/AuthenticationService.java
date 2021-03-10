package Stratonet.Infrastructure.Services.Authentication;

import Stratonet.Core.Entities.User;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Services.Authentication.IAuthenticationService;
import Stratonet.Infrastructure.Services.User.UserService;

public class AuthenticationService implements IAuthenticationService
{
    private StratonetLogger logger;
    private final String SUPER_SECRET_HASH_VALUE = "69";
    private final int TOKEN_LENGTH = 6;

    public AuthenticationService()
    {
        logger = StratonetLogger.getInstance();
    }

    @Override
    public String GenerateToken(User user)
    {
        String tokenAsString = user.getUsername() + SUPER_SECRET_HASH_VALUE;

        int token = Math.abs(tokenAsString.hashCode());

        return String.valueOf(token).substring(0,TOKEN_LENGTH);
    }

    @Override
    public boolean ValidateToken(User user, String token)
    {
        if (user.getSession().getToken().equals(token))
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean ValidateUsername(String username)
    {
        User user = UserService.getInstance().GetUserByUsername(username);
        if (user == null)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean ValidatePassword(User user, String password)
    {
        if (user.getPassword().equals(password))
        {
            return true;
        }

        return false;
    }
}
