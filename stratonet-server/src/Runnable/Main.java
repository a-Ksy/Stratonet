package Runnable;

import Stratonet.Infrastructure.Services.Socket.SocketService;

public class Main {

    public static void main(String[] args) {
        SocketService socketService = new SocketService(SocketService.DEFAULT_SERVER_PORT);
    }
}
