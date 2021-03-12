package Stratonet.Core.Models;

import Stratonet.Core.Entities.User;

public class UserQuery
{
    private String token;

    private Object object;

    public UserQuery(String token, Object object)
    {
        this.token = token;
        this.object = object;
    }

    public String getToken() {
        return token;
    }

    public void setToken(User user) {
        this.token = token;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
