package Stratonet.Core.Models;

import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;

public class Message
{
    public RequestPhase requestPhase;

    public RequestType requestType;

    public int size;

    public String payload;

    public Message(RequestPhase requestPhase, RequestType requestType, int size, String payload)
    {
        this.requestPhase = requestPhase;
        this.requestType = requestType;
        this.size = size;
        this.payload = payload;
    }
}
