package Stratonet.Infrastructure.Services.Startup;

import Stratonet.Core.Enums.ServiceType;
import Stratonet.Core.Services.Socket.ISocketService;
import Stratonet.Core.Services.Startup.IStartupService;
import Stratonet.Infrastructure.Services.Authentication.AuthenticationService;
import Stratonet.Infrastructure.Services.Socket.SocketService;

public class StartupService implements IStartupService
{
    private ISocketService socketService;

    private final String DEFAULT_SERVER_ADDRESS = "localhost";
    private final int AUTH_PORT = 4444;
    private final int QUERY_PORT = 4445;

    public StartupService()
    {
        socketService = new SocketService(DEFAULT_SERVER_ADDRESS, AUTH_PORT, ServiceType.AUTH);
        socketService.Connect();
        if (AuthenticationService.GetToken() != null)
        {
            socketService = new SocketService(DEFAULT_SERVER_ADDRESS, QUERY_PORT, ServiceType.QUERY);
            socketService.Connect();
        }
    }
}
