package Stratonet.Infrastructure.Services.Authentication;

import Stratonet.Core.Entities.User;
import Stratonet.Core.Services.Authentication.IAuthenticationService;
import Stratonet.Infrastructure.Services.User.UserService;

public class AuthenticationService implements IAuthenticationService
{
    private final String SUPER_SECRET_HASH_VALUE = "69";
    private final int TOKEN_LENGTH = 6;

    @Override
    public String GenerateToken(User user)
    {
        String tokenAsString = user.getUsername() + SUPER_SECRET_HASH_VALUE;

        int token = Math.abs(tokenAsString.hashCode());

        return String.valueOf(token).substring(0,TOKEN_LENGTH);
    }

    @Override
    public boolean ValidateToken(String token)
    {
        if (token == null || token == "")
        {
            return false;
        }

        User user = UserService.getInstance().GetUserByToken(token);
        if (user == null)
        {
            return false;
        }

        return true;
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

    @Override
    public boolean CheckTokenExists(User user)
    {
        if (user.getSession() != null && user.getSession().getToken() != null)
        {
            return true;
        }

        return false;
    }
}
