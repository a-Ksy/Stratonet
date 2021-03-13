package Stratonet.Infrastructure.Services.Startup;

import Stratonet.Core.Enums.ServiceType;
import Stratonet.Core.Models.UserQuery;
import Stratonet.Infrastructure.Data.Repositories.UserRepository.UserRepository;
import Stratonet.Infrastructure.Services.Socket.SocketService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class StartupService
{
    private final int AUTH_PORT = 4444;
    private final int QUERY_PORT = 4445;
    private final int FILE_PORT = 4446;
    private SocketService authSocketService;
    private SocketService querySocketService;
    private SocketService fileSocketService;
    private UserRepository userRepository;

    public StartupService()
    {
        BlockingQueue<UserQuery> queue = new ArrayBlockingQueue<>(10);
        userRepository = UserRepository.getInstance();

        authSocketService = new SocketService(AUTH_PORT, ServiceType.AUTH);
        querySocketService = new SocketService(QUERY_PORT, ServiceType.QUERY, queue);
        fileSocketService = new SocketService(FILE_PORT, ServiceType.FILE, queue);

        authSocketService.start();
        querySocketService.start();
        fileSocketService.start();
    }
}