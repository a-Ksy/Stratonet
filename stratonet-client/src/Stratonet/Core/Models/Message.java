package Stratonet.Core.Models;

import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;

import java.io.UnsupportedEncodingException;

public class Message {
    private RequestPhase requestPhase;

    private RequestType requestType;

    private int size;

    private String payload;

    private byte[] payloadAsByteArray;

    private String token;

    public Message() {
    }

    public Message(RequestPhase requestPhase, RequestType requestType, String payload) {
        this.requestPhase = requestPhase;
        this.requestType = requestType;
        this.payload = payload.trim();
        this.token = null;
        try {
            this.size = payload.getBytes("UTF-8").length + 2;
        } catch (UnsupportedEncodingException ex) {
        }
    }

    public Message(RequestPhase requestPhase, RequestType requestType, byte[] payloadAsByteArray) {
        this.requestPhase = requestPhase;
        this.requestType = requestType;
        this.payloadAsByteArray = payloadAsByteArray;
        this.token = null;
        this.size = payloadAsByteArray.length;
    }

    public Message(RequestPhase requestPhase, RequestType requestType, String payload, String token) {
        this.requestPhase = requestPhase;
        this.requestType = requestType;
        this.payload = payload.trim();
        this.token = token;
        try {
            this.size = payload.getBytes("UTF-8").length + 2;
        } catch (UnsupportedEncodingException ex) {
        }
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public byte[] getPayloadAsByteArray() {
        return payloadAsByteArray;
    }

    public void setPayloadAsByteArray(byte[] payloadAsByteArray) {
        this.payloadAsByteArray = payloadAsByteArray;
    }
}
