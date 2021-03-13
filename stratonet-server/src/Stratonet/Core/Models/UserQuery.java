package Stratonet.Core.Models;

import Stratonet.Core.Entities.User;
import Stratonet.Core.Enums.APIType;

public class UserQuery
{
    private String token;

    private Object object;

    private APIType apiType;

    public UserQuery(String token, Object object, APIType apiType)
    {
        this.token = token;
        this.object = object;
        this.apiType = apiType;
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

    public APIType getApiType() {
        return apiType;
    }

    public void setApiType(APIType apiType) {
        this.apiType = apiType;
    }
}
