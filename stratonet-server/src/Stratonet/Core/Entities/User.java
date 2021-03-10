package Stratonet.Core.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User
{
    @JsonProperty("username")
    public String username;

    @JsonProperty("password")
    public String password;

    public Session session = null;

    public User() {}

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
