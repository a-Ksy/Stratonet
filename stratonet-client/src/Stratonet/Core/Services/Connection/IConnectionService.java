package Stratonet.Core.Services.Connection;

public interface IConnectionService {

    void Connect();

    void Disconnect();

    String SendForAnswer(String message);
}
