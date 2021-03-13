package Stratonet.Infrastructure.Services.Socket;

import Stratonet.Core.Enums.ServiceType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.UserQuery;
import Stratonet.Core.Services.Socket.ISocketService;
import Stratonet.Infrastructure.Threads.AuthenticationThread.AuthenticationThread;
import Stratonet.Infrastructure.Threads.FileThread.FileThread;
import Stratonet.Infrastructure.Threads.QueryThread.QueryThread;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

public class SocketService extends Thread implements ISocketService {
    private StratonetLogger logger;
    private ServerSocket serverSocket;
    private ServiceType serviceType;
    private BlockingQueue<UserQuery> queue;

    public SocketService(int port, ServiceType serviceType) {
        logger = StratonetLogger.getInstance();
        this.serviceType = serviceType;

        try {
            serverSocket = new ServerSocket(port);
            logger.log(Level.INFO, "Opened up a " + serviceType + " server socket on: " + Inet4Address.getLocalHost() + " on port " + port);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Exception while opening a server socket: " + ex);
        }
    }

    public SocketService(int port, ServiceType serviceType, BlockingQueue<UserQuery> queue) {
        logger = StratonetLogger.getInstance();
        this.serviceType = serviceType;
        this.queue = queue;

        try {
            serverSocket = new ServerSocket(port);
            logger.log(Level.INFO, "Opened up a " + serviceType + " server socket on: " + Inet4Address.getLocalHost() + " on port " + port);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Exception while opening a server socket: " + ex);
        }
    }

    public void run() {
        ListenAndAccept();
    }

    private void ListenAndAccept() {
        while (true) {
            Socket socket;
            try {
                switch (serviceType) {

                    case AUTH:
                        socket = serverSocket.accept();
                        logger.log(Level.INFO, "AUTH: A connection was established with the client: " + socket.getRemoteSocketAddress());

                        AuthenticationThread authenticationThread = new AuthenticationThread(socket);
                        authenticationThread.start();
                        continue;

                    case QUERY:
                        socket = serverSocket.accept();
                        logger.log(Level.INFO, "QUERY: A connection was established with the client: " + socket.getRemoteSocketAddress());

                        QueryThread queryThread = new QueryThread(socket, queue);
                        queryThread.start();
                        continue;

                    case FILE:
                        socket = serverSocket.accept();
                        logger.log(Level.INFO, "FILE: A connection was established with the client: " + socket.getRemoteSocketAddress());

                        FileThread fileThread = new FileThread(socket, queue);
                        fileThread.start();
                        continue;
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Exception while establishing connection with the client: " + ex);
            }
        }
    }
}
