package Stratonet.Infrastructure.Services.Socket;

import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Services.Socket.ISocketService;
import Stratonet.Infrastructure.Threads.AuthenticationThread.AuthenticationThread;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class SocketService implements ISocketService
{
    private StratonetLogger logger;
    private ServerSocket serverSocket;

    public SocketService(int port)
    {
        logger = StratonetLogger.getInstance();

        try
        {
            serverSocket = new ServerSocket(port);
            logger.log(Level.INFO, "Opened up a server socket on: " + Inet4Address.getLocalHost());
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Exception while opening a server socket: " + ex);
        }

        ListenAndAccept();
    }

    private void ListenAndAccept()
    {
        while (true)
        {
            Socket socket;
            try
            {
                // Authentication Socket
                socket = serverSocket.accept();
                logger.log(Level.INFO,"AUTH: A connection was established with the client: " + socket.getRemoteSocketAddress());

                AuthenticationThread authenticationThread = new AuthenticationThread(socket);
                authenticationThread.start();

                // Query Socket
                socket = serverSocket.accept();
                logger.log(Level.INFO,"QUERY: A connection was established with the client: " + socket.getRemoteSocketAddress());
            }
            catch (Exception ex)
            {
                logger.log(Level.SEVERE, "Exception while establishing connection with the client: " + ex);
            }
        }
    }
}
