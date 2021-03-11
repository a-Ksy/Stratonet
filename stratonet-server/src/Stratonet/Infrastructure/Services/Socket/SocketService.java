package Stratonet.Infrastructure.Services.Socket;

import Stratonet.Core.Enums.ServiceType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Services.Socket.ISocketService;
import Stratonet.Infrastructure.Threads.AuthenticationThread.AuthenticationThread;
import Stratonet.Infrastructure.Threads.QueryThread.QueryThread;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class SocketService extends Thread implements ISocketService
{
    private StratonetLogger logger;
    private ServerSocket serverSocket;
    private ServiceType serviceType;

    public SocketService(int port, ServiceType serviceType)
    {
        logger = StratonetLogger.getInstance();
        this.serviceType = serviceType;

        try
        {
            serverSocket = new ServerSocket(port);
            logger.log(Level.INFO, "Opened up a "+ serviceType + " server socket on: " + Inet4Address.getLocalHost() + " on port " + port);
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Exception while opening a server socket: " + ex);
        }

    }

    public void run()
    {
        ListenAndAccept();
    }

    private void ListenAndAccept()
    {
        while (true)
        {
            Socket socket;
            // ToDo: Implement a more abstract structure for serviceType
            try {
                if (serviceType == ServiceType.AUTH) {
                    socket = serverSocket.accept();
                    logger.log(Level.INFO, "AUTH: A connection was established with the client: " + socket.getRemoteSocketAddress());

                    AuthenticationThread authenticationThread = new AuthenticationThread(socket);
                    authenticationThread.start();
                    continue;
                } else if (serviceType == ServiceType.QUERY) {
                    socket = serverSocket.accept();
                    logger.log(Level.INFO, "QUERY: A connection was established with the client: " + socket.getRemoteSocketAddress());

                    QueryThread queryThread = new QueryThread(socket);
                    queryThread.start();
                    continue;
                }

            }
            catch (Exception ex)
            {
                logger.log(Level.SEVERE, "Exception while establishing connection with the client: " + ex);
            }
        }
    }
}
