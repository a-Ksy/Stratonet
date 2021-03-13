package Stratonet.Core.Services.User;

import Stratonet.Core.Entities.User;

public interface IUserService {
    User GetUserByUsername(String username);

    User GetUserByToken(String token);

    void ModifyUser(User user);

    User GetUser(String username);

    void ResetUserSession(User user);
}
