package Stratonet.Infrastructure.Services.User;

import Stratonet.Core.Entities.User;
import Stratonet.Core.Services.User.IUserService;
import Stratonet.Infrastructure.Data.Repositories.UserRepository.UserRepository;

public class UserService implements IUserService {
    private static UserService userService;

    private UserService() {
    }

    public static UserService getInstance() {
        if (userService == null) {
            userService = new UserService();
        }

        return userService;
    }

    @Override
    public User GetUserByUsername(String username) {
        return UserRepository.getInstance().GetUserByUsername(username);
    }

    @Override
    public User GetUserByToken(String token) {
        return UserRepository.getInstance().GetUserByToken(token);
    }

    @Override
    public void ModifyUser(User user) {
        UserRepository.getInstance().ModifyUser(user);
    }

    @Override
    public User GetUser(String username) {
        return UserRepository.getInstance().GetUserByUsername(username);
    }

    @Override
    public void ResetUserSession(User user) {
        user.setSession(null);
        ModifyUser(user);
    }

}
