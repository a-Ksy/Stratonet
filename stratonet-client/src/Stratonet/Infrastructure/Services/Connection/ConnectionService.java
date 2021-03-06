package Stratonet.Infrastructure.Services.Connection;

import Stratonet.Core.Services.Connection.IConnectionService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionService implements IConnectionService {
    public static final String DEFAULT_SERVER_ADDRESS = "localhost";
    public static final int DEFAULT_SERVER_PORT = 4444;
    protected BufferedReader is;
    protected PrintWriter os;
    protected String serverAddress;
    protected int serverPort;
    private Socket s;

    public ConnectionService(String address, int port) {
        serverAddress = address;
        serverPort = port;
    }

    @Override
    public void Connect() {
        try {
            s = new Socket(serverAddress, serverPort);
            //br= new BufferedReader(new InputStreamReader(System.in));
            /*
            Read and write buffers on the socket
             */
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = new PrintWriter(s.getOutputStream());

            System.out.println("Successfully connected to " + serverAddress + " on port " + serverPort);
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("Error: no server has been found on " + serverAddress + "/" + serverPort);
        }
    }

    @Override
    public void Disconnect() {
        try {
            is.close();
            os.close();
            //br.close();
            s.close();
            System.out.println("ConnectionToServer. SendForAnswer. Connection Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String SendForAnswer(String message) {
        String response = "";
        try {
            /*
            Sends the message to the server via PrintWriter
             */
            os.println(message);
            os.flush();
            /*
            Reads a line from the server via Buffer Reader
             */
            response = is.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ConnectionToServer. SendForAnswer. Socket read Error");
        }
        return response;
    }
}
