package Stratonet.Core.Entities;

import java.net.SocketAddress;

public class Session
{
    public String token;

    public SocketAddress socketAddress;

    public Session(String token, SocketAddress connection)
    {
        this.token = token;
        this.socketAddress = connection;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SocketAddress getConnection() {
        return socketAddress;
    }

    public void setConnection(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }
}
