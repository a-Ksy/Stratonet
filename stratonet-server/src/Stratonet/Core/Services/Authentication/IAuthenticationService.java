package Stratonet.Core.Services.Authentication;

import Stratonet.Core.Entities.User;

public interface IAuthenticationService {
    String GenerateToken(User user);

    boolean ValidateToken(String token);

    boolean ValidateUsername(String username);

    boolean ValidatePassword(User user, String password);

    boolean CheckTokenExists(User user);
}
