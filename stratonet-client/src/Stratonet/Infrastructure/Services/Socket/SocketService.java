package Stratonet.Infrastructure.Services.Socket;

import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Services.Authentication.IAuthenticationService;
import Stratonet.Core.Services.Socket.ISocketService;
import Stratonet.Infrastructure.Services.Authentication.AuthenticationService;

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
            socket = new Socket(address, port);
            logger.log(Level.INFO, "Successfully connected to " + address + " on port " + port);
            IAuthenticationService authenticationService = new AuthenticationService(socket);
            authenticationService.RunAuthentication();
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Exception while connecting to " + address + " on port " + port + ": " + ex);
        }
    }
}
