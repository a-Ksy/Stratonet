package Stratonet.Infrastructure.Services.Startup;

import Stratonet.Core.Enums.ServiceType;
import Stratonet.Infrastructure.Data.Repositories.UserRepository.UserRepository;
import Stratonet.Infrastructure.Services.Socket.SocketService;

public class StartupService
{
    private final int AUTH_PORT = 4444;
    private final int QUERY_PORT = 4445;
    private SocketService authService;
    private SocketService queryService;
    private UserRepository userRepository;

    public StartupService()
    {
        userRepository = UserRepository.getInstance();

        authService = new SocketService(AUTH_PORT, ServiceType.AUTH);
        queryService = new SocketService(QUERY_PORT, ServiceType.QUERY);
        authService.start();
        queryService.start();
    }
}