package Stratonet.Core.Services.Authentication;

import Stratonet.Core.Entities.User;

public interface IAuthenticationService
{
    public String GenerateToken(User user);

    public boolean ValidateToken();

    public boolean ValidateUsername(String username);

    public boolean ValidatePassword(User user, String password);

    public User GetUser(String username);

    public void ModifyUser(User user);
}
