package Stratonet.Core.Services.Message;

import Stratonet.Core.Models.Message;

public interface IMessageService {
    void SendMessage(Message message);

    Message RetrieveMessage(boolean payloadIsByteArray);
}

