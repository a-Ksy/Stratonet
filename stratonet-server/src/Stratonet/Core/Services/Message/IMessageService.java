package Stratonet.Core.Services.Message;

import Stratonet.Core.Models.Message;

import java.io.IOException;

public interface IMessageService {
    void SendMessage(Message message) throws IOException;

    Message RetrieveMessage(boolean hasToken) throws IOException;
}
