package Stratonet.Infrastructure.Services.Startup;

import Stratonet.Core.Services.Socket.ISocketService;
import Stratonet.Infrastructure.Data.Repositories.UserRepository.UserRepository;
import Stratonet.Infrastructure.Services.Socket.SocketService;

public class StartupService
{
    private final int DEFAULT_SERVER_PORT = 4444;
    private ISocketService socketService;
    private UserRepository userRepository;

    public StartupService()
    {
        userRepository = UserRepository.getInstance();
        socketService = new SocketService(DEFAULT_SERVER_PORT);
    }
}
