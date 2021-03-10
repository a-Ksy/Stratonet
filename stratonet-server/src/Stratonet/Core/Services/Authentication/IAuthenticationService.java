package Stratonet.Core.Services.Authentication;

import Stratonet.Core.Entities.User;

public interface IAuthenticationService
{
    public String GenerateToken(User user);

    public boolean ValidateToken(String token);

    public boolean ValidateUsername(String username);

    public boolean ValidatePassword(User user, String password);
}
