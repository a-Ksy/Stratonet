package Stratonet.Core.Entities;

public class Session
{
    public String token;

    public String connection;

    public Session(String token, String connection)
    {
        this.token = token;
        this.connection = connection;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }
}
