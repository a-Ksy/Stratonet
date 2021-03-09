package Stratonet.Infrastructure.Services.Startup;

import Stratonet.Core.Services.Socket.ISocketService;
import Stratonet.Core.Services.Startup.IStartupService;
import Stratonet.Infrastructure.Services.Socket.SocketService;

public class StartupService implements IStartupService
{
    private ISocketService socketService;

    private final String DEFAULT_SERVER_ADDRESS = "localhost";
    private final int DEFAULT_SERVER_PORT = 4444;

    public StartupService()
    {
        socketService = new SocketService(DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT);
        socketService.Connect();
    }
}
