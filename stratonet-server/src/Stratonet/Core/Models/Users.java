package Stratonet.Core.Models;

import Stratonet.Core.Entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Users {
    @JsonProperty("users")
    public ArrayList<User> users;
}
