package Stratonet.Infrastructure.Services.Socket;

import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Services.Authentication.IAuthenticationService;
import Stratonet.Core.Services.Socket.ISocketService;
import Stratonet.Infrastructure.Services.Authentication.AuthenticationService;
import Stratonet.Infrastructure.Services.Query.QueryService;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

public class SocketService implements ISocketService
{
    private StratonetLogger logger;
    private Socket socket;
    private String address;
    private int port;

    public SocketService(String address, int port)
    {
        logger = StratonetLogger.getInstance();
        this.address = address;
        this.port = port;
    }

    public void Connect()
    {
        try
        {
            // Authentication Socket
            socket = new Socket(address, port);
            logger.log(Level.INFO, "AUTH: Successfully connected to " + address + " on port " + port);

            AuthenticationService authenticationService = new AuthenticationService(socket);
            authenticationService.RunAuthentication();

            if (AuthenticationService.GetToken() == null)
            {
                return;
            }

            // Query Socket
            socket = new Socket(address, port);
            logger.log(Level.INFO, "QUERY: Successfully connected to " + address + " on port " + port);
            QueryService queryService = new QueryService(socket);
            queryService.RunQuery();

        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Exception while connecting to " + address + " on port " + port + ": " + ex);
        }
    }
}
