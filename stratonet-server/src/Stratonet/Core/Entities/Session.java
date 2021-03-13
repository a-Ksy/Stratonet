package Stratonet.Core.Entities;

import java.net.InetAddress;

public class Session {
    private String token;

    private InetAddress inetAddress;

    private int port;

    public Session(String token, InetAddress inetAddress, int port) {
        this.token = token;
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
