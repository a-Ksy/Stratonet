package Runnable;

import Stratonet.Infrastructure.Services.Connection.ConnectionService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ConnectionService connectionToServer = new ConnectionService(ConnectionService.DEFAULT_SERVER_ADDRESS, ConnectionService.DEFAULT_SERVER_PORT);
        connectionToServer.Connect();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a message for the echo");
        String message = scanner.nextLine();
        while (!message.equals("QUIT"))
        {
            System.out.println("Response from server: " + connectionToServer.SendForAnswer(message));
            message = scanner.nextLine();
        }
        connectionToServer.Disconnect();
    }
}
