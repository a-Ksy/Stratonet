package Stratonet.Core.Repositories.UserRepository;
import Stratonet.Core.Entities.User;
import java.util.ArrayList;

public interface IUserRepository
{
    ArrayList<User> GetUsers();

    void ModifyUser(User user);

    User GetUserByUsername(String username);
}
