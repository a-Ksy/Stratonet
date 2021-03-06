package Stratonet.Infrastructure.Services.Socket;

import Stratonet.Core.Services.Socket.ISocketService;
import Stratonet.Infrastructure.Threads.ServerThread;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService implements ISocketService {

    public static final int DEFAULT_SERVER_PORT = 4444;
    private final ServerSocket serverSocket;

    public SocketService(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Oppened up a server socket on " + Inet4Address.getLocalHost());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Server class.Constructor exception on oppening a server socket");
        }
        while (true) {
            ListenAndAccept();
        }
    }

    private void ListenAndAccept() {
        Socket s;
        try {
            s = serverSocket.accept();
            System.out.println("A connection was established with a client on the address of " + s.getRemoteSocketAddress());
            ServerThread st = new ServerThread(s);
            st.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Server Class.Connection establishment error inside listen and accept function");
        }
    }
}
