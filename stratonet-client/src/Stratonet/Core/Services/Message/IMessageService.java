package Stratonet.Core.Services.Message;

import Stratonet.Core.Models.Message;

import java.io.IOException;

public interface IMessageService
{
    void SendMessage(Message message);

    Message RetrieveMessage();
}

