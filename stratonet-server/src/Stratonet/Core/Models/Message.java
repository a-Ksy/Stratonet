package Stratonet.Core.Models;

import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;

import java.io.UnsupportedEncodingException;

public class Message
{
    public RequestPhase requestPhase;

    public RequestType requestType;

    public int size;

    public String payload;

    public Message(){}

    public Message(RequestPhase requestPhase, RequestType requestType, String payload)
    {
        this.requestPhase = requestPhase;
        this.requestType = requestType;
        this.payload = payload.trim();
        try
        {
            this.size = payload.getBytes("UTF-8").length + 2;
        }
        catch (UnsupportedEncodingException ex) {}
    }

    public RequestPhase getRequestPhase() {
        return requestPhase;
    }

    public void setRequestPhase(RequestPhase requestPhase) {
        this.requestPhase = requestPhase;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
